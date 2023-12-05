package org.cynic.spring_stuff.utils;

import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.ApplicationException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public final class OffsetDateTimeUtils {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            Constants.DATE_FORMAT_DOT,
            Locale.getDefault()
    );

    private OffsetDateTimeUtils() {
    }

    public static OffsetDateTime convert(String value, ZoneOffset offset) {
        return Optional.ofNullable(value)
                .map(it -> DATE_TIME_FORMATTER.parse(it, LocalDate::from))
                .map(it -> OffsetDateTime.of(it.atStartOfDay(), offset))
                .orElseThrow(() -> new ApplicationException("error.offset-date-time.convert", value, offset));
    }
}
