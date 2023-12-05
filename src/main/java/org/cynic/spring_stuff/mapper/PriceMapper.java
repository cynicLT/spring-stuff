package org.cynic.spring_stuff.mapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.entity.Price;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp;
import org.cynic.spring_stuff.domain.http.price.PriceDetailsHttp;
import org.cynic.spring_stuff.domain.http.price.PriceDetailsHttp.PriceDetailsFractionHttp;
import org.cynic.spring_stuff.domain.http.price.PriceHttp;
import org.cynic.spring_stuff.domain.http.price.PriceHttp.PriceFractionHttp;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;
import org.cynic.spring_stuff.utils.BigDecimalUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class PriceMapper {

    @Mapping(source = "value", target = "value", qualifiedByName = "value")
    @Mapping(source = "itemOrderPrices", target = "fractions")
    @Mapping(source = "itemOrderPrices", target = "referenceType", qualifiedByName = "referenceType")
    protected abstract PriceHttp listItem(Price entity);

    @Mapping(source = "value", target = "value", qualifiedByName = "value")
    @Mapping(source = "itemOrderPrices", target = "fractions")
    @Mapping(source = "itemOrderPrices", target = "referenceType", qualifiedByName = "referenceType")
    protected abstract PriceDetailsHttp item(Price entity);


    @Mapping(target = "documents", ignore = true)
    protected abstract Price internal(CreatePriceHttp http, Set<ItemOrderPrice> itemOrderPrices);

    @Mapping(source = "fraction", target = "fraction", qualifiedByName = "value")
    protected abstract PriceFractionHttp fractionList(ItemOrderPrice itemOrderPrice);

    @Mapping(source = "fraction", target = "fraction", qualifiedByName = "value")
    @Mapping(source = "itemOrderPrice", target = "referenceId", qualifiedByName = "referenceId")
    protected abstract PriceDetailsFractionHttp fractionItem(ItemOrderPrice itemOrderPrice);

    public Optional<PriceHttp> toHttpList(Price entity) {
        return Optional.ofNullable(listItem(entity));
    }

    public Optional<PriceDetailsHttp> toHttpItem(Price price) {
        return Optional.ofNullable(item(price));
    }

    public Optional<Price> toEntity(CreatePriceHttp http, Set<ItemOrderPrice> itemOrderPrices) {
        return Optional.ofNullable(internal(http, itemOrderPrices));
    }


    @Named("referenceId")
    protected Long referenceId(ItemOrderPrice itemOrderPrice) {
        return Optional.of(itemOrderPrice)
            .map(ItemOrderPrice::getItem)
            .map(Item::getId)
            .or(() -> Optional.of(itemOrderPrice)
                .map(ItemOrderPrice::getOrder)
                .map(Order::getId))
            .orElseThrow(() -> new ApplicationException("error.reference-id.not-found", itemOrderPrice.getId()));
    }

    @Named("referenceType")
    protected ReferenceType referenceType(Set<ItemOrderPrice> itemOrderPrices) {
        return itemOrderPrices.stream()
            .filter(it -> Objects.nonNull(it.getItem()))
            .findAny()
            .map(it -> ReferenceType.ITEM)
            .orElse(ReferenceType.ORDER);
    }

    @Named("value")
    protected BigDecimal value(BigDecimal value) {
        return BigDecimalUtils.scale(value);
    }

    protected ZonedDateTime convert(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toZonedDateTime();
    }

    protected OffsetDateTime convert(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toOffsetDateTime();
    }
}
