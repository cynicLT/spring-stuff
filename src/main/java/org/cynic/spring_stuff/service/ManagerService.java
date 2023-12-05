package org.cynic.spring_stuff.service;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.http.manager.ManagerHttp;
import org.cynic.spring_stuff.mapper.ManagerMapper;
import org.cynic.spring_stuff.repository.ManagerRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;

    public ManagerService(ManagerRepository managerRepository, ManagerMapper managerMapper) {
        this.managerRepository = managerRepository;
        this.managerMapper = managerMapper;
    }

    public List<ManagerHttp> managersBy(OidcUser oidcUser, Boolean owner) {
        return managerRepository.findAll(ManagerRepository.byEmailAndIsOwner(oidcUser.getEmail(), owner))
            .stream()
            .map(managerMapper::toHttp)
            .flatMap(Optional::stream)
            .toList();
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    public void deleteBy(Long id, OidcUser oidcUser) {
        Manager manager = managerRepository.findOne(ManagerRepository.byIdAndEmailNotSameOrganizationOrUser(id, oidcUser.getEmail()))
            .orElseThrow(() -> new ApplicationException("error.manger.not-found", id, oidcUser.getEmail()));

        managerRepository.delete(manager);
    }
}
