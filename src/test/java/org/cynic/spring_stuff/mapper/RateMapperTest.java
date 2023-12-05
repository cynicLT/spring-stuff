package org.cynic.spring_stuff.mapper;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.entity.Rate;
import org.cynic.spring_stuff.domain.http.rate.CreateRateHttp;
import org.cynic.spring_stuff.domain.http.rate.RateDetailsHttp;
import org.cynic.spring_stuff.domain.http.rate.RateDetailsHttp.RateDetailsSequenceHttp;
import org.cynic.spring_stuff.domain.http.rate.RateHttp;
import org.cynic.spring_stuff.domain.model.type.RateSourceType;
import org.cynic.spring_stuff.utils.BigDecimalUtils;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith({
    MockitoExtension.class,
    InstancioExtension.class
})
@Tag("unit")
class RateMapperTest {

    private RateMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = new RateMapperImpl();
    }


    @Test
    void toHttpWhenOK() {
        Rate rate = Instancio.create(Rate.class);

        Optional<RateHttp> actual = mapper.toHttp(rate);

        RateHttp expected = new RateHttp(rate.getId(), rate.getDateTime().toZonedDateTime(), rate.getCurrency(), rate.getSource(),
            BigDecimalUtils.scale(rate.getValue()));

        Assertions.<Optional<RateHttp>>assertThat(actual)
            .extracting(Optional::get)
            .isEqualTo(expected);
    }

    @Test
    void toHttpDetailedWhenOk() {
        Rate rateNew = Instancio.of(Rate.class)
            .set(Select.field("currency"), Currency.getInstance("EUR"))
            .generate(Select.field("dateTime"), it -> it.temporal().offsetDateTime().future())
            .create();
        Rate rateOld = Instancio.of(Rate.class)
            .set(Select.field("currency"), Currency.getInstance("EUR"))
            .generate(Select.field("dateTime"), it -> it.temporal().offsetDateTime().past())
            .create();

        RateDetailsHttp expected = new RateDetailsHttp(
            rateNew.getCurrency(),
            List.of(
                new RateDetailsSequenceHttp(rateOld.getId(), BigDecimalUtils.scale(rateOld.getValue()), rateOld.getDateTime().toZonedDateTime(),
                    rateOld.getSource()),
                new RateDetailsSequenceHttp(rateNew.getId(), BigDecimalUtils.scale(rateNew.getValue()), rateNew.getDateTime().toZonedDateTime(),
                    rateNew.getSource())
            )
        );

        Assertions.<Optional<RateDetailsHttp>>assertThat(mapper.toHttp(List.of(rateNew, rateOld)))
            .extracting(Optional::get)
            .isEqualTo(expected);
    }

    @Test
    void toEntityWhenOK2() {
        CreateRateHttp http = Instancio.create(CreateRateHttp.class);
        RateSourceType source = Instancio.create(RateSourceType.class);

        Rate expected = new Rate();
        expected.setDateTime(http.date().toOffsetDateTime());
        expected.setValue(BigDecimalUtils.scale(http.value()));
        expected.setCurrency(http.currency());
        expected.setSource(source);

        Assertions.<Optional<Rate>>assertThat(mapper.toEntity(http, http.date().toOffsetDateTime(), source))
            .extracting(Optional::get)
            .usingRecursiveComparison()
            .comparingOnlyFields(
                "value",
                "currency",
                "dateTime",
                "source"
            )
            .isEqualTo(expected);
    }
}
