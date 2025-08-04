package com.stag.platform.codelist.service;

import com.stag.platform.codelist.repository.CountryRepository;
import com.stag.platform.codelist.repository.projection.CountryNameProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    @Transactional(readOnly = true)
    public Map<Integer, String> findNamesByIds(Collection<Integer> ids, String language) {
        return countryRepository.findNamesByIds(ids, language)
                                .stream()
                                .collect(Collectors.toMap(
                                    CountryNameProjection::id,
                                    CountryNameProjection::name
                                ));
    }

}

