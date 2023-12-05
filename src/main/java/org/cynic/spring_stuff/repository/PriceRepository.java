package org.cynic.spring_stuff.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.SetJoin;
import java.util.Optional;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice_;
import org.cynic.spring_stuff.domain.entity.Item_;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Manager_;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.entity.Order_;
import org.cynic.spring_stuff.domain.entity.Price;
import org.cynic.spring_stuff.domain.entity.Price_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends CrudRepository<Price, Long>, JpaSpecificationExecutor<Price> {

    @SuppressWarnings("unchecked")
    static Specification<Price> byManagerEmailAndOrderId(String email, Optional<Long> orderId) {
        return (root, query, criteria) -> {
            SetJoin<Price, ItemOrderPrice> itemOrderPrices = (SetJoin<Price, ItemOrderPrice>) root
                .fetch(Price_.itemOrderPrices, JoinType.LEFT);
            SetJoin<Item, Order> itemOrders = ((Join<ItemOrderPrice, Item>) itemOrderPrices
                .fetch(ItemOrderPrice_.item, JoinType.LEFT))
                .join(Item_.orders, JoinType.LEFT);
            Join<ItemOrderPrice, Order> orders = (Join<ItemOrderPrice, Order>) itemOrderPrices
                .fetch(ItemOrderPrice_.order, JoinType.LEFT);

            Join<Order, Manager> orderManager = orders.join(Order_.manager, JoinType.LEFT);
            Join<Order, Manager> itemManager = itemOrders.join(Order_.manager, JoinType.LEFT);

            root.fetch(Price_.documents, JoinType.LEFT);

            return criteria.and(
                criteria.or(
                    criteria.equal(orderManager.get(Manager_.email), email),
                    criteria.equal(itemManager.get(Manager_.email), email)
                ),
                criteria.or(

                    criteria.equal(itemOrders.get(Order_.id), orderId.map(criteria::literal).orElse(itemOrders.get(Order_.id))),
                    criteria.equal(orders.get(Order_.id), orderId.map(criteria::literal).orElse(orders.get(Order_.id)))
                )
            );
        };
    }

    static Specification<Price> byIdFetchRefs(Long id) {
        return (root, query, criteriaBuilder) -> {
            root.fetch(Price_.itemOrderPrices, JoinType.LEFT)
                .fetch(ItemOrderPrice_.notification, JoinType.LEFT);

            root.fetch(Price_.documents, JoinType.LEFT);

            return criteriaBuilder.equal(root.get(Price_.id), id);
        };
    }
}
