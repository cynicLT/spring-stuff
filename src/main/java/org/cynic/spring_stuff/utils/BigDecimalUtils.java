package org.cynic.spring_stuff.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class BigDecimalUtils {

    private static final Integer SCALE = 4;

    public static BigDecimal scale(BigDecimal value) {
        return Optional.ofNullable(value)
            .map(it -> it.setScale(SCALE, RoundingMode.CEILING))
            .orElse(value);
    }
}
