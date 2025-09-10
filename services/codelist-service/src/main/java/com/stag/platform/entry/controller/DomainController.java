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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class DomainController implements DomainsApi {

    private final DomainService domainService;

    @Override
    public ResponseEntity<DomainValueListResponse> getDomainValues(String domain, String acceptLanguage) {
        List<DomainValueView> domainValues = domainService.getDomainValues(domain, acceptLanguage);
        DomainValueListResponse domainValuesResponse = DomainApiMapper.INSTANCE.toDomainValueListResponse(domainValues);

        return ResponseEntity.ok(domainValuesResponse);
    }

    @Override
    public ResponseEntity<DomainListResponse> getDomains() {
        List<String> domains = domainService.getDomains();
        DomainListResponse domainsResponse = DomainApiMapper.INSTANCE.toDomainListResponse(domains);

        return ResponseEntity.ok(domainsResponse);
    }

}
