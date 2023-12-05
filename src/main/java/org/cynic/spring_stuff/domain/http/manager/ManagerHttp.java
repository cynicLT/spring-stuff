package org.cynic.spring_stuff.domain.http.manager;

import java.io.Serial;
import java.io.Serializable;

public record ManagerHttp(Long id, String name, String phone, String email, ManagerOrganizationHttp organization) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    public record ManagerOrganizationHttp(String name, String email, String phone, String address) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
