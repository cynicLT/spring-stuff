package org.cynic.spring_stuff.service;

import jakarta.persistence.LockModeType;
import org.cynic.spring_stuff.domain.ApplicationException;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.http.item.CreateItemHttp;
import org.cynic.spring_stuff.domain.http.item.ItemDetailsHttp;
import org.cynic.spring_stuff.domain.http.item.ItemHttp;
import org.cynic.spring_stuff.mapper.ItemMapper;
import org.cynic.spring_stuff.repository.ItemRepository;
import org.cynic.spring_stuff.repository.OrderRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class ItemService {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final ItemMapper itemMapper;

    public ItemService(ItemRepository itemRepository,
        OrderRepository orderRepository,
        ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.itemMapper = itemMapper;
    }

    public ItemDetailsHttp itemBy(Long id) {
        return itemRepository.findById(id)
            .flatMap(itemMapper::toItem)
            .orElseThrow(() -> new ApplicationException("error.item.not-found", id));
    }

    public List<ItemHttp> itemsBy(OidcUser oidcUser) {
        return itemRepository.findAllByOrdersIsNullOrOrdersManagerEmail(oidcUser.getEmail())
            .stream()
            .map(itemMapper::toListItem)
            .flatMap(Optional::stream)
            .toList();
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    public Long create(CreateItemHttp http) {
        Item item = itemMapper.toEntity(http, orderRepository.findAllByIdIn(http.orders()))
            .map(itemRepository::save)
            .orElseThrow(() -> new ApplicationException("error.item.create"));

        orderRepository.saveAll(item.getOrders());

        return item.getId();
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    public void deleteBy(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ApplicationException("error.item.not-found", id));

        item.getOrders().forEach(it -> {
            it.getItems().remove(item);

            orderRepository.save(it);
        });

        itemRepository.delete(item);
    }
}
