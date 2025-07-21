package com.stag.platform.codelist.repository;

import com.stag.platform.codelist.entity.CodelistValue;
import com.stag.platform.codelist.entity.CodelistValueId;
import com.stag.platform.codelist.projection.CodelistValueMeaning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CodelistValueRepository extends JpaRepository<CodelistValue, CodelistValueId> {

    Optional<CodelistValue> findByIdDomainAndIdLowValue(String domain, String lowValue);

    List<CodelistValue> findAllByIdDomain(String domain);

    List<CodelistValueMeaning> findAllByIdIn(Collection<CodelistValueId> ids);

}
