package com.stag.platform.entry.repository.specification;

import com.stag.platform.entry.entity.CodelistEntry;
import com.stag.platform.entry.entity.CodelistEntryId_;
import com.stag.platform.entry.entity.CodelistEntry_;
import com.stag.platform.shared.grpc.model.CodelistDomain;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CodelistEntrySpecification {

    private CodelistEntrySpecification() {
    }

    public static Specification<CodelistEntry> byPersonProfileCriteria(String maritalStatus, String titlePrefix, String titleSuffix) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addPredicateIfPresent(predicates, cb, root, CodelistDomain.STAV.name(), maritalStatus);
            addPredicateIfPresent(predicates, cb, root, CodelistDomain.TITUL_PRED.name(), titlePrefix);
            addPredicateIfPresent(predicates, cb, root, CodelistDomain.TITUL_ZA.name(), titleSuffix);

            if (predicates.isEmpty()) {
                return cb.disjunction();
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addPredicateIfPresent(List<Predicate> predicates, CriteriaBuilder cb, Root<CodelistEntry> root, String domain, String meaning) {
        if (meaning == null || meaning.isBlank()) {
            return;
        }

        Predicate domainMatch = cb.equal(
            root.get(CodelistEntry_.ID).get(CodelistEntryId_.DOMAIN), domain
        );
        Predicate meaningMatch = cb.or(
            cb.equal(root.get(CodelistEntry_.ABBREVIATION), meaning),
            cb.equal(root.get(CodelistEntry_.MEANING_CZ), meaning),
            cb.equal(root.get(CodelistEntry_.MEANING_EN), meaning)
        );

        predicates.add(cb.and(domainMatch, meaningMatch));
    }

}
