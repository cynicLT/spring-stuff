package org.cynic.spring_stuff.controller;

import java.util.List;
import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.http.manager.ManagerHttp;
import org.cynic.spring_stuff.service.ManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/managers")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping
    public List<ManagerHttp> list(@AuthenticationPrincipal OidcUser oidcUser,
        @RequestParam(required = false, defaultValue = "false") Boolean owner) {
        return managerService.managersBy(oidcUser, owner);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal OidcUser oidcUser) {
        managerService.deleteBy(id, oidcUser);
    }
}
