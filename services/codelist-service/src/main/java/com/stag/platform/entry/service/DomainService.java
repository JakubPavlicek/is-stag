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

@Slf4j
@RequiredArgsConstructor
@Service
public class DomainService {

    private final CodelistEntryRepository codelistEntryRepository;
    private final DomainRepository domainRepository;

    @Cacheable(value = "domain-values", key = "{#domain, #language}")
    @Transactional(readOnly = true)
    public List<DomainValueView> getDomainValues(String domain, String language) {
        return codelistEntryRepository.findDomainValuesByDomain(domain, language);
    }

    @Cacheable(value = "domains")
    @Transactional(readOnly = true)
    public List<String> getDomains() {
        return domainRepository.findAllDomainNames();
    }

}
