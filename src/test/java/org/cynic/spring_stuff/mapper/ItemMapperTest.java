package org.cynic.spring_stuff.mapper;

import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.entity.Document;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.http.item.CreateItemHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp.ItemDetailsDocumentHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp.ItemDetailsManagerHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp.ItemDetailsOrderHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp.ItemDetailsOrganizationHttp;
import org.cynic.spring_stuff.domain.http.item.ItemHttp;
import org.cynic.spring_stuff.domain.http.item.ItemHttp.ItemOrderHttp;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
@Tag("unit")
class ItemMapperTest {

    private ItemMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ItemMapperImpl();
    }

    @Test
    void toItemWhenOk() {
        Order orderClosed = Instancio.of(Order.class)
            .set(Select.field("closed"), true)
            .create();
        Order orderOpen = Instancio.of(Order.class)
            .set(Select.field("closed"), false)
            .create();

        Document document = Instancio.create(Document.class);
        Item item = Instancio.of(Item.class)
            .set(Select.field("orders"), Set.of(orderClosed, orderOpen))
            .set(Select.field("documents"), Set.of(document))
            .create();

        ItemDetailsHttp expected = new ItemDetailsHttp(
            item.getId(),
            item.getName(),
            item.getDescription(),
            Set.of(
                new ItemDetailsOrderHttp(orderOpen.getId(),
                    orderOpen.getDateTime().toZonedDateTime(),
                    orderOpen.getName(),
                    orderOpen.getDescription(),
                    new ItemDetailsManagerHttp(
                        orderOpen.getManager().getName(),
                        orderOpen.getManager().getPhone(),
                        orderOpen.getManager().getEmail(),
                        new ItemDetailsOrganizationHttp(
                            orderOpen.getManager().getOrganization().getName(),
                            orderOpen.getManager().getOrganization().getEmail(),
                            orderOpen.getManager().getOrganization().getPhone(),
                            orderOpen.getManager().getOrganization().getAddress()
                        )
                    ))
            ),
            Set.of(
                new ItemDetailsDocumentHttp(
                    document.getId(),
                    document.getFileName()
                )
            )
        );

        Assertions.<Optional<ItemDetailsHttp>>assertThat(mapper.toItem(item))
            .extracting(Optional::get)
            .isEqualTo(expected);
    }

    @Test
    void toListItemWhenOk() {
        Order order = Instancio.create(Order.class);
        Item item = Instancio.of(Item.class)
            .set(Select.field("orders"), Set.of(order))
            .create();

        ItemHttp expected = new ItemHttp(
            item.getId(),
            item.getName(),
            item.getDescription(),
            Set.of(
                new ItemOrderHttp(order.getId(),
                    order.getDateTime().toZonedDateTime(),
                    order.getName(),
                    order.getDescription()
                )
            )
        );

        Assertions.<Optional<ItemHttp>>assertThat(mapper.toListItem(item))
            .extracting(Optional::get)
            .isEqualTo(expected);
    }

    @Test
    void toEntityWhenOK() {
        Set<Order> orders = Instancio.createSet(Order.class);
        CreateItemHttp http = Instancio.create(CreateItemHttp.class);

        Item expected = new Item();
        expected.setName(http.name());
        expected.setDescription(http.description());
        expected.setOrders(orders);

        Assertions.<Optional<Item>>assertThat(mapper.toEntity(http, orders))
            .extracting(Optional::get)
            .usingRecursiveComparison()
            .comparingOnlyFields(
                "name",
                "description",
                "orders"
            )
            .isEqualTo(expected);
    }
}