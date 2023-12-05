package org.cynic.spring_stuff.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.http.manager.ManagerHttp;
import org.cynic.spring_stuff.mapper.ManagerMapper;
import org.cynic.spring_stuff.repository.ManagerRepository;
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
class ManagerServiceTest {

    private ManagerService managerService;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private ManagerMapper managerMapper;

    @BeforeEach
    void setUp() {
        this.managerService = new ManagerService(managerRepository, managerMapper);
    }

    @Test
    void mangersByWhenOK() {
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
        Manager manager = Instancio.create(Manager.class);
        ManagerHttp http = Instancio.create(ManagerHttp.class);
        Boolean owner = Instancio.create(Boolean.class);

        Mockito.when(managerRepository.findAll(Mockito.any())).thenReturn(List.of(manager));
        Mockito.when(managerMapper.toHttp(manager)).thenReturn(Optional.of(http));

        Assertions.assertThat(managerService.managersBy(oidcUser, owner))
            .containsExactly(http);

        Mockito.verify(managerRepository, Mockito.times(1)).findAll(Mockito.any());
        Mockito.verify(managerMapper, Mockito.times(1)).toHttp(manager);
    }


    @Test
    void deleteByWhenOk() {
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
        Manager manager = Instancio.create(Manager.class);

        Mockito.when(managerRepository.findOne(Mockito.any())).thenReturn(Optional.of(manager));

        managerService.deleteBy(id, oidcUser);

        Mockito.verify(managerRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(managerRepository, Mockito.times(1)).delete(manager);
    }
}