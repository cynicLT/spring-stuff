package org.cynic.spring_stuff.domain.http.item;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record CreateItemHttp(String name, String description, List<Long> orders) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
