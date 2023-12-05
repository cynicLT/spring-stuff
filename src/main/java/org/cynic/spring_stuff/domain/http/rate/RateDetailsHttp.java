package org.cynic.spring_stuff.domain.http.rate;

import org.cynic.spring_stuff.domain.model.type.RateSourceType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;

public record RateDetailsHttp(Currency currency, List<RateDetailsSequenceHttp> data) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public record RateDetailsSequenceHttp(Long id, BigDecimal value, ZonedDateTime dateTime,
                                          RateSourceType source) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

    }
}
