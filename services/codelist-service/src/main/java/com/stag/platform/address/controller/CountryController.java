package com.stag.platform.address.controller;

import com.stag.platform.address.mapper.CountryApiMapper;
import com.stag.platform.address.repository.projection.CountryView;
import com.stag.platform.address.service.CountryService;
import com.stag.platform.api.CountriesApi;
import com.stag.platform.api.dto.CountryListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CountryController implements CountriesApi {

    private final CountryService countryService;

    @Override
    public ResponseEntity<CountryListResponse> getCountries(String language) {
        log.info("Countries requested in language: {}", language);

        Set<CountryView> countries = countryService.getCountries(language);
        CountryListResponse countryListResponse = CountryApiMapper.INSTANCE.toCountryListResponse(countries);

        return ResponseEntity.ok(countryListResponse);
    }

}
