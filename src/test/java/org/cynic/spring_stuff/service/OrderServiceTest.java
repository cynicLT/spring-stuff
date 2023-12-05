package org.cynic.spring_stuff.service;

import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.http.order.CreateOrderHttp;
import org.cynic.spring_stuff.domain.http.order.OrderDetailsHttp;
import org.cynic.spring_stuff.domain.http.order.OrderHttp;
import org.cynic.spring_stuff.mapper.OrderMapper;
import org.cynic.spring_stuff.repository.ItemOrderPriceRepository;
import org.cynic.spring_stuff.repository.ItemRepository;
import org.cynic.spring_stuff.repository.ManagerRepository;
import org.cynic.spring_stuff.repository.NotificationRepository;
import org.cynic.spring_stuff.repository.OrderRepository;
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
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemOrderPriceRepository itemOrderPriceRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderService(managerRepository, itemRepository, orderRepository, itemOrderPriceRepository, notificationRepository, orderMapper);
    }

    @Test
    void ordersByOidcUserAndCurrencyWhenOK() {
        OidcUser oidcUser = new DefaultOidcUser(
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
        Order order = Instancio.create(Order.class);
        OrderHttp http = Instancio.create(OrderHttp.class);
        Boolean closed = Instancio.create(Boolean.class);

        Mockito.when(orderRepository.findAll(Mockito.any()))
            .thenReturn(List.of(order));
        Mockito.when(orderMapper.toListItem(order)).thenReturn(Optional.of(http));

        Assertions.assertThat(orderService.ordersBy(oidcUser, Optional.ofNullable(closed)))
            .containsExactly(http);

        Mockito.verify(orderRepository, Mockito.timeout(1)).findAll(Mockito.any());
        Mockito.verify(orderMapper, Mockito.timeout(1)).toListItem(order);
    }


    @Test
    void ordersByOidcUserWhenOK() {
        OidcUser oidcUser = new DefaultOidcUser(
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
        Order order = Instancio.create(Order.class);
        OrderHttp http = Instancio.create(OrderHttp.class);
        Boolean closed = Instancio.create(Boolean.class);

        Mockito.when(orderRepository.findAll(Mockito.any()))
            .thenReturn(List.of(order));
        Mockito.when(orderMapper.toListItem(order)).thenReturn(Optional.of(http));

        Assertions.assertThat(orderService.ordersBy(oidcUser, Optional.ofNullable(closed)))
            .containsExactly(http);

        Mockito.verify(orderRepository, Mockito.timeout(1)).findAll(Mockito.any());
        Mockito.verify(orderMapper, Mockito.timeout(1)).toListItem(order);
    }

    @Test
    void orderByWhenOk() {
        Order order = Instancio.create(Order.class);
        Currency currency = Instancio.create(Currency.class);
        OrderDetailsHttp http = Instancio.create(OrderDetailsHttp.class);
        Long id = Instancio.create(Long.class);

        Mockito.when(orderRepository.findById(id))
            .thenReturn(Optional.of(order));
        Mockito.when(orderMapper.toItem(order, currency)).thenReturn(Optional.of(http));

        Assertions.assertThat(orderService.orderDetailsBy(id, currency))
            .isEqualTo(http);

        Mockito.verify(orderRepository, Mockito.timeout(1)).findById(id);
        Mockito.verify(orderMapper, Mockito.timeout(1)).toItem(order, currency);
    }

    @Test
    void orderByWhenError() {
        Order order = Instancio.create(Order.class);
        Currency currency = Instancio.create(Currency.class);
        Long id = Instancio.create(Long.class);

        Mockito.when(orderRepository.findById(id))
            .thenReturn(Optional.of(order));
        Mockito.when(orderMapper.toItem(order, currency)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> orderService.orderDetailsBy(id, currency))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.order.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id);

        Mockito.verify(orderRepository, Mockito.timeout(1)).findById(id);
        Mockito.verify(orderMapper, Mockito.timeout(1)).toItem(order, currency);
    }

    @Test
    void deleteByWhenOK() {
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
        Long id = Instancio.create(Long.class);
        Order order = Instancio.create(Order.class);

        Mockito.when(orderRepository.findOne(Mockito.any())).thenReturn(Optional.of(order));

        orderService.deleteBy(id, oidcUser);

        Mockito.verify(orderRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(orderRepository, Mockito.times(1)).delete(order);
    }

    @Test
    void deleteByWhenError() {
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
        Long id = Instancio.create(Long.class);

        Mockito.when(orderRepository.findOne(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> orderService.deleteBy(id, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.order.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id);

        Mockito.verify(orderRepository, Mockito.times(1)).findOne(Mockito.any());
    }

    @Test
    void createWhenOK() {
        Long itemId = Instancio.create(Long.class);
        CreateOrderHttp http = Instancio.of(CreateOrderHttp.class)
            .set(Select.field("items"), Set.of(itemId))
            .create();

        Item item = Instancio.of(Item.class)
            .set(Select.field("id"), itemId)
            .create();
        Manager manager = Instancio.create(Manager.class);
        Manager owner = Instancio.create(Manager.class);
        Order order = Instancio.create(Order.class);

        Mockito.when(itemRepository.findAllByIdIn(http.items())).thenReturn(Set.of(item));
        Mockito.when(managerRepository.findById(http.manager())).thenReturn(Optional.of(manager));
        Mockito.when(managerRepository.findById(http.owner())).thenReturn(Optional.of(owner));
        Mockito.when(orderMapper.toEntity(http, Set.of(item), manager, owner))
            .thenReturn(Optional.of(order));
        Mockito.doReturn(order).when(orderRepository).save(order);

        Assertions.assertThat(orderService.create(http))
            .isEqualTo(order.getId());

        Mockito.verify(itemRepository, Mockito.times(1)).findAllByIdIn(http.items());
        Mockito.verify(managerRepository, Mockito.times(1)).findById(http.manager());
        Mockito.verify(managerRepository, Mockito.times(1)).findById(http.owner());

        Mockito.verify(orderMapper, Mockito.times(1)).toEntity(http, Set.of(item), manager, owner);
        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
    }

    @Test
    void createWhenErrorCreate() {
        Long itemId = Instancio.create(Long.class);
        CreateOrderHttp http = Instancio.of(CreateOrderHttp.class)
            .set(Select.field("items"), Set.of(itemId))
            .create();

        Item item = Instancio.of(Item.class)
            .set(Select.field("id"), itemId)
            .create();
        Manager manager = Instancio.create(Manager.class);
        Manager owner = Instancio.create(Manager.class);

        Mockito.when(itemRepository.findAllByIdIn(http.items())).thenReturn(Set.of(item));
        Mockito.when(managerRepository.findById(http.manager())).thenReturn(Optional.of(manager));
        Mockito.when(managerRepository.findById(http.owner())).thenReturn(Optional.of(owner));
        Mockito.when(orderMapper.toEntity(http, Set.of(item), manager, owner))
            .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> orderService.create(http))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.order.create"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .isEmpty();

        Mockito.verify(itemRepository, Mockito.times(1)).findAllByIdIn(http.items());
        Mockito.verify(managerRepository, Mockito.times(1)).findById(http.manager());
        Mockito.verify(managerRepository, Mockito.times(1)).findById(http.owner());

        Mockito.verify(orderMapper, Mockito.times(1)).toEntity(http, Set.of(item), manager, owner);
    }

    @Test
    void createWhenErrorManager() {
        Long itemId = Instancio.create(Long.class);
        CreateOrderHttp http = Instancio.of(CreateOrderHttp.class)
            .set(Select.field("items"), Set.of(itemId))
            .create();

        Item item = Instancio.of(Item.class)
            .set(Select.field("id"), itemId)
            .create();
        Manager manager = Instancio.create(Manager.class);

        Mockito.when(itemRepository.findAllByIdIn(http.items())).thenReturn(Set.of(item));
        Mockito.when(managerRepository.findById(http.manager())).thenReturn(Optional.of(manager));
        Mockito.when(managerRepository.findById(http.owner())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> orderService.create(http))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.order.owner.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(http.owner());

        Mockito.verify(itemRepository, Mockito.times(1)).findAllByIdIn(http.items());
        Mockito.verify(managerRepository, Mockito.times(1)).findById(http.manager());
        Mockito.verify(managerRepository, Mockito.times(1)).findById(http.owner());
    }

    @Test
    void createWhenErrorOwner() {
        Long itemId = Instancio.create(Long.class);
        CreateOrderHttp http = Instancio.of(CreateOrderHttp.class)
            .set(Select.field("items"), Set.of(itemId))
            .create();

        Item item = Instancio.of(Item.class)
            .set(Select.field("id"), itemId)
            .create();

        Mockito.when(itemRepository.findAllByIdIn(http.items())).thenReturn(Set.of(item));
        Mockito.when(managerRepository.findById(http.manager())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> orderService.create(http))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.order.manager.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(http.manager());

        Mockito.verify(itemRepository, Mockito.times(1)).findAllByIdIn(http.items());
        Mockito.verify(managerRepository, Mockito.times(1)).findById(http.manager());
    }

    @Test
    void createWhenErrorItems() {
        Long itemId1 = Instancio.create(Long.class);
        Long itemId2 = Instancio.create(Long.class);
        CreateOrderHttp http = Instancio.of(CreateOrderHttp.class)
            .set(Select.field("items"), Set.of(itemId1, itemId2))
            .create();

        Item item = Instancio.of(Item.class)
            .set(Select.field("id"), itemId1)
            .create();

        Mockito.when(itemRepository.findAllByIdIn(http.items())).thenReturn(Set.of(item));

        Assertions.assertThatThrownBy(() -> orderService.create(http))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.order.items.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(itemId2);

        Mockito.verify(itemRepository, Mockito.times(1)).findAllByIdIn(http.items());
    }

    @Test
    void closeByWhenOK() {
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
        Long id = Instancio.create(Long.class);
        Order order = Instancio.create(Order.class);

        Mockito.when(orderRepository.findOne(Mockito.any())).thenReturn(Optional.of(order));
        Mockito.when(orderMapper.toEntity(order, Boolean.TRUE)).thenReturn(Optional.of(order));

        orderService.closeBy(id, oidcUser);

        Mockito.verify(orderRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(orderMapper, Mockito.times(1)).toEntity(order, Boolean.TRUE);
        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
    }

    @Test
    void closeByWhenErrorSave() {
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
        Long id = Instancio.create(Long.class);
        Order order = Instancio.create(Order.class);

        Mockito.when(orderRepository.findOne(Mockito.any())).thenReturn(Optional.of(order));
        Mockito.when(orderMapper.toEntity(order, Boolean.TRUE)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> orderService.closeBy(id, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.order.save"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id);

        Mockito.verify(orderRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(orderMapper, Mockito.times(1)).toEntity(order, Boolean.TRUE);
    }

    @Test
    void closeByWhenErrorFind() {
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
        Long id = Instancio.create(Long.class);

        Mockito.when(orderRepository.findOne(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> orderService.closeBy(id, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.order.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id, oidcUser.getEmail());

        Mockito.verify(orderRepository, Mockito.times(1)).findOne(Mockito.any());
    }
}