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

public interface CodelistEntryRepository extends JpaRepository<CodelistEntry, CodelistEntryId>, JpaSpecificationExecutor<CodelistEntry> {

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

    /// Using COALESCE because not all values have their English translation
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
