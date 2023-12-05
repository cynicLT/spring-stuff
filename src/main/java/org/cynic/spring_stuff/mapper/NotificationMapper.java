package org.cynic.spring_stuff.mapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Notification;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.http.NotificationHttp;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;
import org.cynic.spring_stuff.utils.BigDecimalUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class NotificationMapper {

    @Mapping(constant = "false", target = "visit")
    @Mapping(ignore = true, target = "manager")
    protected abstract Notification internal(OffsetDateTime dateTime, ItemOrderPrice itemOrderPrice);

    @Mapping(source = "itemOrderPrice", target = "referenceId", qualifiedByName = "referenceId")
    @Mapping(source = "itemOrderPrice", target = "referenceType", qualifiedByName = "referenceType")
    @Mapping(source = "itemOrderPrice.price.dueDateTime", target = "dueDateTime")

    @Mapping(source = "itemOrderPrice.price.type", target = "price.type")
    @Mapping(source = "itemOrderPrice.price.currency", target = "price.currency")
    @Mapping(source = "itemOrderPrice.price.value", target = "price.value", qualifiedByName = "value")
    protected abstract NotificationHttp internal(Notification notification);


    @Mapping(ignore = true, target = "dateTime")
    @Mapping(ignore = true, target = "itemOrderPrice")
    protected abstract Notification internal(@MappingTarget Notification notification, Manager manager, Boolean visit);

    public Optional<Notification> toEntity(Notification notification, Manager manager, Boolean visit) {
        return Optional.ofNullable(internal(notification, manager, visit));
    }

    public Optional<Notification> toEntity(OffsetDateTime dateTime, ItemOrderPrice itemOrderPrice) {
        return Optional.ofNullable(internal(dateTime, itemOrderPrice));
    }

    public Optional<NotificationHttp> toHttp(Notification notification) {
        return Optional.ofNullable(internal(notification));
    }

    protected ZonedDateTime convert(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toZonedDateTime();
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
    protected ReferenceType referenceType(ItemOrderPrice itemOrderPrice) {
        return Optional.of(itemOrderPrice)
            .map(ItemOrderPrice::getItem)
            .map(it -> ReferenceType.ITEM)
            .orElse(ReferenceType.ORDER);
    }

    @Named("value")
    protected BigDecimal value(BigDecimal value) {
        return BigDecimalUtils.scale(value);
    }


}