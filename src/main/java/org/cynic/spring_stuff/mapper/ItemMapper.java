package org.cynic.spring_stuff.mapper;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.http.item.CreateItemHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp;
import org.cynic.spring_stuff.domain.http.item.ItemHttp;
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
public abstract class ItemMapper {


    @Mapping(source = "http.name", target = "name")
    @Mapping(source = "http.description", target = "description")
    @Mapping(source = "orders", target = "orders")
    @Mapping(ignore = true, target = "itemOrderPrices")
    @Mapping(ignore = true, target = "documents")
    protected abstract Item internal(CreateItemHttp http, Set<Order> orders);

    protected abstract ItemDetailsHttp details(Item item);

    protected abstract ItemHttp simple(Item item);

    protected abstract ItemDetailsHttp.ItemDetailsOrderHttp convert(Order order);

    public Optional<ItemDetailsHttp> toItem(Item item) {
        return Optional.ofNullable(details(item));
    }

    public Optional<ItemHttp> toListItem(Item item) {
        return Optional.ofNullable(simple(item));
    }

    public Optional<Item> toEntity(CreateItemHttp http, Set<Order> orders) {
        return Optional.ofNullable(internal(http, orders));
    }

    protected ZonedDateTime convert(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toZonedDateTime();
    }

    protected Set<ItemDetailsHttp.ItemDetailsOrderHttp> convert(Set<Order> orders) {
        return orders
            .stream()
            .filter(Predicate.not(Order::getClosed))
            .map(this::convert)
            .collect(Collectors.toSet());
    }
}
