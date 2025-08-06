package com.stag.platform.entry.repository;

import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
import com.stag.platform.entry.entity.CodelistEntry;
import com.stag.platform.entry.entity.CodelistEntryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CodelistEntryRepository extends JpaRepository<CodelistEntry, CodelistEntryId> {

    /// Using COALESCE because not all values have their English translation
    @Query(
        """
        SELECT
            c.id,
            CASE
                WHEN :language = 'en' THEN COALESCE(c.meaningEn, c.meaningCz)
                ELSE c.meaningCz
            END AS meaning
        FROM
            CodelistEntry c
        WHERE
            c.id IN :ids
        """
    )
    List<CodelistEntryMeaningProjection> findCodelistEntriesByIds(Collection<CodelistEntryId> ids, String language);

}
