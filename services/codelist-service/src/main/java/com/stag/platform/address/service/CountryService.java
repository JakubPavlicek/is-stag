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

        // Determine which IDs are missing
        List<Integer> missingIds = getMissingIds(requestedIds, foundCountries);

        // If, after filtering, no IDs are missing (e.g., due to duplicates in the original request), then we can return successfully
        if (missingIds.isEmpty()) {
            return;
        }

        String formattedMissingIds = formatMissingIdsForError(missingIds);
        String errorMessage = "Unable to find countries for IDs: [" + formattedMissingIds + "]";

        log.warn(errorMessage);
        throw new CountriesNotFoundException(errorMessage);
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

    private String formatMissingIdsForError(List<Integer> missingIds) {
        return missingIds.stream()
                         .map(String::valueOf)
                         .collect(Collectors.joining(", "));
    }

}

