package org.cynic.spring_stuff.controller;


import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.http.item.CreateItemHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp;
import org.cynic.spring_stuff.domain.http.item.ItemHttp;
import org.cynic.spring_stuff.service.ItemService;
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
class ItemControllerTest {

    private ItemController itemController;

    @Mock
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        this.itemController = new ItemController(itemService);
    }

    @Test
    void itemWhenOK() {
        Long id = Instancio.create(Long.class);
        ItemDetailsHttp item = Instancio.create(ItemDetailsHttp.class);

        Mockito.when(itemService.itemBy(id)).thenReturn(item);

        Assertions.assertThat(itemController.item(id))
            .isEqualTo(item);

        Mockito.verify(itemService, Mockito.times(1)).itemBy(id);
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

        List<ItemHttp> items = Instancio.createList(ItemHttp.class);

        Mockito.when(itemService.itemsBy(oidcUser)).thenReturn(items);

        Assertions.assertThat(itemController.list(oidcUser))
            .isEqualTo(items);

        Mockito.verify(itemService, Mockito.times(1)).itemsBy(oidcUser);
    }


    @Test
    void createWhenOK() {
        Long id = Instancio.create(Long.class);
        CreateItemHttp http = Instancio.create(CreateItemHttp.class);

        Mockito.when(itemService.create(http)).thenReturn(id);

        Assertions.assertThat(itemController.create(http))
            .isEqualTo(id);

        Mockito.verify(itemService, Mockito.times(1)).create(http);
    }

    @Test
    void deleteWhenOk() {
        Long id = Instancio.create(Long.class);

        itemController.delete(id);

        Mockito.verify(itemService, Mockito.times(1)).deleteBy(id);
    }
}