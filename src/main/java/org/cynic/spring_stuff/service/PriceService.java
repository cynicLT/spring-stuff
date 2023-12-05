package org.cynic.spring_stuff.service;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.Price;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp.CreatePriceFractionHttp;
import org.cynic.spring_stuff.domain.http.price.PriceDetailsHttp;
import org.cynic.spring_stuff.domain.http.price.PriceHttp;
import org.cynic.spring_stuff.mapper.ItemOrderPriceMapper;
import org.cynic.spring_stuff.mapper.PriceMapper;
import org.cynic.spring_stuff.repository.ItemOrderPriceRepository;
import org.cynic.spring_stuff.repository.ItemRepository;
import org.cynic.spring_stuff.repository.NotificationRepository;
import org.cynic.spring_stuff.repository.OrderRepository;
import org.cynic.spring_stuff.repository.PriceRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PriceService {


    private final PriceRepository priceRepository;
    private final ItemOrderPriceRepository itemOrderPriceRepository;
    private final NotificationRepository notificationRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final PriceMapper priceMapper;
    private final ItemOrderPriceMapper itemOrderPriceMapper;

    public PriceService(
        PriceRepository priceRepository,
        ItemOrderPriceRepository itemOrderPriceRepository,
        NotificationRepository notificationRepository,
        ItemRepository itemRepository,
        OrderRepository orderRepository,
        PriceMapper priceMapper,
        ItemOrderPriceMapper itemOrderPriceMapper) {

        this.priceRepository = priceRepository;
        this.itemOrderPriceRepository = itemOrderPriceRepository;
        this.notificationRepository = notificationRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.priceMapper = priceMapper;
        this.itemOrderPriceMapper = itemOrderPriceMapper;
    }

    public List<PriceHttp> pricesBy(OidcUser oidcUser, Optional<Long> orderId) {
        return priceRepository.findAll(PriceRepository.byManagerEmailAndOrderId(oidcUser.getEmail(), orderId))
            .stream()
            .map(priceMapper::toHttpList)
            .flatMap(Optional::stream)
            .toList();
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public Long create(CreatePriceHttp http, OidcUser oidcUser) {
        Set<ItemOrderPrice> itemOrderPrices = http.fractions()
            .stream()
            .map(it -> createItemOrderPrice(http, it, oidcUser))
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());

        return priceMapper.toEntity(http, itemOrderPrices)
            .map(priceRepository::save)
            .map(Price::getId)
            .orElseThrow(() -> new ApplicationException("error.price.save"));
    }


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public void deleteBy(Long id) {
        Price price = priceRepository.findOne(PriceRepository.byIdFetchRefs(id))
            .orElseThrow(() -> new ApplicationException("error.price.not-found", id));

        price.getItemOrderPrices()
            .forEach(it -> {
                    Optional.ofNullable(it.getNotification())
                        .ifPresent(notificationRepository::delete);

                    itemOrderPriceRepository.delete(it);
                }
            );
        priceRepository.delete(price);
    }

    public PriceDetailsHttp priceBy(Long id) {
        return priceRepository.findOne(PriceRepository.byIdFetchRefs(id))
            .flatMap(priceMapper::toHttpItem)
            .orElseThrow(() -> new ApplicationException("error.price.not-found", id));
    }

    private Optional<ItemOrderPrice> createItemOrderPrice(CreatePriceHttp http, CreatePriceFractionHttp it, OidcUser oidcUser) {
        return switch (http.referenceType()) {
            case ORDER -> itemOrderPriceMapper.toEntity(
                it,
                orderRepository.findOne(OrderRepository.byManagerAndIdAndClosed(it.referenceId(), oidcUser.getEmail(), Boolean.FALSE))
                    .orElseThrow(() -> new ApplicationException("error.order.not-found", it.referenceId()))
            );
            case ITEM -> itemOrderPriceMapper.toEntity(
                it,
                itemRepository.findById(it.referenceId())
                    .orElseThrow(() -> new ApplicationException("error.item.not-found", it.referenceId()))
            );
        };
    }
}
