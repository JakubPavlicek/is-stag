package com.stag.platform.address.controller;

import com.stag.platform.address.service.CountryService;
import com.stag.platform.api.CountriesApi;
import com.stag.platform.api.dto.CountryListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CountryController implements CountriesApi {

    private final CountryService countryService;

    // TODO: use Gemini to propose changes/best practices in my OpenAPI schema
    // TODO: implement this

    @Override
    public ResponseEntity<CountryListResponse> getCountries(String acceptLanguage) {
        return CountriesApi.super.getCountries(acceptLanguage);
    }

}
