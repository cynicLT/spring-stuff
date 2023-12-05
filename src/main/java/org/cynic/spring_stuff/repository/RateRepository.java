package org.cynic.spring_stuff.repository;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.cynic.spring_stuff.domain.entity.Rate;
import org.cynic.spring_stuff.domain.entity.Rate_;
import org.cynic.spring_stuff.domain.model.type.RateSourceType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.Optional;

@Repository
public interface RateRepository extends CrudRepository<Rate, Long>, JpaSpecificationExecutor<Rate> {

    static Specification<Rate> latestRates(Optional<OffsetDateTime> date) {
        return (root, query, criteria) -> {
            Subquery<String> subQuery = query.subquery(String.class);
            Root<Rate> subRoot = subQuery.from(Rate.class);
            subQuery.groupBy(subRoot.get(Rate_.currency));

            date.ifPresent(it -> subQuery.where(
                criteria.lessThanOrEqualTo(
                    subRoot.get(Rate_.DATE_TIME),
                    criteria.literal(it)
                )
            ));

            subQuery.select(
                criteria.concat(
                    subRoot.get(Rate_.CURRENCY),
                    criteria.function(
                        "TO_CHAR",
                        String.class,
                        criteria.max(subRoot.get(Rate_.DATE_TIME)),
                        criteria.literal("YYYY-MM-DD HH24:MI:SS")
                    )
                )
            );

            return criteria.in(
                criteria.concat(
                    root.get(Rate_.CURRENCY),
                    criteria.function(
                        "TO_CHAR",
                        String.class,
                        root.get(Rate_.DATE_TIME),
                        criteria.literal("YYYY-MM-DD HH24:MI:SS")
                    )
                )
            ).value(subQuery);
        };
    }

    static Specification<Rate> history(Currency currency) {
        return (root, query, criteria) -> criteria.equal(root.get(Rate_.currency), currency);
    }


    Boolean existsByCurrencyAndDateTime(Currency code, OffsetDateTime dateTime);

    Optional<Rate> findByIdAndSource(Long id, RateSourceType source);

}
