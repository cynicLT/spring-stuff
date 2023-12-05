package org.cynic.spring_stuff.domain.http.rate;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;

public record CreateRateHttp(BigDecimal value, ZonedDateTime date, Currency currency) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
