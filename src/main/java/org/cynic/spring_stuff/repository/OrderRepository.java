package org.cynic.spring_stuff.repository;

import jakarta.persistence.criteria.Join;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Manager_;
import org.cynic.spring_stuff.domain.entity.Order;
import org.cynic.spring_stuff.domain.entity.Order_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    static Specification<Order> byManagerAndClosed(String email, Optional<Boolean> closed) {
        return (root, query, criteriaBuilder) -> {
            Join<Order, Manager> manager = root.join(Order_.manager);

            root.fetch(Order_.owner).fetch(Manager_.organization);

            return criteriaBuilder.and(
                criteriaBuilder.equal(manager.get(Manager_.email), email),
                criteriaBuilder.equal(root.get(Order_.closed), closed.map(criteriaBuilder::literal).orElseGet(() -> root.get(Order_.closed)))
            );
        };
    }

    static Specification<Order> byManagerAndIdAndClosed(Long id, String email, Boolean closed) {
        return (root, query, criteriaBuilder) -> {
            Join<Order, Manager> manager = root.join(Order_.manager);

            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Order_.id), id),
                criteriaBuilder.equal(manager.get(Manager_.email), email),
                criteriaBuilder.equal(root.get(Order_.closed), closed)
            );
        };
    }

    @EntityGraph(attributePaths = {"documents", "items.itemOrderPrices.price", "itemOrderPrices.price", "itemOrderPrices.notification", "manager.organization",
        "owner.organization"})
    @Override
    Optional<Order> findById(Long id);


    Set<Order> findAllByIdIn(List<Long> id);
}
