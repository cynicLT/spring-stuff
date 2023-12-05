package org.cynic.spring_stuff.mapper;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.Price;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp;
import org.cynic.spring_stuff.domain.http.price.PriceDetailsHttp;
import org.cynic.spring_stuff.domain.http.price.PriceDetailsHttp.PriceDetailsDocumentHttp;
import org.cynic.spring_stuff.domain.http.price.PriceDetailsHttp.PriceDetailsFractionHttp;
import org.cynic.spring_stuff.domain.http.price.PriceHttp;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;
import org.cynic.spring_stuff.utils.BigDecimalUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PriceMapperTest {

    private PriceMapper mapper;

    @BeforeEach
    void setUp() {
        this.mapper = new PriceMapperImpl();
    }

    @Test
    void toHttpListWhenOK() {
        Price price = Instancio.create(Price.class);

        PriceHttp expected = new PriceHttp(
            price.getId(),
            price.getDateTime().toZonedDateTime(),
            price.getCurrency(),
            BigDecimalUtils.scale(price.getValue()),
            price.getType(),
            price.getItemOrderPrices()
                .stream()
                .filter(it -> Objects.nonNull(it.getItem()))
                .findAny()
                .map(it -> ReferenceType.ITEM)
                .orElse(ReferenceType.ORDER),
            price.getItemOrderPrices()
                .stream()
                .map(it -> new PriceHttp.PriceFractionHttp(BigDecimalUtils.scale(it.getFraction()), it.getComment()))
                .collect(Collectors.toSet()),
            price.getDocuments()
                .stream()
                .map(it -> new PriceHttp.PriceDocumentHttp(it.getId(), it.getFileName()))
                .collect(Collectors.toSet()),
            price.getCovered()
        );

        Assertions.<Optional<PriceHttp>>assertThat(mapper.toHttpList(price))
            .extracting(Optional::get)
            .isEqualTo(expected);
    }

    @Test
    void toHttpItemWhenOk() {
        Price price = Instancio.create(Price.class);

        PriceDetailsHttp expected = new PriceDetailsHttp(
            price.getId(),
            price.getDateTime().toZonedDateTime(),
            price.getDueDateTime().toZonedDateTime(),
            price.getCurrency(),
            BigDecimalUtils.scale(price.getValue()),
            price.getType(),
            price.getCovered(),
            price.getItemOrderPrices()
                .stream()
                .filter(it -> Objects.nonNull(it.getItem()))
                .findAny()
                .map(i -> ReferenceType.ITEM)
                .orElse(ReferenceType.ORDER),
            price.getItemOrderPrices()
                .stream()
                .map(it -> new PriceDetailsFractionHttp(BigDecimalUtils.scale(it.getFraction()),
                    it.getComment(),
                    Optional.of(it)
                        .map(ItemOrderPrice::getItem)
                        .map(Item::getId)
                        .orElse(it.getOrder().getId())
                ))
                .collect(Collectors.toSet()),
            price.getDocuments()
                .stream()
                .map(it -> new PriceDetailsDocumentHttp(it.getId(), it.getFileName()))
                .collect(Collectors.toSet())
        );

        Assertions.<Optional<PriceDetailsHttp>>assertThat(mapper.toHttpItem(price))
            .extracting(Optional::get)
            .isEqualTo(expected);
    }

    @Test
    void toEntityWhenOK() {
        CreatePriceHttp http = Instancio.create(CreatePriceHttp.class);

        Price expected = new Price();
        expected.setValue(http.value());
        expected.setCovered(http.covered());
        expected.setCurrency(http.currency());
        expected.setDueDateTime(http.dueDateTime().toOffsetDateTime());
        expected.setDateTime(http.dateTime().toOffsetDateTime());

        Assertions.<Optional<Price>>assertThat(mapper.toEntity(http, Set.of()))
            .extracting(Optional::get)
            .usingRecursiveComparison()
            .comparingOnlyFields(
                "value",
                "currency",
                "dueDateTime",
                "dateTime"
            )
            .isEqualTo(expected);
    }
}