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

/// **Codelist Entry Specification**
///
/// JPA Criteria API specifications for complex codelist entry queries.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public class CodelistEntrySpecification {

    /// Private constructor to prevent instantiation.
    private CodelistEntrySpecification() {
    }

    /// Creates a specification for finding person profile codelist entries.
    ///
    /// Searches for marital status, title prefix, and title suffix by matching
    /// meanings against abbreviation or Czech/English meanings.
    ///
    /// @param maritalStatus Marital status to search for
    /// @param titlePrefix Title prefix to search for
    /// @param titleSuffix Title suffix to search for
    /// @return Specification matching any of the provided criteria
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

    /// Adds a predicate for domain and meaning matching if the meaning is present.
    ///
    /// @param predicates List of predicates to add to
    /// @param cb CriteriaBuilder for building predicates
    /// @param root Root of the CodelistEntry entity
    /// @param domain Domain to match
    /// @param meaning Meaning to match
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
