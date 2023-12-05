package org.cynic.spring_stuff.controller;

import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.http.organization.OrganizationHttp;
import org.cynic.spring_stuff.service.OrganizationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/self")
    public OrganizationHttp self(@AuthenticationPrincipal OidcUser oidcUser) {
        return organizationService.organizationBy(oidcUser);
    }
}
