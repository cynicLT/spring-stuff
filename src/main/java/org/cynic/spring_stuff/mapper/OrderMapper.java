package org.cynic.spring_stuff.mapper;

import org.cynic.spring_stuff.converter.MoneyConverter;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.http.order.CreateOrderHttp;
import org.cynic.spring_stuff.domain.http.order.OrderDetailsHttp;
import org.cynic.spring_stuff.domain.http.order.OrderDetailsHttp.OrderDetailsPriceHttp;
import org.cynic.spring_stuff.domain.http.order.OrderHttp;
import org.cynic.spring_stuff.domain.model.type.PriceType;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class OrderMapper {

    private MoneyConverter moneyConverter;

    @Autowired
    public void setMoneyService(MoneyConverter moneyConverter) {
        this.moneyConverter = moneyConverter;
    }

    protected abstract OrderHttp item(Order order);

    @Mapping(ignore = true, target = "dateTime")
    @Mapping(ignore = true, target = "name")
    @Mapping(ignore = true, target = "description")
    @Mapping(ignore = true, target = "owner")
    @Mapping(ignore = true, target = "manager")
    @Mapping(ignore = true, target = "items")
    @Mapping(ignore = true, target = "documents")
    @Mapping(ignore = true, target = "itemOrderPrices")
    protected abstract Order internal(@MappingTarget Order order, Boolean closed);

    @Mapping(source = "order", target = "earnings", qualifiedByName = "earnings")
    @Mapping(source = "order", target = "expenses", qualifiedByName = "expenses")
    @Mapping(expression = "java(currency)", target = "currency")
    protected abstract OrderDetailsHttp details(Order order, @Context Currency currency);

    @Mapping(source = "http.name", target = "name")
    @Mapping(source = "http.description", target = "description")
    @Mapping(source = "http.dateTime", target = "dateTime")
    @Mapping(source = "manager", target = "manager")
    @Mapping(constant = "false", target = "closed")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "items", target = "items")
    @Mapping(ignore = true, target = "documents")
    @Mapping(ignore = true, target = "itemOrderPrices")
    protected abstract Order internal(CreateOrderHttp http, Set<Item> items, Manager manager, Manager owner);

    public Optional<Order> toEntity(Order order, Boolean closed) {
        return Optional.ofNullable(internal(order, closed));
    }

    public Optional<OrderHttp> toListItem(Order order) {
        return Optional.of(item(order));
    }


    public Optional<Order> toEntity(CreateOrderHttp http, Set<Item> items, Manager manager, Manager owner) {
        return Optional.ofNullable(internal(http, items, manager, owner));
    }

    public Optional<OrderDetailsHttp> toItem(Order order, Currency currency) {
        return Optional.ofNullable(details(order, currency));
    }

    @Named("earnings")
    protected OrderDetailsPriceHttp earningsDetails(Order order, @Context Currency currency) {
        return internal(order, PriceType.INCOME, currency);
    }

    @Named("expenses")
    protected OrderDetailsPriceHttp expensesDetails(Order order, @Context Currency currency) {
        return internal(order, PriceType.SPENT, currency);
    }

    private OrderDetailsPriceHttp internal(Order order, PriceType type, Currency currency) {
        return
            Stream.concat(
                    order.getItemOrderPrices()
                        .stream()
                        .map(it -> Map.entry(it.getFraction(), it.getPrice())),

                    order.getItems()
                        .stream()
                        .map(Item::getItemOrderPrices)
                        .flatMap(Collection::stream)
                        .map(it -> Map.entry(it.getFraction(), it.getPrice()))
                )
                .filter(it -> type.equals(it.getValue().getType()))
                .collect(Collectors.groupingBy(it -> it.getValue().getId(), Collectors.toSet()))
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(it -> Map.entry(
                    it.getValue().getCovered(),
                    moneyConverter.convert(it.getValue(), it.getKey(), currency)
                ))
                .collect(
                    Collectors.teeing(
                        Collectors.mapping(
                            Entry::getValue,
                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        ),
                        Collectors.filtering(Map.Entry::getKey,
                            Collectors.mapping(
                                Entry::getValue,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                            )
                        ),
                        OrderDetailsPriceHttp::new
                    )
                );
    }

    protected ZonedDateTime convert(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toZonedDateTime();
    }

    protected OffsetDateTime convert(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toOffsetDateTime();
    }
}
