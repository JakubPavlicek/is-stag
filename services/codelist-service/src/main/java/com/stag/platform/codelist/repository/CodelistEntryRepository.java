package com.stag.platform.codelist.repository;

import com.stag.platform.codelist.entity.CodelistEntry;
import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.repository.projection.CodelistEntryValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CodelistEntryRepository extends JpaRepository<CodelistEntry, CodelistEntryId> {

    Optional<CodelistEntry> findByIdDomainAndIdLowValue(String domain, String lowValue);

    List<CodelistEntry> findAllByIdDomain(String domain);

//    @Query("SELECT c FROM CodelistEntry c WHERE (c.id.domain, c.id.lowValue) IN :ids")
    List<CodelistEntryValue> findAllByIdIn(Collection<CodelistEntryId> ids);

}
