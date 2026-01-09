package com.stag.platform.address.service;

import com.stag.platform.address.exception.CountriesNotFoundException;
import com.stag.platform.address.exception.CountryNotFoundException;
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

/// **Country Service**
///
/// Manages country data retrieval with caching support.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService {

    /// Country Repository
    private final CountryRepository countryRepository;

    /// Retrieves all valid countries in the specified language.
    ///
    /// @param language Language code ('cs' or 'en')
    /// @return Set of country views
    @Transactional(readOnly = true)
    @Cacheable(value = "countries", key = "#language")
    public Set<CountryView> getCountries(String language) {
        log.info("Fetching all countries for language: {}", language);
        return countryRepository.findAllValidCountries(language);
    }

    /// Finds country ID by country name.
    ///
    /// @param countryName Country name to search for
    /// @return Country ID or null if the name is blank
    /// @throws CountryNotFoundException if the country is not found
    @Transactional(readOnly = true)
    @Cacheable(value = "country-id", key = "#countryName", unless = "#result == null")
    public Integer findCountryIdByName(String countryName) {
        if (countryName == null || countryName.isBlank()) {
            log.debug("Country name is null or blank, returning null");
            return null;
        }

        log.info("Finding country ID by name: {}", countryName);
        return countryRepository.findCountryIdByName(countryName)
                                .orElseThrow(() -> new CountryNotFoundException(countryName));
    }

    /// Retrieves country names by IDs in the specified language.
    ///
    /// @param countryIds Collection of country IDs
    /// @param language Language code
    /// @return Map of IDs to country names
    /// @throws CountriesNotFoundException if any IDs are missing
    @Transactional(readOnly = true)
    public Map<Integer, String> findNamesByIds(Collection<Integer> countryIds, String language) {
        log.info("Finding country names for {} IDs in language: {}", countryIds.size(), language);

        List<CountryNameProjection> foundCountries = countryRepository.findNamesByIds(countryIds, language);

        ensureAllCountriesWereFound(countryIds, foundCountries);

        log.debug("Successfully retrieved {} country names", foundCountries.size());
        return foundCountries.stream()
                             .collect(Collectors.toMap(
                                 CountryNameProjection::id,
                                 CountryNameProjection::name
                             ));
    }

    /// Validates that all requested countries were found.
    ///
    /// @param requestedIds Collection of requested country IDs
    /// @param foundCountries List of found country projections
    /// @throws CountriesNotFoundException if any IDs are missing
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

    /// Identifies country IDs that were not found.
    ///
    /// @param requestedIds Collection of requested country IDs
    /// @param foundCountries List of found country projections
    /// @return List of missing country IDs
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
