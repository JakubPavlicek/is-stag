package com.stag.platform.entry.controller;

import com.stag.platform.api.DomainsApi;
import com.stag.platform.api.dto.DomainListResponse;
import com.stag.platform.api.dto.DomainValueListResponse;
import com.stag.platform.entry.mapper.DomainApiMapper;
import com.stag.platform.entry.repository.projection.DomainValueView;
import com.stag.platform.entry.service.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/// **Domain Controller**
///
/// REST API endpoint for domain and domain value retrieval.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class DomainController implements DomainsApi {

    /// Domain Service
    private final DomainService domainService;

    /// Retrieves all values for a specific domain.
    ///
    /// @param domain Domain name
    /// @param acceptLanguage Language code from Accept-Language header
    /// @return Response containing domain values
    @Override
    public ResponseEntity<DomainValueListResponse> getDomainValues(String domain, String acceptLanguage) {
        List<DomainValueView> domainValues = domainService.getDomainValues(domain, acceptLanguage);
        DomainValueListResponse domainValuesResponse = DomainApiMapper.INSTANCE.toDomainValueListResponse(domainValues);

        return ResponseEntity.ok(domainValuesResponse);
    }

    /// Retrieves all available domain names.
    ///
    /// @return Response containing list of domain names
    @Override
    public ResponseEntity<DomainListResponse> getDomains() {
        List<String> domains = domainService.getDomains();
        DomainListResponse domainsResponse = DomainApiMapper.INSTANCE.toDomainListResponse(domains);

        return ResponseEntity.ok(domainsResponse);
    }

}
