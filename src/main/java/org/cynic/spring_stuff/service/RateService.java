package org.cynic.spring_stuff.service;

import jakarta.persistence.LockModeType;
import org.apache.commons.collections4.IterableUtils;
import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Rate;
import org.cynic.spring_stuff.domain.http.rate.CreateRateHttp;
import org.cynic.spring_stuff.domain.http.rate.RateDetailsHttp;
import org.cynic.spring_stuff.domain.http.rate.RateHttp;
import org.cynic.spring_stuff.domain.model.type.RateSourceType;
import org.cynic.spring_stuff.mapper.RateMapper;
import org.cynic.spring_stuff.repository.RateRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class RateService {

    private final RateRepository rateRepository;
    private final RateMapper rateMapper;

    public RateService(RateRepository rateRepository, RateMapper rateMapper) {
        this.rateRepository = rateRepository;
        this.rateMapper = rateMapper;
    }

    @Cacheable(cacheNames = Constants.RATES_CACHE_NAME)
    public List<Rate> getAllRates() {
        return IterableUtils.toList(rateRepository.findAll());
    }

    public List<RateHttp> items(Optional<ZonedDateTime> date) {

        return rateRepository.findAll(RateRepository.latestRates(date.map(ZonedDateTime::toOffsetDateTime)))
                .stream()
                .map(rateMapper::toHttp)
                .flatMap(Optional::stream)
                .toList();
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public void deleteBy(Long id) {
        Rate rate = rateRepository.findByIdAndSource(id, RateSourceType.USR)
                .orElseThrow(() -> new ApplicationException("error.rate.not-found", id));

        rateRepository.delete(rate);
    }

    public RateDetailsHttp itemBy(Currency currency) {
        return rateMapper.toHttp(
                        rateRepository.findAll(
                                RateRepository.history(currency),
                                Pageable.ofSize(Constants.MAX_HISTORY_RATES)
                        ).toList()
                )
                .orElseThrow(() -> new ApplicationException("error.rate.not-found", currency));
    }

    @Transactional
    public Long create(CreateRateHttp http) {
        OffsetDateTime dateTime = http.date().toOffsetDateTime();

        return Optional.of(http)
                .filter(Predicate.not(
                        it -> rateRepository.existsByCurrencyAndDateTime(it.currency(), dateTime)
                ))
                .map(it -> rateMapper.toEntity(it, dateTime, RateSourceType.USR)
                        .map(rateRepository::save)
                        .map(Rate::getId)
                        .orElseThrow(() -> new ApplicationException("error.rate.create"))
                )
                .orElseThrow(() -> new ApplicationException("error.rate.exists", http.currency(), dateTime));
    }
}
