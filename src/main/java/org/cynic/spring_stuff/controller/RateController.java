package org.cynic.spring_stuff.controller;

import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.http.rate.CreateRateHttp;
import org.cynic.spring_stuff.domain.http.rate.RateDetailsHttp;
import org.cynic.spring_stuff.domain.http.rate.RateHttp;
import org.cynic.spring_stuff.service.RateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rates")
@PreAuthorize(Constants.PRE_AUTHORIZE_EXPRESSION)
public class RateController {

    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }


    @GetMapping
    public List<RateHttp> list(
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Optional<ZonedDateTime> date
    ) {
        return rateService.items(date);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        rateService.deleteBy(id);
    }

    @GetMapping("/{currency}")
    public RateDetailsHttp item(@PathVariable Currency currency) {
        return rateService.itemBy(currency);
    }

    @PostMapping
    public Long create(@RequestBody CreateRateHttp rate) {
        return rateService.create(rate);
    }
}
