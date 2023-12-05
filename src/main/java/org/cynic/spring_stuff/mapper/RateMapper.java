package org.cynic.spring_stuff.mapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.cynic.spring_stuff.domain.entity.Rate;
import org.cynic.spring_stuff.domain.http.rate.CreateRateHttp;
import org.cynic.spring_stuff.domain.http.rate.RateDetailsHttp;
import org.cynic.spring_stuff.domain.http.rate.RateDetailsHttp.RateDetailsSequenceHttp;
import org.cynic.spring_stuff.domain.http.rate.RateHttp;
import org.cynic.spring_stuff.domain.model.type.RateSourceType;
import org.cynic.spring_stuff.utils.BigDecimalUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class RateMapper {

    @Mapping(source = "value", target = "value")
    protected abstract RateHttp internal(Rate rate);

    protected abstract RateDetailsSequenceHttp item(Rate rate);

    protected abstract Rate internal(CreateRateHttp http, OffsetDateTime dateTime, RateSourceType source);


    public Optional<Rate> toEntity(CreateRateHttp http, OffsetDateTime dateTime, RateSourceType source) {
        return Optional.ofNullable(internal(http, dateTime, source));
    }

    public Optional<RateHttp> toHttp(Rate rate) {
        return Optional.ofNullable(internal(rate));
    }

    public Optional<RateDetailsHttp> toHttp(List<Rate> rates) {
        return Optional.ofNullable(rates)
            .filter(CollectionUtils::isNotEmpty)
            .stream()
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(Rate::getDateTime))
            .collect(
                Collectors.teeing(
                    Collectors.mapping(Rate::getCurrency, Collectors.reducing((currency, currency2) -> currency)),
                    Collectors.mapping(this::item, Collectors.toList()),
                    (currency, data) -> currency.map(it -> new RateDetailsHttp(it, data)))
            );
    }

    protected Currency convert(String currency) {
        return Currency.getInstance(currency);
    }

    protected BigDecimal convert(BigDecimal value) {
        return BigDecimalUtils.scale(value);
    }

    protected ZonedDateTime convert(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toZonedDateTime();
    }
}
