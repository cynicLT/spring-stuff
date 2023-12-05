package org.cynic.spring_stuff.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.entity.Price;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp.CreatePriceFractionHttp;
import org.cynic.spring_stuff.domain.http.price.PriceDetailsHttp;
import org.cynic.spring_stuff.domain.http.price.PriceHttp;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;
import org.cynic.spring_stuff.mapper.ItemOrderPriceMapper;
import org.cynic.spring_stuff.mapper.PriceMapper;
import org.cynic.spring_stuff.repository.ItemOrderPriceRepository;
import org.cynic.spring_stuff.repository.ItemRepository;
import org.cynic.spring_stuff.repository.NotificationRepository;
import org.cynic.spring_stuff.repository.OrderRepository;
import org.cynic.spring_stuff.repository.PriceRepository;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;


@ExtendWith({
    InstancioExtension.class,
    MockitoExtension.class
})
@Tag("unit")
class PriceServiceTest {

    private PriceService priceService;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private PriceMapper priceMapper;

    @Mock
    private ItemOrderPriceRepository itemOrderPriceRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemOrderPriceMapper itemOrderPriceMapper;

    @BeforeEach
    void setUp() {
        this.priceService = new PriceService(priceRepository, itemOrderPriceRepository, notificationRepository, itemRepository, orderRepository, priceMapper,
            itemOrderPriceMapper);
    }

    @Test
    void pricesByWhenOK() {
        OidcUser oidcUser =
            new DefaultOidcUser(
                Instancio.createList(SimpleGrantedAuthority.class),
                new OidcIdToken(
                    Instancio.create(String.class),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().past())
                        .create(),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().future())
                        .create(),
                    Map.of("sub", "subject",
                        "iss", "http://localhost.com")
                )
            );
        Long orderId = Instancio.create(Long.class);
        Price price = Instancio.create(Price.class);
        PriceHttp http = Instancio.create(PriceHttp.class);

        Mockito.when(priceRepository.findAll(Mockito.any())).thenReturn(List.of(price));
        Mockito.when(priceMapper.toHttpList(price)).thenReturn(Optional.of(http));

        Assertions.assertThat(priceService.pricesBy(oidcUser, Optional.of(orderId)))
            .containsExactly(http);

        Mockito.verify(priceRepository, Mockito.times(1)).findAll(Mockito.any());
        Mockito.verify(priceMapper, Mockito.times(1)).toHttpList(price);
    }

    @Test
    void createWhenOkItems() {
        OidcUser oidcUser =
            new DefaultOidcUser(
                Instancio.createList(SimpleGrantedAuthority.class),
                new OidcIdToken(
                    Instancio.create(String.class),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().past())
                        .create(),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().future())
                        .create(),
                    Map.of("sub", "subject",
                        "iss", "http://localhost.com")
                )
            );
        CreatePriceFractionHttp fraction = Instancio.create(CreatePriceFractionHttp.class);
        CreatePriceHttp http = Instancio.of(CreatePriceHttp.class)
            .set(Select.field("referenceType"), ReferenceType.ITEM)
            .set(Select.field("fractions"), Set.of(fraction))
            .create();
        Price price = Instancio.create(Price.class);
        Item item = Instancio.create(Item.class);
        ItemOrderPrice itemOrderPrice = Instancio.create(ItemOrderPrice.class);

        Mockito.when(itemRepository.findById(fraction.referenceId()))
            .thenReturn(Optional.of(item));
        Mockito.when(itemOrderPriceMapper.toEntity(fraction, item))
            .thenReturn(Optional.of(itemOrderPrice));
        Mockito.when(priceMapper.toEntity(http, Set.of(itemOrderPrice))).thenReturn(Optional.of(price));
        Mockito.when(priceRepository.save(price)).thenReturn(price);

        Assertions.assertThat(priceService.create(http, oidcUser))
            .isEqualTo(price.getId());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(fraction.referenceId());
        Mockito.verify(itemOrderPriceMapper, Mockito.times(1)).toEntity(fraction, item);
        Mockito.verify(priceMapper, Mockito.times(1)).toEntity(http, Set.of(itemOrderPrice));
        Mockito.verify(priceRepository, Mockito.times(1)).save(price);
    }

    @Test
    void createWhenOkOrders() {
        OidcUser oidcUser =
            new DefaultOidcUser(
                Instancio.createList(SimpleGrantedAuthority.class),
                new OidcIdToken(
                    Instancio.create(String.class),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().past())
                        .create(),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().future())
                        .create(),
                    Map.of("sub", "subject",
                        "iss", "http://localhost.com")
                )
            );
        CreatePriceFractionHttp fraction = Instancio.create(CreatePriceFractionHttp.class);
        CreatePriceHttp http = Instancio.of(CreatePriceHttp.class)
            .set(Select.field("referenceType"), ReferenceType.ORDER)
            .set(Select.field("fractions"), Set.of(fraction))
            .create();
        Price price = Instancio.create(Price.class);
        Order order = Instancio.create(Order.class);
        ItemOrderPrice itemOrderPrice = Instancio.create(ItemOrderPrice.class);

        Mockito.when(orderRepository.findOne(Mockito.any()))
            .thenReturn(Optional.of(order));
        Mockito.when(itemOrderPriceMapper.toEntity(fraction, order))
            .thenReturn(Optional.of(itemOrderPrice));
        Mockito.when(priceMapper.toEntity(http, Set.of(itemOrderPrice))).thenReturn(Optional.of(price));
        Mockito.when(priceRepository.save(price)).thenReturn(price);

        Assertions.assertThat(priceService.create(http, oidcUser))
            .isEqualTo(price.getId());

        Mockito.verify(orderRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(itemOrderPriceMapper, Mockito.times(1)).toEntity(fraction, order);
        Mockito.verify(priceMapper, Mockito.times(1)).toEntity(http, Set.of(itemOrderPrice));
        Mockito.verify(priceRepository, Mockito.times(1)).save(price);
    }

    @Test
    void createWhenErrorSave() {
        OidcUser oidcUser =
            new DefaultOidcUser(
                Instancio.createList(SimpleGrantedAuthority.class),
                new OidcIdToken(
                    Instancio.create(String.class),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().past())
                        .create(),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().future())
                        .create(),
                    Map.of("sub", "subject",
                        "iss", "http://localhost.com")
                )
            );
        CreatePriceFractionHttp fraction = Instancio.create(CreatePriceFractionHttp.class);
        CreatePriceHttp http = Instancio.of(CreatePriceHttp.class)
            .set(Select.field("referenceType"), ReferenceType.ORDER)
            .set(Select.field("fractions"), Set.of(fraction))
            .create();
        Price price = Instancio.create(Price.class);
        Order order = Instancio.create(Order.class);
        ItemOrderPrice itemOrderPrice = Instancio.create(ItemOrderPrice.class);

        Mockito.when(orderRepository.findOne(Mockito.any()))
            .thenReturn(Optional.of(order));
        Mockito.when(itemOrderPriceMapper.toEntity(fraction, order))
            .thenReturn(Optional.of(itemOrderPrice));
        Mockito.when(priceMapper.toEntity(http, Set.of(itemOrderPrice))).thenReturn(Optional.of(price));

        Assertions.assertThatThrownBy(() -> priceService.create(http, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.price.save"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .isEmpty();

        Mockito.verify(orderRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(itemOrderPriceMapper, Mockito.times(1)).toEntity(fraction, order);
        Mockito.verify(priceMapper, Mockito.times(1)).toEntity(http, Set.of(itemOrderPrice));
        Mockito.verify(priceRepository, Mockito.times(1)).save(price);
    }

    @Test
    void createWhenErrorOrderNotFound() {
        OidcUser oidcUser =
            new DefaultOidcUser(
                Instancio.createList(SimpleGrantedAuthority.class),
                new OidcIdToken(
                    Instancio.create(String.class),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().past())
                        .create(),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().future())
                        .create(),
                    Map.of("sub", "subject",
                        "iss", "http://localhost.com")
                )
            );
        CreatePriceFractionHttp fraction = Instancio.create(CreatePriceFractionHttp.class);
        CreatePriceHttp http = Instancio.of(CreatePriceHttp.class)
            .set(Select.field("referenceType"), ReferenceType.ORDER)
            .set(Select.field("fractions"), Set.of(fraction))
            .create();

        Mockito.when(orderRepository.findOne(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> priceService.create(http, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.order.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(fraction.referenceId());

        Mockito.verify(orderRepository, Mockito.times(1)).findOne(Mockito.any());
    }


    @Test
    void createWhenErrorItemNotFound() {
        OidcUser oidcUser =
            new DefaultOidcUser(
                Instancio.createList(SimpleGrantedAuthority.class),
                new OidcIdToken(
                    Instancio.create(String.class),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().past())
                        .create(),
                    Instancio.of(Instant.class)
                        .generate(Select.root(), it -> it.temporal().instant().future())
                        .create(),
                    Map.of("sub", "subject",
                        "iss", "http://localhost.com")
                )
            );
        CreatePriceFractionHttp fraction = Instancio.create(CreatePriceFractionHttp.class);
        CreatePriceHttp http = Instancio.of(CreatePriceHttp.class)
            .set(Select.field("referenceType"), ReferenceType.ITEM)
            .set(Select.field("fractions"), Set.of(fraction))
            .create();

        Mockito.when(itemRepository.findById(fraction.referenceId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> priceService.create(http, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.item.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(fraction.referenceId());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(fraction.referenceId());
    }

    @Test
    void deleteWhenOK() {
        Long id = Instancio.create(Long.class);
        Price price = Instancio.create(Price.class);

        Mockito.when(priceRepository.findOne(Mockito.any())).thenReturn(Optional.of(price));

        priceService.deleteBy(id);

        Mockito.verify(priceRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(priceRepository, Mockito.times(1)).delete(price);

        price.getItemOrderPrices()
            .forEach(it -> {
                    Mockito.verify(notificationRepository, Mockito.times(1)).delete(it.getNotification());
                    Mockito.verify(itemOrderPriceRepository, Mockito.times(1)).delete(it);

                }
            );
    }

    @Test
    void deleteWhenError() {
        Long id = Instancio.create(Long.class);

        Mockito.when(priceRepository.findOne(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> priceService.deleteBy(id))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.price.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id);

        Mockito.verify(priceRepository, Mockito.times(1)).findOne(Mockito.any());
    }

    @Test
    void priceByWhenOk() {
        Long id = Instancio.create(Long.class);
        Price price = Instancio.create(Price.class);
        PriceDetailsHttp http = Instancio.create(PriceDetailsHttp.class);

        Mockito.when(priceRepository.findOne(Mockito.any())).thenReturn(Optional.of(price));
        Mockito.when(priceMapper.toHttpItem(price)).thenReturn(Optional.of(http));

        Assertions.assertThat(priceService.priceBy(id))
            .isEqualTo(http);

        Mockito.verify(priceRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(priceMapper, Mockito.times(1)).toHttpItem(price);
    }
}