package org.cynic.spring_stuff.service;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Notification;
import org.cynic.spring_stuff.domain.http.NotificationHttp;
import org.cynic.spring_stuff.mapper.NotificationMapper;
import org.cynic.spring_stuff.repository.ManagerRepository;
import org.cynic.spring_stuff.repository.NotificationRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class NotificationServiceTest {

    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private ManagerRepository managerRepository;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, managerRepository, notificationMapper);
    }

    @Test
    void itemsByWhenOK() {
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
        Notification notification = Instancio.create(Notification.class);
        NotificationHttp http = Instancio.create(NotificationHttp.class);
        Boolean visit = Instancio.create(Boolean.class);

        Mockito.when(notificationRepository.findAll(Mockito.any()))
            .thenReturn(List.of(notification));
        Mockito.when(notificationMapper.toHttp(notification)).thenReturn(Optional.of(http));

        Assertions.assertThat(notificationService.itemsBy(oidcUser, Optional.ofNullable(visit)))
            .containsSequence(List.of(http));

        Mockito.verify(notificationRepository, Mockito.times(1)).findAll(Mockito.any());
        Mockito.verify(notificationMapper, Mockito.times(1)).toHttp(notification);
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
        Notification notification = Instancio.create(Notification.class);

        Mockito.when(notificationRepository.findByIdAndManagerEmail(id, oidcUser.getEmail()))
            .thenReturn(Optional.of(notification));

        notificationService.deleteBy(id, oidcUser);

        Mockito.verify(notificationRepository, Mockito.times(1)).findByIdAndManagerEmail(id, oidcUser.getEmail());
        Mockito.verify(notificationRepository, Mockito.times(1)).delete(notification);
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

        Mockito.when(notificationRepository.findByIdAndManagerEmail(id, oidcUser.getEmail()))
            .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> notificationService.deleteBy(id, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.notification.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id, oidcUser.getEmail());

        Mockito.verify(notificationRepository, Mockito.times(1)).findByIdAndManagerEmail(id, oidcUser.getEmail());
    }

    @Test
    void visitByWhenOK() {
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
        Notification notification = Instancio.create(Notification.class);
        Manager manager = Instancio.create(Manager.class);

        Mockito.when(notificationRepository.findOne(Mockito.any())).thenReturn(Optional.of(notification));
        Mockito.when(managerRepository.findByEmail(oidcUser.getEmail())).thenReturn(Optional.of(manager));
        Mockito.when(notificationMapper.toEntity(notification, manager, Boolean.TRUE)).thenReturn(Optional.of(notification));

        notificationService.visitBy(id, oidcUser);

        Mockito.verify(notificationRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(managerRepository, Mockito.times(1)).findByEmail(oidcUser.getEmail());
        Mockito.verify(notificationMapper, Mockito.times(1)).toEntity(notification, manager, Boolean.TRUE);
        Mockito.verify(notificationRepository, Mockito.times(1)).save(notification);
    }

    @Test
    void visitByWhenErrorMapping() {
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
        Notification notification = Instancio.create(Notification.class);
        Manager manager = Instancio.create(Manager.class);

        Mockito.when(notificationRepository.findOne(Mockito.any())).thenReturn(Optional.of(notification));
        Mockito.when(managerRepository.findByEmail(oidcUser.getEmail())).thenReturn(Optional.of(manager));
        Mockito.when(notificationMapper.toEntity(notification, manager, Boolean.TRUE)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> notificationService.visitBy(id, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.notification.update"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(notification.getId());

        Mockito.verify(notificationRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(managerRepository, Mockito.times(1)).findByEmail(oidcUser.getEmail());
        Mockito.verify(notificationMapper, Mockito.times(1)).toEntity(notification, manager, Boolean.TRUE);
    }

    @Test
    void visitByWhenErrorManagerNotFound() {
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
        Notification notification = Instancio.create(Notification.class);

        Mockito.when(notificationRepository.findOne(Mockito.any())).thenReturn(Optional.of(notification));
        Mockito.when(managerRepository.findByEmail(oidcUser.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> notificationService.visitBy(id, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.manager.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(oidcUser.getEmail());

        Mockito.verify(notificationRepository, Mockito.times(1)).findOne(Mockito.any());
        Mockito.verify(managerRepository, Mockito.times(1)).findByEmail(oidcUser.getEmail());
    }


    @Test
    void visitByWhenErrorNotificationNotFound() {
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

        Mockito.when(notificationRepository.findOne(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> notificationService.visitBy(id, oidcUser))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.notification.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id);

        Mockito.verify(notificationRepository, Mockito.times(1)).findOne(Mockito.any());
    }
}