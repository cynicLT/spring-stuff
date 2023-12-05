package org.cynic.spring_stuff.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Manager_;
import org.cynic.spring_stuff.domain.entity.Organization;
import org.cynic.spring_stuff.domain.entity.Organization_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ManagerRepository extends CrudRepository<Manager, Long>, JpaSpecificationExecutor<Manager> {


    @SuppressWarnings("unchecked")
    static Specification<Manager> byEmailAndIsOwner(String email, Boolean owner) {
        return (root, query, criteria) -> {
            Subquery<Long> subQuery = query.subquery(Long.class);
            Root<Manager> subRoot = subQuery.from(Manager.class);

            subQuery.select(subRoot.get(Manager_.ORGANIZATION).get(Organization_.ID));
            subQuery.where(criteria.equal(subRoot.get(Manager_.EMAIL), email));

            Join<Manager, Organization> organization = (Join<Manager, Organization>) root.fetch(Manager_.organization);

            return owner ?
                criteria.equal(organization.get(Organization_.ID), subQuery) :
                criteria.notEqual(organization.get(Organization_.ID), subQuery);
        };
    }

    static Specification<Manager> byIdAndEmailNotSameOrganizationOrUser(Long id, String email) {
        return (root, query, criteria) -> {
            Subquery<Long> subQuery = query.subquery(Long.class);
            Root<Organization> subRoot = subQuery.from(Organization.class);

            subQuery.select(subRoot.get(Organization_.ID));
            subQuery.where(criteria.equal(subRoot.join(Organization_.managers).get(Manager_.email), email));

            Join<Manager, Organization> organization = root.join(Manager_.organization);

            return criteria.and(
                criteria.equal(root.get(Manager_.id), id),
                criteria.or(
                    criteria.notEqual(organization.get(Organization_.id), subQuery),
                    criteria.notEqual(root.get(Manager_.email), email)
                )
            );
        };
    }

    Optional<Manager> findByEmail(String email);
}
