package com.atmate.portal.gateway.atmategateway.database.specification;

import com.atmate.portal.gateway.atmategateway.database.dto.ClientFilterDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.Client;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ClientSpecification {

    public static Specification<Client> withFilters(ClientFilterDTO filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.getName() != null) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
            }
            if (filter.getNif() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("nif"), filter.getNif()));
            }
            if (filter.getClientType() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("clientType"), filter.getClientType()));
            }
            if (filter.getGender() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("gender"), filter.getGender()));
            }
            if (filter.getNationality() != null) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("nationality")), "%" + filter.getNationality().toLowerCase() + "%"));
            }
            if (filter.getColaborator() != null) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("associatedColaborator")), "%" + filter.getColaborator().toLowerCase() + "%"));
            }
            if (filter.getBirthDateStart() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("birthDate"), filter.getBirthDateStart()));
            }
            if (filter.getBirthDateEnd() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("birthDate"), filter.getBirthDateEnd()));
            }
            if (filter.getCreatedAtStart() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAtStart()));
            }
            if (filter.getCreatedAtEnd() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedAtEnd()));
            }

            return predicate;
        };
    }
}
