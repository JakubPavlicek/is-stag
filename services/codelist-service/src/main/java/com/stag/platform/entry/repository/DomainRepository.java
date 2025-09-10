package com.stag.platform.entry.repository;

import com.stag.platform.entry.entity.Domain;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface DomainRepository extends Repository<Domain, String> {

    @Query("SELECT d.domainId FROM Domain d ORDER BY d.domainId")
    List<String> findAllDomainNames();

}