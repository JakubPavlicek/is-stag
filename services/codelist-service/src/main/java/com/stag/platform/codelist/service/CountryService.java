package com.stag.platform.codelist.service;

import com.stag.platform.codelist.entity.Country;
import com.stag.platform.codelist.repository.projection.CountryNameProjection;
import com.stag.platform.codelist.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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

    @Transactional(readOnly = true)
    public Map<Integer, String> findNamesByIds(Collection<Integer> ids) {
        return countryRepository.findNamesByIds(ids)
                                .stream()
                                .collect(Collectors.toMap(
                                    CountryNameProjection::id,
                                    CountryNameProjection::name
                                ));
    }

}

