package org.cynic.spring_stuff.repository;

import jakarta.persistence.criteria.JoinType;
import org.cynic.spring_stuff.domain.entity.Document;
import org.cynic.spring_stuff.domain.entity.Document_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    static Specification<Document> byIdFetchingRefs(Long id) {
        return (root, query, criteria) -> {
            root.fetch(Document_.orders, JoinType.LEFT);
            root.fetch(Document_.items, JoinType.LEFT);
            root.fetch(Document_.prices, JoinType.LEFT);

            return criteria.equal(root.get(Document_.id), id);
        };
    }
}
