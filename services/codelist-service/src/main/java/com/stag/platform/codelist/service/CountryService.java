package com.stag.platform.codelist.service;

import com.stag.platform.codelist.entity.Country;
import com.stag.platform.codelist.projection.CountryName;
import com.stag.platform.codelist.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Map<Integer, String> findNamesByIds(List<Integer> ids) {
        return countryRepository.findNamesByIds(ids)
                                .stream()
                                .collect(Collectors.toMap(
                                    CountryName::id,
                                    CountryName::name
                                ));
    }

}
