package org.cynic.spring_stuff.service;

import jakarta.persistence.LockModeType;
import org.apache.commons.collections4.CollectionUtils;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.http.order.CreateOrderHttp;
import org.cynic.spring_stuff.domain.http.order.OrderDetailsHttp;
import org.cynic.spring_stuff.domain.http.order.OrderHttp;
import org.cynic.spring_stuff.mapper.OrderMapper;
import org.cynic.spring_stuff.repository.*;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderService {

    private final ManagerRepository managerRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final ItemOrderPriceRepository itemOrderPriceRepository;
    private final NotificationRepository notificationRepository;
    private final OrderMapper orderMapper;

    public OrderService(
        ManagerRepository managerRepository,
        ItemRepository itemRepository,
        OrderRepository orderRepository,
        ItemOrderPriceRepository itemOrderPriceRepository,
        NotificationRepository notificationRepository,
        OrderMapper orderMapper) {

        this.managerRepository = managerRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.itemOrderPriceRepository = itemOrderPriceRepository;
        this.notificationRepository = notificationRepository;
        this.orderMapper = orderMapper;
    }


    public List<OrderHttp> ordersBy(OidcUser oidcUser, Optional<Boolean> closed) {
        return orderRepository.findAll(OrderRepository.byManagerAndClosed(oidcUser.getEmail(), closed))
            .stream()
            .map(orderMapper::toListItem)
            .flatMap(Optional::stream)
            .toList();
    }

    public OrderDetailsHttp orderDetailsBy(Long id, Currency currency) {
        return orderRepository.findById(id)
            .flatMap(it -> orderMapper.toItem(it, currency))
            .orElseThrow(() -> new ApplicationException("error.order.not-found", id));
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    public void deleteBy(Long id, OidcUser user) {
        Order order = orderRepository.findOne(OrderRepository.byManagerAndIdAndClosed(id, user.getEmail(), Boolean.TRUE))
            .orElseThrow(() -> new ApplicationException("error.order.not-found", id));

        order.getItemOrderPrices()
            .forEach(it -> {
                Optional.of(it)
                    .map(ItemOrderPrice::getNotification)
                    .ifPresent(notificationRepository::delete);

                itemOrderPriceRepository.delete(it);
            });

        orderRepository.delete(order);
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    public Long create(CreateOrderHttp http) {
        Set<Item> items = itemRepository.findAllByIdIn(http.items());

        Optional.of(
                CollectionUtils.disjunction(http.items(), items.stream()
                    .map(Item::getId)
                    .collect(Collectors.toSet()))
            )
            .filter(CollectionUtils::isNotEmpty)
            .ifPresent(ids -> {
                throw new ApplicationException("error.order.items.not-found", ids.toArray());
            });

        Manager manager = managerRepository.findById(http.manager())
            .orElseThrow(() -> new ApplicationException("error.order.manager.not-found", http.manager()));

        Manager owner = managerRepository.findById(http.owner())
            .orElseThrow(() -> new ApplicationException("error.order.owner.not-found", http.owner()));

        return orderMapper.toEntity(http, items, manager, owner)
            .map(orderRepository::save)
            .map(Order::getId)
            .orElseThrow(() -> new ApplicationException("error.order.create"));
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    public void closeBy(Long id, OidcUser oidcUser) {
        Order order = orderRepository.findOne(OrderRepository.byManagerAndIdAndClosed(id, oidcUser.getEmail(), Boolean.FALSE))
            .orElseThrow(() -> new ApplicationException("error.order.not-found", id, oidcUser.getEmail()));

        orderMapper.toEntity(order, Boolean.TRUE)
            .ifPresentOrElse(orderRepository::save,
                () -> {
                    throw new ApplicationException("error.order.save", id);
                });
    }
}
