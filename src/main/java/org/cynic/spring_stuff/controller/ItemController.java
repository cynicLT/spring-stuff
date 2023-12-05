package org.cynic.spring_stuff.controller;

import java.util.List;
import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.http.item.CreateItemHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp;
import org.cynic.spring_stuff.domain.http.item.ItemHttp;
import org.cynic.spring_stuff.service.ItemService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @GetMapping
    public List<ItemHttp> list(@AuthenticationPrincipal OidcUser oidcUser) {
        return itemService.itemsBy(oidcUser);
    }

    @GetMapping("/{id}")
    public ItemDetailsHttp item(@PathVariable Long id) {
        return itemService.itemBy(id);
    }


    @PostMapping
    public Long create(@RequestBody CreateItemHttp item) {
        return itemService.create(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.deleteBy(id);
    }
}
