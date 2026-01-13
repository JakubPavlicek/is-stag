package com.stag.platform.entry.controller;

import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestSecurityConfig;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.exception.CodelistEntriesNotFoundException;
import com.stag.platform.entry.exception.CodelistMeaningsNotFoundException;
import com.stag.platform.entry.repository.projection.DomainValueView;
import com.stag.platform.entry.service.DomainService;
import com.stag.platform.shared.config.SecurityConfig;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(DomainController.class)
@Import({ TestCacheConfig.class, TestSecurityConfig.class, SecurityConfig.class })
@ActiveProfiles("test")
class DomainControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private DomainService domainService;

    @Test
    @DisplayName("should return 200 OK with list of domains when requested")
    void getDomains_ReturnsOkWithDomains() {
        List<String> domains = List.of("TITUL_PRED", "FAKULTA");
        when(domainService.getDomains()).thenReturn(domains);

        assertThat(mvc.get()
                      .uri("/api/v1/domains")
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.domains").asArray().containsExactly("TITUL_PRED", "FAKULTA");
            });

        verify(domainService).getDomains();
    }

    @Test
    @DisplayName("should return 200 OK with domain values when domain exists")
    void getDomainValues_ValidDomain_ReturnsOkWithValues() {
        String domain = "FAKULTA";
        String language = "cs";
        DomainValueView view = new DomainValueView("FAV", "Fakulta aplikovaných věd", "FAV");
        
        when(domainService.getDomainValues(domain, language)).thenReturn(List.of(view));

        assertThat(mvc.get()
                      .uri("/api/v1/domains/{domain}", domain)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.values[0].key").isEqualTo("FAV");
                json.assertThat().extractingPath("$.values[0].name").isEqualTo("Fakulta aplikovaných věd");
                json.assertThat().extractingPath("$.values[0].abbreviation").isEqualTo("FAV");
            });

        verify(domainService).getDomainValues(domain, language);
    }

    @Test
    @DisplayName("should return 500 Internal Server Error when CodelistEntriesNotFoundException is thrown")
    void getDomainValues_CodelistEntriesNotFound_Returns500() {
        CodelistEntryId missingId = CodelistEntryId.builder().domain("TEST").lowValue("VAL").build();
        when(domainService.getDomainValues(anyString(), anyString()))
                .thenThrow(new CodelistEntriesNotFoundException(List.of(missingId)));

        assertThat(mvc.get()
                      .uri("/api/v1/domains/TEST")
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(500)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.title").isEqualTo("Internal Server Error");
                json.assertThat().extractingPath("$.detail").isEqualTo("Unexpected error");
            });
    }

    @Test
    @DisplayName("should return 500 Internal Server Error when CodelistMeaningsNotFoundException is thrown")
    void getDomainValues_CodelistMeaningsNotFound_Returns500() {
        CodelistMeaningsNotFoundException.MissingMeaning missing = new CodelistMeaningsNotFoundException.MissingMeaning("TEST", "MEANING");
        when(domainService.getDomainValues(anyString(), anyString()))
                .thenThrow(new CodelistMeaningsNotFoundException(List.of(missing)));

        assertThat(mvc.get()
                      .uri("/api/v1/domains/TEST")
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(500)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.title").isEqualTo("Internal Server Error");
            });
    }

    @Test
    @DisplayName("should return 403 Forbidden when AccessDeniedException is thrown")
    void getDomains_AccessDenied_Returns403() {
        when(domainService.getDomains()).thenThrow(new AccessDeniedException("Access denied"));

        assertThat(mvc.get()
                      .uri("/api/v1/domains")
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(403)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.title").isEqualTo("Access Denied");
                json.assertThat().extractingPath("$.detail").isEqualTo("Access denied");
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when ConstraintViolationException is thrown")
    void getDomains_ConstraintViolation_Returns400() {
        when(domainService.getDomains()).thenThrow(new ConstraintViolationException(Collections.emptySet()));

        assertThat(mvc.get()
                      .uri("/api/v1/domains")
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    @Test
    @DisplayName("should return 500 Internal Server Error for unexpected exceptions")
    void getDomains_UnexpectedException_Returns500() {
        when(domainService.getDomains()).thenThrow(new RuntimeException("Something went wrong"));

        assertThat(mvc.get()
                      .uri("/api/v1/domains")
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(500)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.title").isEqualTo("Internal Server Error");
                json.assertThat().extractingPath("$.detail").isEqualTo("Unexpected error");
            });
    }
}