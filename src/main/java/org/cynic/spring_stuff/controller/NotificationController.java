package org.cynic.spring_stuff.controller;

import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.http.NotificationHttp;
import org.cynic.spring_stuff.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationHttp> list(@RequestParam(required = false) Optional<Boolean> visit, @AuthenticationPrincipal OidcUser oidcUser) {
        return notificationService.itemsBy(oidcUser, visit);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal OidcUser oidcUser) {
        notificationService.deleteBy(id, oidcUser);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @AuthenticationPrincipal OidcUser oidcUser) {
        notificationService.visitBy(id, oidcUser);
    }

}
