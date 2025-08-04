package com.stag.platform.codelist.repository;

import com.stag.platform.codelist.entity.CodelistEntry;
import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.repository.projection.CodelistEntryValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CodelistEntryRepository extends JpaRepository<CodelistEntry, CodelistEntryId> {

    @Query(
        """
        SELECT
            c.id,
            CASE
                WHEN :language = 'en' THEN c.meaningEn
                ELSE c.meaningCz
            END AS meaning
        FROM
            CodelistEntry c
        WHERE
            c.id IN :ids
        """
    )
    List<CodelistEntryValue> findAllByIdIn(Collection<CodelistEntryId> ids, String language);

}
