package com.stag.platform.address.service;

import com.stag.platform.address.exception.CountriesNotFoundByIdsException;
import com.stag.platform.address.exception.CountriesNotFoundByNamesException;
import com.stag.platform.address.repository.CountryRepository;
import com.stag.platform.address.repository.projection.CountryNameProjection;
import com.stag.platform.address.repository.projection.CountryView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "countries", key = "#language")
    public Set<CountryView> getCountries(String language) {
        return countryRepository.findAllValidCountries(language);
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> findCountryIds(Collection<String> countryNames, String language) {
        List<CountryNameProjection> foundCountryIds = countryRepository.findIdsByNames(countryNames, language);

        ensureAllCountriesWereFoundByNames(countryNames, foundCountryIds);

        return foundCountryIds.stream()
                              .collect(Collectors.toMap(
                                  CountryNameProjection::name,
                                  CountryNameProjection::id
                              ));
    }

    @Transactional(readOnly = true)
    public Map<Integer, String> findNamesByIds(Collection<Integer> countryIds, String language) {
        List<CountryNameProjection> foundCountries = countryRepository.findNamesByIds(countryIds, language);

        ensureAllCountriesWereFoundByIds(countryIds, foundCountries);

        return foundCountries.stream()
                             .collect(Collectors.toMap(
                                 CountryNameProjection::id,
                                 CountryNameProjection::name
                             ));
    }

    private void ensureAllCountriesWereFoundByNames(Collection<String> requestedNames, List<CountryNameProjection> foundCountries) {
        // If counts match, all countries were found (assumes no duplicates in requestedNames)
        if (requestedNames.size() == foundCountries.size()) {
            return;
        }

        List<String> missingNames = getMissingNames(requestedNames, foundCountries);

        // If, after filtering, there are names still missing, throw an exception
        if (!missingNames.isEmpty()) {
            throw new CountriesNotFoundByNamesException(missingNames);
        }
    }

    private List<String> getMissingNames(Collection<String> requestedNames, List<CountryNameProjection> foundCountries) {
        // Determine which IDs are missing
        Set<String> foundNames = foundCountries.stream()
                                               .map(CountryNameProjection::name)
                                               .collect(Collectors.toSet());

        return requestedNames.stream()
                             .filter(name -> !foundNames.contains(name))
                             .toList();
    }

    private void ensureAllCountriesWereFoundByIds(Collection<Integer> requestedIds, List<CountryNameProjection> foundCountries) {
        // If counts match, all countries were found (assumes no duplicates in requestedIds)
        if (requestedIds.size() == foundCountries.size()) {
            return;
        }

        List<Integer> missingIds = getMissingIds(requestedIds, foundCountries);

        // If, after filtering, there are IDs still missing, throw an exception
        if (!missingIds.isEmpty()) {
            throw new CountriesNotFoundByIdsException(missingIds);
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

