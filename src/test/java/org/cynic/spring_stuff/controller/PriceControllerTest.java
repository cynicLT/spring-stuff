package org.cynic.spring_stuff.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp;
import org.cynic.spring_stuff.domain.http.price.PriceDetailsHttp;
import org.cynic.spring_stuff.domain.http.price.PriceHttp;
import org.cynic.spring_stuff.service.PriceService;
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
class PriceControllerTest {

    private PriceController priceController;

    @Mock
    private PriceService priceService;

    @BeforeEach
    void setUp() {
        this.priceController = new PriceController(priceService);
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
        Long orderId = Instancio.create(Long.class);
        List<PriceHttp> orders = Instancio.createList(PriceHttp.class);

        Mockito.when(priceService.pricesBy(oidcUser, Optional.of(orderId))).thenReturn(orders);

        Assertions.assertThat(priceController.list(oidcUser, Optional.of(orderId)))
            .containsExactlyElementsOf(orders);

        Mockito.verify(priceService, Mockito.times(1)).pricesBy(oidcUser, Optional.of(orderId));
    }

    @Test
    void createWhenOk() {
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
        CreatePriceHttp http = Instancio.create(CreatePriceHttp.class);
        Long id = Instancio.create(Long.class);

        Mockito.when(priceService.create(http, oidcUser)).thenReturn(id);

        Assertions.assertThat(priceController.create(http, oidcUser))
            .isEqualTo(id);

        Mockito.verify(priceService, Mockito.times(1)).create(http, oidcUser);
    }

    @Test
    void deleteWhenOK() {
        Long id = Instancio.create(Long.class);

        priceController.delete(id);

        Mockito.verify(priceService, Mockito.times(1)).deleteBy(id);
    }

    @Test
    void itemWhenOK() {
        Long id = Instancio.create(Long.class);
        PriceDetailsHttp http = Instancio.create(PriceDetailsHttp.class);

        Mockito.when(priceService.priceBy(id)).thenReturn(http);

        Assertions.assertThat(priceController.item(id))
            .isEqualTo(http);

        Mockito.verify(priceService, Mockito.times(1)).priceBy(id);

    }
}