package io.github.jokoframework.myproject.audit.repositories;

import io.github.jokoframework.myproject.audit.entities.AuditEventEntity;
import io.github.jokoframework.myproject.audit.enums.AuditAction;
import io.github.jokoframework.myproject.audit.enums.ResourceType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JPA Specifications for building dynamic queries on AuditEventEntity.
 *
 * @author ana bernal
 */
public class AuditEventSpecification {

    private AuditEventSpecification() {
        // Utility class
    }

    public static Specification<AuditEventEntity> withFilters(
            AuditAction action,
            ResourceType resourceType,
            String resourceId,
            Date from,
            Date to) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (action != null) {
                predicates.add(criteriaBuilder.equal(root.get("action"), action));
            }

            if (resourceType != null) {
                predicates.add(criteriaBuilder.equal(root.get("resourceType"), resourceType));
            }

            if (resourceId != null && !resourceId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("resourceId"), resourceId));
            }

            if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from));
            }

            if (to != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}