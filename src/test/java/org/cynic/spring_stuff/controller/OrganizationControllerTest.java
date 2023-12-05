package org.cynic.spring_stuff.controller;

import java.time.Instant;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.http.organization.OrganizationHttp;
import org.cynic.spring_stuff.service.OrganizationService;
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
class OrganizationControllerTest {

    private OrganizationController organizationController;

    @Mock
    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        organizationController = new OrganizationController(organizationService);
    }

    @Test
    void selfWhenOK() {
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

        OrganizationHttp http = Instancio.create(OrganizationHttp.class);

        Mockito.when(organizationService.organizationBy(oidcUser)).thenReturn(http);

        Assertions.assertThat(organizationController.self(oidcUser))
            .isEqualTo(http);

        Mockito.verify(organizationService, Mockito.times(1)).organizationBy(oidcUser);
    }
}