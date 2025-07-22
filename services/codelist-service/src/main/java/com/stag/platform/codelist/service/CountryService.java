package com.stag.platform.codelist.service;

import com.stag.platform.codelist.entity.Country;
import com.stag.platform.codelist.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Optional<Country> findById(Integer id) {
        return countryRepository.findById(id);
    }

    public Optional<String> findNameById(Integer id) {
        return countryRepository.findNameById(id);
    }

}
