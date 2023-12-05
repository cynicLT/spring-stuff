package org.cynic.spring_stuff.domain.http.organization;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public record OrganizationHttp(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        String comment,
        Set<ManageOrganizationHttp> managers

) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public record ManageOrganizationHttp(Long id, String name, String email, String phone,
                                         String comment) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
