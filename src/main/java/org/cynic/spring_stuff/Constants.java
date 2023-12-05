package org.cynic.spring_stuff;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.time.Duration;
import java.time.ZoneId;
import java.util.TimeZone;

public final class Constants {

    public static final String PRE_AUTHORIZE_EXPRESSION = "isFullyAuthenticated()";
    public static final Marker AUDIT_MARKER = MarkerFactory.getMarker("AUDIT");
    public static final TimeZone TIME_ZONE = TimeZone.getDefault();
    public static final String CURRENCY_VALUE = "EUR";


    public static final String RATES_CACHE_NAME = "rates";
    public static final int MAX_HISTORY_RATES = 100;
    public static final ZoneId CET_ZONE_ID = ZoneId.of("CET");
    public static final ZoneId MSK_ZONE_ID = ZoneId.of("Europe/Moscow");

    public static final String DATE_FORMAT_DOT = "dd.MM.yyyy";
    public static final Duration BEFORE_DUE_DAY_EXPIRATION = Duration.ofDays(2);

    private Constants() {
    }
}
