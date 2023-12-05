package org.cynic.spring_stuff.domain.http;

import org.cynic.spring_stuff.domain.model.type.PriceType;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;

public record NotificationHttp(Long id, ZonedDateTime dateTime, ZonedDateTime dueDateTime, Boolean visit, ReferenceType referenceType,
                               Long referenceId, NotificationManagerHttp manager, NotificationPriceHttp price) implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    public record NotificationManagerHttp(String name, String phone, String email, NotificationOrganizationHttp organization) implements
        Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record NotificationPriceHttp(PriceType type, Currency currency, BigDecimal value) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record NotificationOrganizationHttp(String name, String email, String phone, String address) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }


}
