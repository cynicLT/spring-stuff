package org.cynic.spring_stuff.converter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Price;
import org.cynic.spring_stuff.domain.entity.Rate;
import org.cynic.spring_stuff.service.RateService;
import org.cynic.spring_stuff.utils.BigDecimalUtils;
import org.instancio.Instancio;
import org.instancio.Select;
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
    InstancioExtension.class
})
@Tag("unit")
class MoneyConverterTest {

    private MoneyConverter moneyConverter;

    @Mock
    private RateService rateService;


    @BeforeEach
    void setUp() {
        this.moneyConverter = new MoneyConverter(rateService);
    }

    @Test
    void convertWhenMatches() {
        Price price = Instancio.create(Price.class);
        Currency to = price.getCurrency();
        BigDecimal fraction = Instancio.create(BigDecimal.class);

        Assertions.assertThat(moneyConverter.convert(price, fraction, to))
            .isEqualTo(BigDecimalUtils.scale(price.getValue().multiply(fraction)));

        Mockito.verifyNoInteractions(rateService);
    }


    @Test
    void convertWhenDifferentAndExistClosest() {
        Price price = Instancio.create(Price.class);
        Currency to = Instancio.create(Currency.class);
        BigDecimal fraction = Instancio.create(BigDecimal.class);
        Rate fromRate = Instancio.of(Rate.class)
            .set(Select.field("currency"), price.getCurrency())
            .create();
        Rate toRate = Instancio.of(Rate.class)
            .set(Select.field("currency"), to)
            .create();

        Mockito.when(rateService.getAllRates()).thenReturn(List.of(fromRate, toRate));

        Assertions.assertThat(moneyConverter.convert(price, fraction, to))
            .isEqualTo(
                price.getValue()
                    .multiply(fraction, MathContext.DECIMAL128)
                    .multiply(toRate.getValue(), MathContext.DECIMAL128)
                    .divide(fromRate.getValue(), MathContext.DECIMAL128)
                    .setScale(4, RoundingMode.CEILING)
            );

        Mockito.verify(rateService, Mockito.times(2)).getAllRates();
    }

    @Test
    void convertWhenErrorNotFound() {
        Price price = Instancio.create(Price.class);
        Currency to = Instancio.create(Currency.class);
        BigDecimal fraction = Instancio.create(BigDecimal.class);
        Rate fromRate = Instancio.of(Rate.class)
            .set(Select.field("currency"), price.getCurrency())
            .create();

        Mockito.when(rateService.getAllRates()).thenReturn(List.of(fromRate));

        Assertions.assertThatThrownBy(() -> moneyConverter.convert(price, fraction, to))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.rate.not-found.by-currency-and-date"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(to, price.getDateTime());

        Mockito.verify(rateService, Mockito.times(2))
            .getAllRates();
    }
}