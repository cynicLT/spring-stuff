package org.cynic.spring_stuff.domain.http.order;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Set;

public record OrderDetailsHttp(Long id,
                               ZonedDateTime dateTime,
                               String name,
                               String description,
                               Currency currency,
                               Boolean closed,
                               OrderDetailsManagerHttp owner,
                               OrderDetailsManagerHttp manager,
                               OrderDetailsPriceHttp expenses,
                               OrderDetailsPriceHttp earnings,
                               Set<OrderDetailsDocumentHttp> documents,
                               Set<OrderDetailsItemHttp> items
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public record OrderDetailsManagerHttp(String name, String phone, String email, OrderDetailsOrganizationHttp organization) implements
        Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record OrderDetailsDocumentHttp(Long id, String fileName) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record OrderDetailsPriceHttp(BigDecimal total, BigDecimal covered) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record OrderDetailsItemHttp(Long id, String name) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }


    public record OrderDetailsOrganizationHttp(String name, String email, String phone, String address) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
