package org.cynic.spring_stuff.mapper;

import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.converter.MoneyConverter;
import org.cynic.spring_stuff.domain.entity.*;
import org.cynic.spring_stuff.domain.http.order.CreateOrderHttp;
import org.cynic.spring_stuff.domain.http.order.OrderDetailsHttp;
import org.cynic.spring_stuff.domain.http.order.OrderDetailsHttp.*;
import org.cynic.spring_stuff.domain.http.order.OrderHttp;
import org.cynic.spring_stuff.domain.http.order.OrderHttp.OrderOrganizationHttp;
import org.cynic.spring_stuff.domain.http.order.OrderHttp.OrderOwnerHttp;
import org.cynic.spring_stuff.domain.model.type.PriceType;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.Set;


@ExtendWith({
        MockitoExtension.class,
        InstancioExtension.class
})
@Tag("unit")
class OrderMapperTest {

    private OrderMapper mapper;
    @Mock
    private MoneyConverter moneyConverter;

    @BeforeEach
    void setUp() {
        this.mapper = new OrderMapperImpl();
        this.mapper.setMoneyService(moneyConverter);
    }

    @Test
    void toItemWhenOK() {
        Price priceCovered = Instancio.of(Price.class)
                .set(Select.field("covered"), Boolean.TRUE)
                .set(Select.field("type"), PriceType.INCOME)
                .create();
        Price priceNotCovered = Instancio.of(Price.class)
                .set(Select.field("covered"), Boolean.FALSE)
                .set(Select.field("type"), PriceType.SPENT)
                .create();
        Document document = Instancio.create(Document.class);

        ItemOrderPrice itemPriceNotCovered = Instancio.of(ItemOrderPrice.class)
                .set(Select.field("fraction"), BigDecimal.ONE)
                .set(Select.field("price"), priceNotCovered)
                .create();

        ItemOrderPrice orderPriceCovered = Instancio.of(ItemOrderPrice.class)
                .set(Select.field("fraction"), BigDecimal.ONE)
                .set(Select.field("price"), priceCovered)
                .create();

        Item item = Instancio.of(Item.class)
                .set(Select.field("itemOrderPrices"), Set.of(itemPriceNotCovered))
                .create();
        Order order = Instancio.of(Order.class)
                .set(Select.field("itemOrderPrices"), Set.of(orderPriceCovered))
                .set(Select.field("items"), Set.of(item))
                .set(Select.field("documents"), Set.of(document))
                .create();
        Currency currency = Instancio.create(Currency.class);
        BigDecimal coveredValue = Instancio.create(BigDecimal.class);
        BigDecimal notCoveredValue = Instancio.create(BigDecimal.class);

        Mockito.when(moneyConverter.convert(priceCovered, orderPriceCovered.getFraction(), currency)).
                thenReturn(coveredValue);
        Mockito.when(moneyConverter.convert(priceNotCovered, itemPriceNotCovered.getFraction(), currency)).
                thenReturn(notCoveredValue);

        OrderDetailsHttp expected = new OrderDetailsHttp(
                order.getId(),
                order.getDateTime().toZonedDateTime(),
                order.getName(),
                order.getDescription(),
                currency,
                order.getClosed(),
                new OrderDetailsManagerHttp(
                        order.getOwner().getName(),
                        order.getOwner().getPhone(),
                        order.getOwner().getEmail(),
                        new OrderDetailsOrganizationHttp(
                                order.getOwner().getOrganization().getName(),
                                order.getOwner().getOrganization().getEmail(),
                                order.getOwner().getOrganization().getPhone(),
                                order.getOwner().getOrganization().getAddress()
                        )
                ),
                new OrderDetailsManagerHttp(
                        order.getManager().getName(),
                        order.getManager().getPhone(),
                        order.getManager().getEmail(),
                        new OrderDetailsOrganizationHttp(
                                order.getManager().getOrganization().getName(),
                                order.getManager().getOrganization().getEmail(),
                                order.getManager().getOrganization().getPhone(),
                                order.getManager().getOrganization().getAddress()
                        )
                ),
                new OrderDetailsPriceHttp(
                        notCoveredValue, BigDecimal.ZERO
                ),
                new OrderDetailsPriceHttp(
                        coveredValue, coveredValue
                ),
                Set.of(
                        new OrderDetailsDocumentHttp(
                                document.getId(),
                                document.getFileName()
                        )
                ),
                Set.of(
                        new OrderDetailsItemHttp(
                                item.getId(),
                                item.getName()
                        )
                )
        );

        Assertions.<Optional<OrderDetailsHttp>>assertThat(mapper.toItem(order, currency))
                .extracting(Optional::get)
                .isEqualTo(expected);

        Mockito.verify(moneyConverter, Mockito.times(1)).convert(priceCovered, orderPriceCovered.getFraction(), currency);
        Mockito.verify(moneyConverter, Mockito.times(1)).convert(priceNotCovered, itemPriceNotCovered.getFraction(), currency);
    }


    @Test
    void toListItemWhenOK() {
        Price priceCovered = Instancio.of(Price.class)
                .set(Select.field("covered"), Boolean.TRUE)
                .set(Select.field("type"), PriceType.INCOME)
                .create();
        Price priceNotCovered = Instancio.of(Price.class)
                .set(Select.field("covered"), Boolean.FALSE)
                .set(Select.field("type"), PriceType.SPENT)
                .create();
        Document document = Instancio.create(Document.class);
        ItemOrderPrice itemPriceNotCovered = Instancio.of(ItemOrderPrice.class)
                .set(Select.field("fraction"), BigDecimal.ONE)
                .set(Select.field("price"), priceNotCovered)
                .create();
        Item item = Instancio.of(Item.class)
                .set(Select.field("itemOrderPrices"), Set.of(itemPriceNotCovered))
                .create();
        ItemOrderPrice orderPriceCovered = Instancio.of(ItemOrderPrice.class)
                .set(Select.field("fraction"), BigDecimal.ONE)
                .set(Select.field("price"), priceCovered)
                .create();
        Order order = Instancio.of(Order.class)
                .set(Select.field("itemOrderPrices"), Set.of(orderPriceCovered))
                .set(Select.field("items"), Set.of(item))
                .set(Select.field("documents"), Set.of(document))
                .create();

        OrderHttp expected = new OrderHttp(
                order.getId(),
                order.getName(),
                order.getClosed(),
                order.getDescription(),
                new OrderOwnerHttp(
                        order.getOwner().getName(),
                        order.getOwner().getPhone(),
                        order.getOwner().getEmail(),
                        new OrderOrganizationHttp(
                                order.getOwner().getOrganization().getName(),
                                order.getOwner().getOrganization().getEmail(),
                                order.getOwner().getOrganization().getPhone(),
                                order.getOwner().getOrganization().getAddress()
                        )
                )
        );

        Assertions.<Optional<OrderHttp>>assertThat(mapper.toListItem(order))
                .extracting(Optional::get)
                .isEqualTo(expected);
    }


    @Test
    void toEntityWhenOK() {
        CreateOrderHttp http = Instancio.create(CreateOrderHttp.class);
        Set<Item> items = Instancio.createSet(Item.class);
        Manager manager = Instancio.create(Manager.class);
        Manager owner = Instancio.create(Manager.class);

        Order expected = new Order();
        expected.setName(http.name());
        expected.setDescription(http.description());
        expected.setDateTime(http.dateTime().toOffsetDateTime());
        expected.setManager(manager);
        expected.setOwner(owner);
        expected.setItems(items);

        Assertions.<Optional<Order>>assertThat(mapper.toEntity(http, items, manager, owner))
                .extracting(Optional::get)
                .usingRecursiveComparison()
                .comparingOnlyFields(
                        "name",
                        "description",
                        "items",
                        "owner",
                        "manager",
                        "dateTime"
                )
                .isEqualTo(expected);
    }

    @Test
    void toEntity2WhenOK() {
        Order order = Instancio.create(Order.class);
        Boolean closed = Instancio.create(Boolean.class);

        Order expected = new Order();
        expected.setClosed(closed);

        Assertions.<Optional<Order>>assertThat(mapper.toEntity(order, closed))
                .extracting(Optional::get)
                .usingRecursiveComparison()
                .comparingOnlyFields(
                        "closed"
                )
                .isEqualTo(expected);
    }
}
