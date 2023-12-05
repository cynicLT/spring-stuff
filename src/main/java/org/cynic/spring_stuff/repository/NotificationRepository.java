package org.cynic.spring_stuff.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.Optional;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.ItemOrderPrice_;
import org.cynic.spring_stuff.domain.entity.Item_;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Manager_;
import org.cynic.spring_stuff.domain.entity.Notification;
import org.cynic.spring_stuff.domain.entity.Notification_;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.entity.Order_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    @SuppressWarnings("unchecked")
    static Specification<Notification> byManager(String email, Optional<Boolean> visit) {
        return (root, query, criteriaBuilder) -> {
            Join<Notification, ItemOrderPrice> itemOrderPrice = (Join<Notification, ItemOrderPrice>) root.fetch(Notification_.itemOrderPrice, JoinType.INNER);

            Join<Order, Manager> orderManager = itemOrderPrice.join(ItemOrderPrice_.order, JoinType.LEFT)
                .join(Order_.manager, JoinType.LEFT);
            Join<Order, Manager> itemManager = itemOrderPrice.join(ItemOrderPrice_.item, JoinType.LEFT)
                .join(Item_.orders, JoinType.LEFT)
                .join(Order_.manager, JoinType.LEFT);

            itemOrderPrice.fetch(ItemOrderPrice_.price);
            root.fetch(Notification_.manager, JoinType.LEFT).fetch(Manager_.organization, JoinType.LEFT);

            return criteriaBuilder.and(
                criteriaBuilder.or(
                    criteriaBuilder.equal(orderManager.get(Manager_.email), email),
                    criteriaBuilder.equal(itemManager.get(Manager_.email), email)
                ),
                criteriaBuilder.equal(
                    root.get(Notification_.visit),
                    visit.map(criteriaBuilder::literal)
                        .orElse(root.get(Notification_.visit))
                )
            );
        };
    }

    static Specification<Notification> byIdAndNotVisitedAndNoManager(Long id) {
        return (root, query, criteriaBuilder) -> {
            Join<Notification, Manager> manager = root.join(Notification_.manager, JoinType.LEFT);

            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Notification_.id), id),
                criteriaBuilder.isNull(manager),
                criteriaBuilder.isFalse(root.get(Notification_.visit))
            );
        };
    }

    Optional<Notification> findByIdAndManagerEmail(Long id, String email);
}
