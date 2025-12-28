package com.stag.platform.entry.repository;

import com.stag.platform.entry.entity.CodelistEntry;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
import com.stag.platform.entry.repository.projection.DomainValueView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/// **Codelist Entry Repository**
///
/// Data access layer for codelist entries with specification support.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public interface CodelistEntryRepository extends JpaRepository<CodelistEntry, CodelistEntryId>, JpaSpecificationExecutor<CodelistEntry> {

    /// Retrieves domain values for a specific domain with language-specific meanings.
    ///
    /// Returns only valid entries (isValid != 'N') sorted by meaning and order.
    /// Uses COALESCE to fall back to Czech when English translation is unavailable.
    ///
    /// @param domain Domain name
    /// @param language Language code ('cs' or 'en')
    /// @return List of domain value views
    @Query(
        """
        SELECT new com.stag.platform.entry.repository.projection.DomainValueView(
            ce.id.lowValue,
            CASE
                WHEN :language = 'en' THEN COALESCE(ce.meaningEn, ce.meaningCz)
                ELSE ce.meaningCz
            END,
            ce.abbreviation
        )
        FROM
            CodelistEntry ce
        WHERE
            ce.id.domain = :domain
            AND ce.isValid != 'N'
        ORDER BY
            CASE
                WHEN :language = 'en' THEN COALESCE(ce.meaningEn, ce.meaningCz)
                ELSE ce.meaningCz
            END, ce.order
        """
    )
    List<DomainValueView> findDomainValuesByDomain(String domain, String language);

    /// Retrieves codelist entry meanings by IDs with language-specific translations.
    ///
    /// Uses COALESCE to fall back to Czech when English translation is unavailable.
    ///
    /// @param ids Collection of codelist entry IDs
    /// @param language Language code ('cs' or 'en')
    /// @return List of codelist entry meaning projections
    @Query(
        """
        SELECT
            ce.id,
            CASE
                WHEN :language = 'en' THEN COALESCE(ce.meaningEn, ce.meaningCz)
                ELSE ce.meaningCz
            END AS meaning
        FROM
            CodelistEntry ce
        WHERE
            ce.id IN :ids
        """
    )
    List<CodelistEntryMeaningProjection> findCodelistEntriesByIds(Collection<CodelistEntryId> ids, String language);

}
