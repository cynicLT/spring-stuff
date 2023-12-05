package org.cynic.spring_stuff.domain.http.price;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Set;
import org.cynic.spring_stuff.domain.model.type.PriceType;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;

public record PriceDetailsHttp(
    Long id,
    ZonedDateTime dateTime,
    ZonedDateTime dueDateTime,
    Currency currency,
    BigDecimal value,
    PriceType type,
    Boolean covered,
    ReferenceType referenceType,
    Set<PriceDetailsFractionHttp> fractions,
    Set<PriceDetailsDocumentHttp> documents
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public record PriceDetailsFractionHttp(BigDecimal fraction, String comment, Long referenceId) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }

    public record PriceDetailsDocumentHttp(Long id, String fileName) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
