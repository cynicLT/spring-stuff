package org.cynic.spring_stuff.service;

import jakarta.persistence.LockModeType;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Notification;
import org.cynic.spring_stuff.domain.http.NotificationHttp;
import org.cynic.spring_stuff.mapper.NotificationMapper;
import org.cynic.spring_stuff.repository.ManagerRepository;
import org.cynic.spring_stuff.repository.NotificationRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ManagerRepository managerRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, ManagerRepository managerRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.managerRepository = managerRepository;
        this.notificationMapper = notificationMapper;
    }

    public List<NotificationHttp> itemsBy(OidcUser oidcUser, Optional<Boolean> visit) {
        return notificationRepository.findAll(NotificationRepository.byManager(oidcUser.getEmail(), visit))
            .stream()
            .map(notificationMapper::toHttp)
            .flatMap(Optional::stream)
            .toList();
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    public void deleteBy(Long id, OidcUser oidcUser) {
        Notification notification = notificationRepository.findByIdAndManagerEmail(id, oidcUser.getEmail())
            .orElseThrow(() -> new ApplicationException("error.notification.not-found", id, oidcUser.getEmail()));

        notificationRepository.delete(notification);
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    public void visitBy(Long id, OidcUser oidcUser) {
        Notification notification = notificationRepository.findOne(NotificationRepository.byIdAndNotVisitedAndNoManager(id))
            .orElseThrow(() -> new ApplicationException("error.notification.not-found", id));

        Manager manager = managerRepository.findByEmail(oidcUser.getEmail())
            .orElseThrow(() -> new ApplicationException("error.manager.not-found", oidcUser.getEmail()));

        notificationMapper.toEntity(notification, manager, Boolean.TRUE)
            .ifPresentOrElse(notificationRepository::save,
                () -> {
                    throw new ApplicationException("error.notification.update", notification.getId());
                });
    }
}
