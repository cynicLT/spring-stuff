package org.cynic.spring_stuff.converter;

import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Price;
import org.cynic.spring_stuff.domain.entity.Rate;
import org.cynic.spring_stuff.service.RateService;
import org.cynic.spring_stuff.utils.BigDecimalUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class MoneyConverter {

    private final RateService rateService;

    public MoneyConverter(RateService rateService) {
        this.rateService = rateService;
    }

    public BigDecimal convert(Price price, BigDecimal fraction, Currency to) {
        return BigDecimalUtils.scale(
            Optional.of(price)
                .filter(Predicate.not(it -> price.getCurrency().equals(to)))
                .map(it -> calculate(it, fraction, to))
                .orElse(price.getValue().multiply(fraction, MathContext.DECIMAL128))
        );
    }

    private BigDecimal calculate(Price price, BigDecimal fraction, Currency to) {
        Rate fromRate = getRate(price.getCurrency(), price.getDateTime());
        Rate toRate = getRate(to, price.getDateTime());

        return price.getValue()
            .multiply(fraction, MathContext.DECIMAL128)
            .multiply(toRate.getValue(), MathContext.DECIMAL128)
            .divide(fromRate.getValue(), MathContext.DECIMAL128);

    }

    private Rate getRate(Currency currency, OffsetDateTime dateTime) {
        List<Rate> currencyRates = rateService.getAllRates()
            .stream()
            .filter(it -> currency.equals(it.getCurrency()))
            .toList();

        return currencyRates.stream()
            .filter(it -> dateTime.isBefore(it.getDateTime()))
            .min(Comparator.comparing(Rate::getDateTime).thenComparing(Comparator.comparing(Rate::getId).reversed()))
            .or(() -> currencyRates
                .stream()
                .filter(Predicate.not(it -> dateTime.isBefore(it.getDateTime())))
                .min(Comparator.comparing(Rate::getDateTime).reversed().thenComparing(Rate::getId))
            )
            .stream()
            .findAny()
            .orElseThrow(
                () -> new ApplicationException("error.rate.not-found.by-currency-and-date", currency, dateTime));
    }
}
