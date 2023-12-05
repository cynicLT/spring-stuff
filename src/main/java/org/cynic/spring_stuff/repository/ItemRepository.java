package org.cynic.spring_stuff.repository;

import org.cynic.spring_stuff.domain.entity.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {

    @EntityGraph(attributePaths = {"orders.manager.organization", "documents"})
    @Override
    Optional<Item> findById(Long id);

    @EntityGraph(attributePaths = {"orders"})
    List<Item> findAllByOrdersIsNullOrOrdersManagerEmail(String email);


    Set<Item> findAllByIdIn(Set<Long> ids);
}
