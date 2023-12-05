package org.cynic.spring_stuff.domain.http.price;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Set;
import org.cynic.spring_stuff.domain.model.type.PriceType;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;

public record PriceHttp(Long id,
                        ZonedDateTime dateTime,
                        Currency currency,
                        BigDecimal value,
                        PriceType type,
                        ReferenceType referenceType,
                        Set<PriceFractionHttp> fractions,
                        Set<PriceDocumentHttp> documents, Boolean covered) implements
    Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public record PriceFractionHttp(BigDecimal fraction, String comment) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

    }

    public record PriceDocumentHttp(Long id, String fileName) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
