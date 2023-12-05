package org.cynic.spring_stuff.domain.http.price;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Set;
import org.cynic.spring_stuff.domain.model.type.PriceType;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;

public record CreatePriceHttp(
    BigDecimal value,
    ZonedDateTime dateTime,
    ZonedDateTime dueDateTime,
    Currency currency,
    PriceType type,
    Boolean covered,
    ReferenceType referenceType,
    Set<CreatePriceFractionHttp> fractions
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public record CreatePriceFractionHttp(BigDecimal fraction, String comment, Long referenceId) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
