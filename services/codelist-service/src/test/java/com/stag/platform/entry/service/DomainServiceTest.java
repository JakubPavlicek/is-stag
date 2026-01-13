package com.stag.platform.entry.service;

import com.stag.platform.entry.repository.CodelistEntryRepository;
import com.stag.platform.entry.repository.DomainRepository;
import com.stag.platform.entry.repository.projection.DomainValueView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DomainServiceTest {

    @Mock
    private CodelistEntryRepository codelistEntryRepository;

    @Mock
    private DomainRepository domainRepository;

    @InjectMocks
    private DomainService domainService;

    @Test
    @DisplayName("getDomainValues should return values from repository")
    void getDomainValues_ReturnsValues() {
        String domain = "TEST_DOMAIN";
        String language = "en";
        List<DomainValueView> expectedValues = List.of(
            new DomainValueView("K1", "V1", "A1")
        );

        when(codelistEntryRepository.findDomainValuesByDomain(domain, language)).thenReturn(expectedValues);

        List<DomainValueView> result = domainService.getDomainValues(domain, language);

        assertThat(result).isEqualTo(expectedValues);
        verify(codelistEntryRepository).findDomainValuesByDomain(domain, language);
    }

    @Test
    @DisplayName("getDomains should return all domain names from repository")
    void getDomains_ReturnsAllDomainNames() {
        List<String> expectedDomains = List.of("DOMAIN1", "DOMAIN2");

        when(domainRepository.findAllDomainNames()).thenReturn(expectedDomains);

        List<String> result = domainService.getDomains();

        assertThat(result).isEqualTo(expectedDomains);
        verify(domainRepository).findAllDomainNames();
    }
}
