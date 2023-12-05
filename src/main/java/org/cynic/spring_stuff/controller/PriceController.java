package org.cynic.spring_stuff.controller;

import java.util.List;
import java.util.Optional;
import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp;
import org.cynic.spring_stuff.domain.http.price.PriceDetailsHttp;
import org.cynic.spring_stuff.domain.http.price.PriceHttp;
import org.cynic.spring_stuff.service.PriceService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prices")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    public List<PriceHttp> list(@AuthenticationPrincipal OidcUser oidcUser, @RequestParam(required = false) Optional<Long> orderId) {
        return priceService.pricesBy(oidcUser, orderId);
    }

    @GetMapping("/{id}")
    public PriceDetailsHttp item(@PathVariable Long id) {
        return priceService.priceBy(id);
    }

    @PostMapping
    public Long create(@RequestBody CreatePriceHttp price, @AuthenticationPrincipal OidcUser oidcUser) {
        return priceService.create(price, oidcUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        priceService.deleteBy(id);
    }
}
