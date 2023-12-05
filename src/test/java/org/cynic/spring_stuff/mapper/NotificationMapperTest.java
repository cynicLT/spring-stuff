package org.cynic.spring_stuff.mapper;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Notification;
import org.cynic.spring_stuff.domain.http.NotificationHttp;
import org.cynic.spring_stuff.domain.http.NotificationHttp.NotificationOrganizationHttp;
import org.cynic.spring_stuff.domain.http.NotificationHttp.NotificationPriceHttp;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;
import org.cynic.spring_stuff.utils.BigDecimalUtils;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.OffsetDateTime;
import java.util.Optional;

@ExtendWith({
    InstancioExtension.class
})
@Tag("unit")
class NotificationMapperTest {

    private NotificationMapper mapper;


    @BeforeEach
    void setUp() {
        mapper = new NotificationMapperImpl();
    }


    @Test
    void toEntityWhenOk() {
        OffsetDateTime dateTime = Instancio.create(OffsetDateTime.class);
        ItemOrderPrice itemOrderPrice = Instancio.create(ItemOrderPrice.class);

        Notification expected = new Notification();
        expected.setDateTime(dateTime);
        expected.setItemOrderPrice(itemOrderPrice);
        expected.setVisit(false);

        Assertions.<Optional<Notification>>assertThat(mapper.toEntity(dateTime, itemOrderPrice))
            .extracting(Optional::get)
            .matches(it -> itemOrderPrice.equals(it.getItemOrderPrice()))
            .usingRecursiveComparison()
            .comparingOnlyFields(
                "dateTime",
                "visit"
            )
            .isEqualTo(expected);
    }

    @Test
    void toHttpWhenOkItem() {
        ItemOrderPrice itemOrderPrice = Instancio.of(ItemOrderPrice.class)
            .ignore(Select.field("order"))
            .create();

        Notification notification = Instancio.of(Notification.class)
            .set(Select.field("itemOrderPrice"), itemOrderPrice)
            .create();

        NotificationHttp expected = new NotificationHttp(
            notification.getId(),
            notification.getDateTime().toZonedDateTime(),
            notification.getItemOrderPrice().getPrice().getDueDateTime().toZonedDateTime(),
            notification.getVisit(),
            ReferenceType.ITEM,
            notification.getItemOrderPrice().getItem().getId(),
            new NotificationHttp.NotificationManagerHttp(
                notification.getManager().getName(),
                notification.getManager().getPhone(),
                notification.getManager().getEmail(),
                new NotificationOrganizationHttp(
                    notification.getManager().getOrganization().getName(),
                    notification.getManager().getOrganization().getEmail(),
                    notification.getManager().getOrganization().getPhone(),
                    notification.getManager().getOrganization().getAddress()
                )
            ),
            new NotificationPriceHttp(
                notification.getItemOrderPrice().getPrice().getType(),
                notification.getItemOrderPrice().getPrice().getCurrency(),
                BigDecimalUtils.scale(notification.getItemOrderPrice().getPrice().getValue())
            )
        );

        Assertions.<Optional<NotificationHttp>>assertThat(mapper.toHttp(notification))
            .extracting(Optional::get)
            .isEqualTo(expected);
    }


    @Test
    void toHttpWhenOkOrder() {
        ItemOrderPrice itemOrderPrice = Instancio.of(ItemOrderPrice.class)
            .ignore(Select.field("item"))
            .create();

        Notification notification = Instancio.of(Notification.class)
            .set(Select.field("itemOrderPrice"), itemOrderPrice)
            .create();

        NotificationHttp expected = new NotificationHttp(
            notification.getId(),
            notification.getDateTime().toZonedDateTime(),
            notification.getItemOrderPrice().getPrice().getDueDateTime().toZonedDateTime(),
            notification.getVisit(),
            ReferenceType.ORDER,
            notification.getItemOrderPrice().getOrder().getId(),
            new NotificationHttp.NotificationManagerHttp(
                notification.getManager().getName(),
                notification.getManager().getPhone(),
                notification.getManager().getEmail(),
                new NotificationOrganizationHttp(
                    notification.getManager().getOrganization().getName(),
                    notification.getManager().getOrganization().getEmail(),
                    notification.getManager().getOrganization().getPhone(),
                    notification.getManager().getOrganization().getAddress()
                )
            ),
            new NotificationPriceHttp(
                notification.getItemOrderPrice().getPrice().getType(),
                notification.getItemOrderPrice().getPrice().getCurrency(),
                BigDecimalUtils.scale(notification.getItemOrderPrice().getPrice().getValue())
            )
        );

        Assertions.<Optional<NotificationHttp>>assertThat(mapper.toHttp(notification))
            .extracting(Optional::get)
            .isEqualTo(expected);
    }

    @Test
    void toHttpWhenError() {
        ItemOrderPrice itemOrderPrice = Instancio.of(ItemOrderPrice.class)
            .ignore(Select.field("item"))
            .ignore(Select.field("order"))
            .create();

        Notification notification = Instancio.of(Notification.class)
            .set(Select.field("itemOrderPrice"), itemOrderPrice)
            .create();

        Assertions.assertThatThrownBy(() -> mapper.toHttp(notification))
            .asInstanceOf(InstanceOfAssertFactories.throwable(ApplicationException.class))
            .matches(it -> StringUtils.equals(it.getCode(), "error.reference-id.not-found"))
            .extracting(ApplicationException::getValues)
            .asInstanceOf(InstanceOfAssertFactories.ARRAY)
            .containsExactly(itemOrderPrice.getId());
    }

    @Test
    void toEntityWhenOk2() {
        Notification notification = Instancio.create(Notification.class);
        Manager manager = Instancio.create(Manager.class);
        Boolean visit = Instancio.create(Boolean.class);

        Notification expected = new Notification();
        expected.setVisit(visit);
        expected.setManager(manager);

        Assertions.<Optional<Notification>>assertThat(mapper.toEntity(notification, manager, visit))
            .extracting(Optional::get)
            .usingRecursiveComparison()
            .comparingOnlyFields(
                "visit",
                "manager"
            )
            .isEqualTo(expected);
    }
}