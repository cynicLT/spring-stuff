package org.cynic.spring_stuff.domain.http.order;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

public record CreateOrderHttp(
        ZonedDateTime dateTime,
        String name,
        String description,
        Long owner,
        Long manager,
        Set<Long> items
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
