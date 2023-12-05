package org.cynic.spring_stuff.repository;

import jakarta.persistence.criteria.SetJoin;
import org.cynic.spring_stuff.domain.entity.Manager;
import org.cynic.spring_stuff.domain.entity.Manager_;
import org.cynic.spring_stuff.domain.entity.Organization;
import org.cynic.spring_stuff.domain.entity.Organization_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface OrganizationRepository extends Repository<Organization, Long>, JpaSpecificationExecutor<Organization> {
    @SuppressWarnings("unchecked")
    static Specification<Organization> byManagerEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            SetJoin<Organization, Manager> managers = (SetJoin<Organization, Manager>) root.fetch(Organization_.managers);

            return criteriaBuilder.equal(managers.get(Manager_.email), email);
        };
    }
}
