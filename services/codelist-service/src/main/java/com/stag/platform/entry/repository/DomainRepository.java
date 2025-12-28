package com.stag.platform.entry.repository;

import com.stag.platform.entry.entity.Domain;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/// **Domain Repository**
///
/// Data access layer for domain entities with read-only operations.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public interface DomainRepository extends Repository<Domain, String> {

    /// Retrieves all domain names sorted alphabetically.
    ///
    /// @return List of domain names
    @Query("SELECT d.domainId FROM Domain d ORDER BY d.domainId")
    List<String> findAllDomainNames();

}