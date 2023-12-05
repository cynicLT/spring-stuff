package org.cynic.spring_stuff.domain.http.item;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

public record ItemHttp(Long id,
                       String name,
                       String description,
                       Set<ItemOrderHttp> orders) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public record ItemOrderHttp(Long id,
                                ZonedDateTime dateTime,
                                String name,
                                String description) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
