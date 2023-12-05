package org.cynic.spring_stuff.service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Organization;
import org.cynic.spring_stuff.domain.http.organization.OrganizationHttp;
import org.cynic.spring_stuff.mapper.OrganizationMapper;
import org.cynic.spring_stuff.repository.OrganizationRepository;
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
class OrganizationServiceTest {

    private OrganizationService organizationService;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationMapper organizationMapper;

    @BeforeEach
    void setUp() {
        organizationService = new OrganizationService(organizationRepository, organizationMapper);
    }

    @Test
    void organizationByWhenOK() {
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

        Organization organization = Instancio.create(Organization.class);
        OrganizationHttp http = Instancio.create(OrganizationHttp.class);

        Mockito.when(organizationRepository.findOne(Mockito.any())).thenReturn(Optional.of(organization));
        Mockito.when(organizationMapper.toHttp(organization)).thenReturn(Optional.of(http));

        Assertions.assertThat(organizationService.organizationBy(oidcUser))
            .isEqualTo(http);

        Mockito.verify(organizationRepository, Mockito.atMost(1)).findOne(Mockito.any());
        Mockito.verify(organizationMapper, Mockito.times(1)).toHttp(organization);
    }


    @Test
    void organizationByWhenError() {
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

        Organization organization = Instancio.create(Organization.class);

        Mockito.when(organizationRepository.findOne(Mockito.any())).thenReturn(Optional.of(organization));
        Mockito.when(organizationMapper.toHttp(organization)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> organizationService.organizationBy(oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.organization.not.found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(oidcUser.getEmail());

        Mockito.verify(organizationRepository, Mockito.atMost(1)).findOne(Mockito.any());
        Mockito.verify(organizationMapper, Mockito.times(1)).toHttp(organization);
    }
}