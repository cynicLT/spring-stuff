package org.cynic.spring_stuff.domain.http.item;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

public record ItemDetailsHttp(Long id,
                              String name,
                              String description,
                              Set<ItemDetailsOrderHttp> orders,
                              Set<ItemDetailsDocumentHttp> documents) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public record ItemDetailsOrderHttp(Long id,
                                       ZonedDateTime dateTime,
                                       String name,
                                       String description,
                                       ItemDetailsManagerHttp manager) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record ItemDetailsDocumentHttp(Long id, String fileName) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record ItemDetailsManagerHttp(String name, String phone, String email, ItemDetailsOrganizationHttp organization) implements
        Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record ItemDetailsOrganizationHttp(String name, String email, String phone, String address) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
