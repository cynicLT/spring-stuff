package org.cynic.spring_stuff.service;

import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.http.organization.OrganizationHttp;
import org.cynic.spring_stuff.mapper.OrganizationMapper;
import org.cynic.spring_stuff.repository.OrganizationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    public OrganizationService(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
    }

    public OrganizationHttp organizationBy(OidcUser oidcUser) {
        return organizationRepository.findOne(OrganizationRepository.byManagerEmail(oidcUser.getEmail()))
                .flatMap(organizationMapper::toHttp)
                .orElseThrow(() -> new ApplicationException("error.organization.not.found", oidcUser.getEmail()));
    }
}
