package org.cynic.spring_stuff.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.SetJoin;
import org.cynic.spring_stuff.domain.entity.Item;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice_;
import org.cynic.spring_stuff.domain.entity.Item_;
import org.cynic.spring_stuff.domain.entity.Notification;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.entity.Order_;
import org.cynic.spring_stuff.domain.entity.Price;
import org.cynic.spring_stuff.domain.entity.Price_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface ItemOrderPriceRepository extends CrudRepository<ItemOrderPrice, Long>, JpaSpecificationExecutor<ItemOrderPrice> {

    @SuppressWarnings("unchecked")
    static Specification<ItemOrderPrice> byNotCoveredAndDueDateTimeIsBeforeAndNotificationIsMissing(OffsetDateTime offsetDateTime) {
        return (root, query, criteria) -> {
            Join<ItemOrderPrice, Price> price = root.join(ItemOrderPrice_.price);

            SetJoin<Item, Order> itemOrder = root.join(ItemOrderPrice_.item, JoinType.LEFT).join(Item_.orders, JoinType.LEFT);
            Join<ItemOrderPrice, Order> order = root.join(ItemOrderPrice_.order, JoinType.LEFT);
            Join<ItemOrderPrice, Notification> notification = (Join<ItemOrderPrice, Notification>) root.fetch(ItemOrderPrice_.notification, JoinType.LEFT);

            return criteria.and(
                criteria.isFalse(criteria.coalesce(
                    itemOrder.get(Order_.closed),
                    order.get(Order_.closed)
                )),
                criteria.isFalse(price.get(Price_.covered)),
                criteria.isNull(notification),
                criteria.lessThan(price.get(Price_.dueDateTime), offsetDateTime)
            );
        };
    }
}
