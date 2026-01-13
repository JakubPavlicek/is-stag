package com.stag.platform.address.controller;

import com.stag.platform.address.repository.projection.CountryView;
import com.stag.platform.address.service.CountryService;
import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestSecurityConfig;
import com.stag.platform.shared.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(CountryController.class)
@Import({ TestCacheConfig.class, TestSecurityConfig.class, SecurityConfig.class })
@ActiveProfiles("test")
class CountryControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private CountryService countryService;

    @Test
    @DisplayName("should return 200 OK with list of countries when requested")
    void getCountries_ReturnsOkWithCountries() {
        String language = "cs";
        CountryView country = new CountryView(203, "Česká republika", "Česko", "CZ");
        when(countryService.getCountries(language)).thenReturn(Set.of(country));

        assertThat(mvc.get()
                      .uri("/api/v1/countries")
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.countries").asArray().hasSize(1);
                json.assertThat().extractingPath("$.countries[0].id").isEqualTo(203);
                json.assertThat().extractingPath("$.countries[0].name").isEqualTo("Česká republika");
                json.assertThat().extractingPath("$.countries[0].commonName").isEqualTo("Česko");
                json.assertThat().extractingPath("$.countries[0].abbreviation").isEqualTo("CZ");
            });

        verify(countryService).getCountries(language);
    }

    @Test
    @DisplayName("should return 200 OK with empty list when no countries found")
    void getCountries_NoCountries_ReturnsOkWithEmptyList() {
        String language = "en";
        when(countryService.getCountries(language)).thenReturn(Collections.emptySet());

        assertThat(mvc.get()
                      .uri("/api/v1/countries")
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> json.assertThat().extractingPath("$.countries").asArray().isEmpty());

        verify(countryService).getCountries(language);
    }

    @Test
    @DisplayName("should return 500 Internal Server Error when service throws exception")
    void getCountries_ServiceThrowsException_Returns500() {
        String language = "en";
        when(countryService.getCountries(language)).thenThrow(new RuntimeException("Database error"));

        assertThat(mvc.get()
                      .uri("/api/v1/countries")
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON))
            .hasStatus(500)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.title").isEqualTo("Internal Server Error");
                json.assertThat().extractingPath("$.detail").isEqualTo("Unexpected error");
            });

        verify(countryService).getCountries(language);
    }

    @Test
    @DisplayName("should use default language 'cs' when Accept-Language header is missing")
    void getCountries_NoLanguageHeader_UsesDefaultLanguage() {
        CountryView country = new CountryView(203, "Czech Republic", "Czechia", "CZ");
        when(countryService.getCountries("cs")).thenReturn(Set.of(country));

        assertThat(mvc.get()
                      .uri("/api/v1/countries")
                      .accept(MediaType.APPLICATION_JSON))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> json.assertThat().extractingPath("$.countries").asArray().isNotEmpty());
    }
}
