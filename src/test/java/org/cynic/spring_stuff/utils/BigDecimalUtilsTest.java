package org.cynic.spring_stuff.utils;

import java.math.BigDecimal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@Tag("unit")
class BigDecimalUtilsTest {

    @Test
    void scaleWhenOK() {
        BigDecimal value = BigDecimal.valueOf(155788.111235);
        BigDecimal expected = BigDecimal.valueOf(155788.1113);

        Assertions.assertThat(BigDecimalUtils.scale(value))
            .isEqualTo(expected);
    }

    @Test
    void scaleWhenOKNull() {

        Assertions.assertThat(BigDecimalUtils.scale(null))
            .isNull();
    }
}