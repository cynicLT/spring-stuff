package org.cynic.spring_stuff.service;


import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.http.item.CreateItemHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp;
import org.cynic.spring_stuff.domain.http.item.ItemHttp;
import org.cynic.spring_stuff.mapper.ItemMapper;
import org.cynic.spring_stuff.repository.ItemRepository;
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
class ItemServiceTest {

    private ItemService itemService;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        this.itemService = new ItemService(itemRepository, orderRepository, itemMapper);
    }


    @Test
    void itemByWhenOK() {
        Long id = Instancio.create(Long.class);
        Item item = Instancio.create(Item.class);
        ItemDetailsHttp http = Instancio.create(ItemDetailsHttp.class);

        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.toItem(item)).thenReturn(Optional.of(http));

        Assertions.assertThat(itemService.itemBy(id))
            .isEqualTo(http);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(id);
        Mockito.verify(itemMapper, Mockito.times(1)).toItem(item);
    }


    @Test
    void itemByWhenError() {
        Long id = Instancio.create(Long.class);
        Item item = Instancio.create(Item.class);

        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.toItem(item)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> itemService.itemBy(id))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.item.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(id);
        Mockito.verify(itemMapper, Mockito.times(1)).toItem(item);
    }

    @Test
    void itemsByWhenOK() {
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

        ItemHttp http = Instancio.create(ItemHttp.class);
        Item item = Instancio.create(Item.class);

        Mockito.when(itemRepository.findAllByOrdersIsNullOrOrdersManagerEmail(oidcUser.getEmail()))
            .thenReturn(List.of(item));
        Mockito.when(itemMapper.toListItem(item)).thenReturn(Optional.of(http));

        Assertions.assertThat(itemService.itemsBy(oidcUser))
            .containsExactly(http);
        System.gc();
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByOrdersIsNullOrOrdersManagerEmail(oidcUser.getEmail());
        Mockito.verify(itemMapper, Mockito.times(1)).toListItem(item);
    }

    @Test
    void createWhenOK() {
        CreateItemHttp http = Instancio.create(CreateItemHttp.class);
        Set<Order> orders = Instancio.createSet(Order.class);

        Item item = Instancio.of(Item.class)
            .set(Select.field("orders"), orders)
            .create();

        Mockito.when(itemMapper.toEntity(http, orders)).thenReturn(Optional.of(item));
        Mockito.when(orderRepository.findAllByIdIn(http.orders())).thenReturn(orders);
        Mockito.doReturn(item).when(itemRepository).save(item);

        Assertions.assertThat(itemService.create(http))
            .isEqualTo(item.getId());

        Mockito.verify(orderRepository, Mockito.times(1)).findAllByIdIn(http.orders());
        Mockito.verify(itemMapper, Mockito.times(1)).toEntity(http, orders);
        Mockito.verify(orderRepository, Mockito.times(1)).saveAll(item.getOrders());
    }

    @Test
    void createWhenError() {
        CreateItemHttp http = Instancio.create(CreateItemHttp.class);
        Set<Order> orders = Instancio.createSet(Order.class);

        Item item = Instancio.of(Item.class)
            .set(Select.field("orders"), orders)
            .create();

        Mockito.when(itemMapper.toEntity(http, orders)).thenReturn(Optional.of(item));
        Mockito.when(orderRepository.findAllByIdIn(http.orders())).thenReturn(orders);

        Assertions.assertThatThrownBy(() -> itemService.create(http))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.item.create"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .isEmpty();

        Mockito.verify(orderRepository, Mockito.times(1)).findAllByIdIn(http.orders());
        Mockito.verify(itemMapper, Mockito.times(1)).toEntity(http, orders);
    }

    @Test
    void deleteWhenOK() {
        Long id = Instancio.create(Long.class);
        Order order = Instancio.of(Order.class)
            .set(Select.field("items"), new HashSet<Item>())
            .create();

        Item item = Instancio.of(Item.class)
            .set(Select.field("orders"), Set.of(order))
            .create();

        order.getItems().add(item);

        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        itemService.deleteBy(id);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(id);
        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
        Mockito.verify(itemRepository, Mockito.times(1)).delete(item);
    }

    @Test
    void deleteWhenOKEmpty() {
        Long id = Instancio.create(Long.class);

        Item item = Instancio.of(Item.class)
            .set(Select.field("orders"), Set.of())
            .create();

        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        itemService.deleteBy(id);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(id);
        Mockito.verify(itemRepository, Mockito.times(1)).delete(item);
    }


    @Test
    void deleteWhenError() {
        Long id = Instancio.create(Long.class);

        Mockito.when(itemRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> itemService.deleteBy(id))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.item.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(id);
    }
}