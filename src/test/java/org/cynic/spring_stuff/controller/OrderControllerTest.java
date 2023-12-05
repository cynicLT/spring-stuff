package org.cynic.spring_stuff.controller;

import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.http.order.CreateOrderHttp;
import org.cynic.spring_stuff.domain.http.order.OrderDetailsHttp;
import org.cynic.spring_stuff.domain.http.order.OrderHttp;
import org.cynic.spring_stuff.service.OrderService;
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

import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class OrderControllerTest {

    private OrderController orderController;
    @Mock
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        this.orderController = new OrderController(orderService);
    }

    @Test
    void listWhenOK() {
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

        List<OrderHttp> orders = Instancio.createList(OrderHttp.class);
        Boolean closed = Instancio.create(Boolean.class);

        Mockito.when(orderService.ordersBy(oidcUser, Optional.of(closed))).thenReturn(orders);

        Assertions.assertThat(orderController.list(oidcUser, Optional.of(closed)))
            .isEqualTo(orders);

        Mockito.verify(orderService, Mockito.timeout(1)).ordersBy(oidcUser, Optional.of(closed));
    }


    @Test
    void itemWhenOK() {
        Long id = Instancio.create(Long.class);
        Currency currency = Instancio.create(Currency.class);
        OrderDetailsHttp order = Instancio.create(OrderDetailsHttp.class);

        Mockito.when(orderService.orderDetailsBy(id, currency)).thenReturn(order);

        Assertions.assertThat(orderController.item(id, currency))
            .isEqualTo(order);

        Mockito.verify(orderService, Mockito.timeout(1)).orderDetailsBy(id, currency);
    }

    @Test
    void deleteWhenOK() {
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

        orderController.delete(id, oidcUser);

        Mockito.verify(orderService, Mockito.times(1)).deleteBy(id, oidcUser);
    }

    @Test
    void createWhenOk() {
        CreateOrderHttp http = Instancio.create(CreateOrderHttp.class);
        Long id = Instancio.create(Long.class);

        Mockito.when(orderService.create(http)).thenReturn(id);

        Assertions.assertThat(orderController.create(http))
            .isEqualTo(id);

        Mockito.verify(orderService, Mockito.times(1)).create(http);
    }

    @Test
    void updateWHenOk() {
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

        orderController.update(id, oidcUser);

        Mockito.verify(orderService, Mockito.times(1)).closeBy(id, oidcUser);

    }
}