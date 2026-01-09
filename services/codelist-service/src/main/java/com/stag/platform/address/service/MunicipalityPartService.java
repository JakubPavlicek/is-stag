package com.stag.platform.address.service;

import com.stag.platform.address.exception.MunicipalityPartsNotFoundException;
import com.stag.platform.address.repository.MunicipalityPartRepository;
import com.stag.platform.address.repository.projection.AddressPlaceNameProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/// **Municipality Part Service**
///
/// Manages municipality part data retrieval for address information.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@Service
@RequiredArgsConstructor
public class MunicipalityPartService {

    /// Municipality Part Repository
    private final MunicipalityPartRepository municipalityPartRepository;

    /// Retrieves address place names by municipality part IDs.
    ///
    /// @param ids Collection of municipality part IDs
    /// @return Map of IDs to address place names
    /// @throws MunicipalityPartsNotFoundException if any IDs are missing
    @Transactional(readOnly = true)
    public Map<Long, AddressPlaceNameProjection> findAddressNamesByIds(Collection<Long> ids) {
        log.info("Finding address place names for {} municipality part IDs", ids.size());

        List<AddressPlaceNameProjection> foundMunicipalityParts = municipalityPartRepository.findAddressNamesByIds(ids);

        ensureAllMunicipalityPartsWereFound(ids, foundMunicipalityParts);

        log.debug("Successfully retrieved {} address place names", foundMunicipalityParts.size());
        return foundMunicipalityParts.stream()
                                     .collect(Collectors.toMap(
                                         AddressPlaceNameProjection::municipalityPartId,
                                         addressPlaceNameProjection -> addressPlaceNameProjection
                                     ));
    }

    /// Validates that all requested municipality parts were found.
    ///
    /// @param requestedIds Collection of requested municipality part IDs
    /// @param foundMunicipalityParts List of found municipality part projections
    /// @throws MunicipalityPartsNotFoundException if any IDs are missing
    private void ensureAllMunicipalityPartsWereFound(Collection<Long> requestedIds, List<AddressPlaceNameProjection> foundMunicipalityParts) {
        // If counts match, all municipality parts were found (assumes no duplicates in requestedIds)
        if (requestedIds.size() == foundMunicipalityParts.size()) {
            return;
        }

        List<Long> missingIds = getMissingIds(requestedIds, foundMunicipalityParts);

        // If, after filtering, there are IDs still missing, throw an exception
        if (!missingIds.isEmpty()) {
            throw new MunicipalityPartsNotFoundException(missingIds);
        }
    }

    /// Identifies municipality part IDs that were not found.
    ///
    /// @param requestedIds Collection of requested municipality part IDs
    /// @param foundMunicipalityParts List of found municipality part projections
    /// @return List of missing IDs
    private List<Long> getMissingIds(Collection<Long> requestedIds, List<AddressPlaceNameProjection> foundMunicipalityParts) {
        Set<Long> foundIds = foundMunicipalityParts.stream()
                                                   .map(AddressPlaceNameProjection::municipalityPartId)
                                                   .collect(Collectors.toSet());

        return requestedIds.stream()
                           .filter(id -> !foundIds.contains(id))
                           .toList();
    }

}
