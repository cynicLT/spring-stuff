package org.cynic.spring_stuff.controller;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.http.rate.CreateRateHttp;
import org.cynic.spring_stuff.domain.http.rate.RateDetailsHttp;
import org.cynic.spring_stuff.domain.http.rate.RateHttp;
import org.cynic.spring_stuff.service.RateService;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class,
})
@Tag("unit")
class RateControllerTest {

    private RateController rateController;

    @Mock
    private RateService rateService;

    @BeforeEach
    void setUp() {
        this.rateController = new RateController(rateService);
    }

    @Test
    void listWhenOK() {
        List<RateHttp> items = Instancio.createList(RateHttp.class);
        ZonedDateTime date = Instancio.create(ZonedDateTime.class);
        Mockito.when(rateService.items(Optional.ofNullable(date))).thenReturn(items);

        Assertions.assertThat(rateController.list(Optional.ofNullable(date)))
            .containsAll(items);

        Mockito.verify(rateService, Mockito.times(1)).items(Optional.ofNullable(date));
    }

    @Test
    void deleteWhenOK() {
        Long id = Instancio.create(Long.class);

        rateController.delete(id);

        Mockito.verify(rateService, Mockito.times(1)).deleteBy(id);
    }

    @Test
    void itemWhenOK() {
        Currency currency = Instancio.create(Currency.class);
        RateDetailsHttp http = Instancio.create(RateDetailsHttp.class);

        Mockito.when(rateService.itemBy(currency)).thenReturn(http);

        Assertions.assertThat(rateController.item(currency)).isEqualTo(http);

        Mockito.verify(rateService, Mockito.times(1)).itemBy(currency);
    }

    @Test
    void createWhenOK() {
        CreateRateHttp http = Instancio.create(CreateRateHttp.class);
        Long id = Instancio.create(Long.class);

        Mockito.when(rateService.create(http)).thenReturn(id);

        Assertions.assertThat(rateController.create(http))
            .isEqualTo(id);

        Mockito.verify(rateService, Mockito.times(1)).create(http);
    }
}