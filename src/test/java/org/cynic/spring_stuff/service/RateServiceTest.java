package org.cynic.spring_stuff.service;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Rate;
import org.cynic.spring_stuff.domain.http.rate.CreateRateHttp;
import org.cynic.spring_stuff.domain.http.rate.RateDetailsHttp;
import org.cynic.spring_stuff.domain.http.rate.RateHttp;
import org.cynic.spring_stuff.domain.model.type.RateSourceType;
import org.cynic.spring_stuff.mapper.RateMapper;
import org.cynic.spring_stuff.repository.RateRepository;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class RateServiceTest {

    private RateService rateService;
    @Mock
    private RateRepository rateRepository;

    @Mock
    private RateMapper rateMapper;

    @BeforeEach
    void setUp() {
        this.rateService = new RateService(rateRepository, rateMapper);
    }

    @Test
    void getAllRatesWhenOK() {
        Rate rate = Instancio.create(Rate.class);

        Mockito.when(rateRepository.findAll()).thenReturn(List.of(rate));

        Assertions.assertThat(rateService.getAllRates())
            .containsExactly(rate);

        Mockito.verify(rateRepository, Mockito.times(1)).findAll();
    }

    @Test
    void itemsWhenOK() {
        Rate rate = Instancio.create(Rate.class);
        RateHttp result = Instancio.create(RateHttp.class);
        ZonedDateTime date = Instancio.create(ZonedDateTime.class);
        Mockito.when(rateRepository.findAll(Mockito.any())).thenReturn(List.of(rate));
        Mockito.when(rateMapper.toHttp(rate)).thenReturn(Optional.of(result));

        Assertions.assertThat(rateService.items(Optional.ofNullable(date)))
            .containsExactly(result);

        Mockito.verify(rateRepository, Mockito.times(1)).findAll(Mockito.any());
        Mockito.verify(rateMapper, Mockito.times(1)).toHttp(rate);
    }

    @Test
    void deleteByWhenOK() {
        Long id = Instancio.create(Long.class);
        Rate rate = Instancio.create(Rate.class);

        Mockito.when(rateRepository.findByIdAndSource(id, RateSourceType.USR)).thenReturn(Optional.of(rate));

        rateService.deleteBy(id);

        Mockito.verify(rateRepository, Mockito.times(1)).findByIdAndSource(id, RateSourceType.USR);
        Mockito.verify(rateRepository, Mockito.times(1)).delete(rate);
    }


    @Test
    void deleteByWhenError() {
        Long id = Instancio.create(Long.class);

        Mockito.when(rateRepository.findByIdAndSource(id, RateSourceType.USR)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> rateService.deleteBy(id))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.rate.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(id);

        Mockito.verify(rateRepository, Mockito.times(1)).findByIdAndSource(id, RateSourceType.USR);
    }

    @Test
    void itemByWhenOK() {
        Currency currency = Instancio.create(Currency.class);
        Rate rate = Instancio.create(Rate.class);
        RateDetailsHttp http = Instancio.create(RateDetailsHttp.class);

        Mockito.when(rateRepository.findAll(Mockito.any(), Mockito.eq(Pageable.ofSize(Constants.MAX_HISTORY_RATES))))
            .thenReturn(new PageImpl<>(List.of(rate)));
        Mockito.when(rateMapper.toHttp(List.of(rate))).thenReturn(Optional.of(http));

        Assertions.assertThat(rateService.itemBy(currency))
            .isEqualTo(http);

        Mockito.verify(rateRepository, Mockito.times(1))
            .findAll(Mockito.any(), Mockito.eq(Pageable.ofSize(Constants.MAX_HISTORY_RATES)));
        Mockito.verify(rateMapper, Mockito.times(1)).toHttp(List.of(rate));
    }


    @Test
    void itemByWhenError() {
        Currency currency = Instancio.create(Currency.class);
        Rate rate = Instancio.create(Rate.class);

        Mockito.when(rateRepository.findAll(Mockito.any(), Mockito.eq(Pageable.ofSize(Constants.MAX_HISTORY_RATES))))
            .thenReturn(new PageImpl<>(List.of(rate)));
        Mockito.when(rateMapper.toHttp(List.of(rate))).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> rateService.itemBy(currency))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.rate.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(currency);

        Mockito.verify(rateRepository, Mockito.times(1))
            .findAll(Mockito.any(), Mockito.eq(Pageable.ofSize(Constants.MAX_HISTORY_RATES)));
        Mockito.verify(rateMapper, Mockito.times(1)).toHttp(List.of(rate));
    }

    @Test
    void createWhenOk() {
        CreateRateHttp http = Instancio.create(CreateRateHttp.class);
        Rate rate = Instancio.create(Rate.class);

        Mockito.when(
            rateMapper.toEntity(http, http.date().toOffsetDateTime(), RateSourceType.USR)).thenReturn(Optional.of(rate));
        Mockito.doReturn(rate).when(rateRepository).save(rate);

        Assertions.assertThat(rateService.create(http))
            .isEqualTo(rate.getId());

        Mockito.verify(rateMapper, Mockito.times(1)).toEntity(http, http.date().toOffsetDateTime(), RateSourceType.USR);
        Mockito.verify(rateRepository, Mockito.times(1)).save(rate);
        Mockito.verify(rateRepository, Mockito.times(1)).existsByCurrencyAndDateTime(http.currency(), http.date().toOffsetDateTime());
    }


    @Test
    void createWhenErrorExists() {
        CreateRateHttp http = Instancio.create(CreateRateHttp.class);

        Mockito.when(rateRepository.existsByCurrencyAndDateTime(http.currency(), http.date().toOffsetDateTime()))
            .thenReturn(true);

        Assertions.assertThatThrownBy(() -> rateService.create(http))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.rate.exists"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(
                http.currency(),
                http.date().toOffsetDateTime()
            );

        Mockito.verify(rateRepository, Mockito.times(1)).existsByCurrencyAndDateTime(http.currency(),
            http.date().toOffsetDateTime());

    }

    @Test
    void createWhenErrorCreate() {
        CreateRateHttp http = Instancio.create(CreateRateHttp.class);
        Rate rate = Instancio.create(Rate.class);

        Mockito.when(rateMapper.toEntity(http, http.date().toOffsetDateTime(), RateSourceType.USR)).thenReturn(Optional.of(rate));

        Assertions.assertThatThrownBy(() -> rateService.create(http))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.rate.create"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .isEmpty();

        Mockito.verify(rateMapper, Mockito.times(1))
            .toEntity(http, http.date().toOffsetDateTime(), RateSourceType.USR);
        Mockito.verify(rateRepository, Mockito.times(1)).save(rate);
        Mockito.verify(rateRepository, Mockito.times(1)).existsByCurrencyAndDateTime(http.currency(), http.date().toOffsetDateTime());

    }
}