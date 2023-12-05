package org.cynic.spring_stuff.controller;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.http.order.CreateOrderHttp;
import org.cynic.spring_stuff.domain.http.order.OrderDetailsHttp;
import org.cynic.spring_stuff.domain.http.order.OrderHttp;
import org.cynic.spring_stuff.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping
    public List<OrderHttp> list(@AuthenticationPrincipal OidcUser oidcUser,
        @RequestParam(required = false) Optional<Boolean> closed) {
        return orderService.ordersBy(oidcUser, closed);
    }

    @GetMapping("/{id}")
    public OrderDetailsHttp item(@PathVariable Long id,
        @RequestParam(defaultValue = Constants.CURRENCY_VALUE, required = false) Currency currency) {
        return orderService.orderDetailsBy(id, currency);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal OidcUser oidcUser) {
        orderService.deleteBy(id, oidcUser);
    }


    @PostMapping
    public Long create(@RequestBody CreateOrderHttp http) {
        return orderService.create(http);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @AuthenticationPrincipal OidcUser oidcUser) {
        orderService.closeBy(id, oidcUser);
    }

}
