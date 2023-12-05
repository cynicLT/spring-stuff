package org.cynic.spring_stuff.utils;


import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.Constants;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
    InstancioExtension.class
})
@Tag("unit")
class OffsetDateTimeUtilsTest {

    private final Clock clock = Clock.fixed(ZonedDateTime.of(
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 154000000),
                Clock.system(ZoneId.systemDefault()).getZone()
            )
            .toInstant(),
        Clock.system(ZoneId.systemDefault()).getZone()
    );

    @Test
    void convertWhenOK() {
        LocalDate now = LocalDate.now(clock);

        String value = now.format(DateTimeFormatter.ofPattern(
            Constants.DATE_FORMAT_DOT,
            Locale.getDefault()
        ));
        ZoneOffset offset = Instancio.create(ZoneOffset.class);

        Assertions.assertThat(OffsetDateTimeUtils.convert(value, offset))
            .isEqualTo(OffsetDateTime.of(now.atStartOfDay(), offset));
    }
}