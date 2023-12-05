package org.cynic.spring_stuff.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.http.manager.ManagerHttp;
import org.cynic.spring_stuff.service.ManagerService;
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
class ManagerControllerTest {


    private ManagerController managerController;

    @Mock
    private ManagerService managerService;

    @BeforeEach
    void setUp() {
        this.managerController = new ManagerController(managerService);
    }

    @Test
    void listWhenOk() {
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

        Boolean owner = Instancio.create(Boolean.class);
        ManagerHttp http = Instancio.create(ManagerHttp.class);

        Mockito.when(managerService.managersBy(oidcUser, owner)).thenReturn(List.of(http));

        Assertions.assertThat(managerController.list(oidcUser, owner))
            .containsExactly(http);

        Mockito.verify(managerService, Mockito.times(1)).managersBy(oidcUser, owner);
    }

    @Test
    void deleteWhenOk() {
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

        managerController.delete(id, oidcUser);

        Mockito.verify(managerService, Mockito.times(1)).deleteBy(id, oidcUser);

    }
}