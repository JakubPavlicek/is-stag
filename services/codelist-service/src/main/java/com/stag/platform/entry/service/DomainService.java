package com.stag.platform.entry.service;

import com.stag.platform.entry.repository.CodelistEntryRepository;
import com.stag.platform.entry.repository.DomainRepository;
import com.stag.platform.entry.repository.projection.DomainValueView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/// **Domain Service**
///
/// Manages codelist domain and domain value retrieval with caching.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class DomainService {

    private final CodelistEntryRepository codelistEntryRepository;
    private final DomainRepository domainRepository;

    /// Retrieves all values for a specific domain in the given language.
    ///
    /// @param domain Domain name
    /// @param language Language code ('cs' or 'en')
    /// @return List of domain value views
    @Cacheable(value = "domain-values", key = "{#domain, #language}")
    @Transactional(readOnly = true)
    public List<DomainValueView> getDomainValues(String domain, String language) {
        return codelistEntryRepository.findDomainValuesByDomain(domain, language);
    }

    /// Retrieves all domain names.
    ///
    /// @return List of domain names
    @Cacheable(value = "domains")
    @Transactional(readOnly = true)
    public List<String> getDomains() {
        return domainRepository.findAllDomainNames();
    }

}
