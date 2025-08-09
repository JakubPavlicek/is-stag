package com.stag.platform.address.service;

import com.stag.platform.address.exception.CountriesNotFoundException;
import com.stag.platform.address.repository.CountryRepository;
import com.stag.platform.address.repository.projection.CountryNameProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    @Transactional(readOnly = true)
    public Map<Integer, String> findNamesByIds(Collection<Integer> countryIds, String language) {
        List<CountryNameProjection> foundCountries = countryRepository.findNamesByIds(countryIds, language);

        ensureAllCountriesWereFound(countryIds, foundCountries);

        return foundCountries.stream()
                             .collect(Collectors.toMap(
                                 CountryNameProjection::id,
                                 CountryNameProjection::name
                             ));
    }

    private void ensureAllCountriesWereFound(Collection<Integer> requestedIds, List<CountryNameProjection> foundCountries) {
        // If counts match, all countries were found (assumes no duplicates in requestedIds)
        if (requestedIds.size() == foundCountries.size()) {
            return;
        }

        List<Integer> missingIds = getMissingIds(requestedIds, foundCountries);

        // If, after filtering, there are IDs still missing, throw an exception
        if (!missingIds.isEmpty()) {
            throw new CountriesNotFoundException(missingIds);
        }
    }

    private List<Integer> getMissingIds(Collection<Integer> requestedIds, List<CountryNameProjection> foundCountries) {
        // Determine which IDs are missing
        Set<Integer> foundIds = foundCountries.stream()
                                              .map(CountryNameProjection::id)
                                              .collect(Collectors.toSet());

        return requestedIds.stream()
                           .filter(id -> !foundIds.contains(id))
                           .toList();
    }

}

