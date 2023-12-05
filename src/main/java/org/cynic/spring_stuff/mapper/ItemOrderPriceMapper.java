package org.cynic.spring_stuff.mapper;

import java.util.Optional;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp.CreatePriceFractionHttp;
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
public abstract class ItemOrderPriceMapper {

    @Mapping(target = "price", ignore = true)
    @Mapping(target = "item", ignore = true)
    protected abstract ItemOrderPrice internal(CreatePriceFractionHttp http, Order order);


    @Mapping(target = "price", ignore = true)
    @Mapping(target = "order", ignore = true)
    protected abstract ItemOrderPrice internal(CreatePriceFractionHttp http, Item item);

    public Optional<ItemOrderPrice> toEntity(CreatePriceFractionHttp http, Order order) {
        return Optional.ofNullable(internal(http, order));
    }

    public Optional<ItemOrderPrice> toEntity(CreatePriceFractionHttp http, Item item) {
        return Optional.ofNullable(internal(http, item));
    }
}
