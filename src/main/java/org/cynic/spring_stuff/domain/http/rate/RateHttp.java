package org.cynic.spring_stuff.domain.http.rate;

import org.cynic.spring_stuff.domain.model.type.RateSourceType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;

public record RateHttp(Long id, ZonedDateTime dateTime, Currency currency, RateSourceType source,
                       BigDecimal value) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
