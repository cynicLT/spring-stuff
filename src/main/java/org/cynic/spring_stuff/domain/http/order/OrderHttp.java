package org.cynic.spring_stuff.domain.http.order;

import java.io.Serial;
import java.io.Serializable;

public record OrderHttp(Long id, String name, Boolean closed, String description, OrderOwnerHttp owner) implements
    Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public record OrderOwnerHttp(String name, String phone, String email, OrderOrganizationHttp organization) implements
        Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record OrderOrganizationHttp(String name, String email, String phone, String address) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
