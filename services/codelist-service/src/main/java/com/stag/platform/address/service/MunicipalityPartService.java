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

@Slf4j
@Service
@RequiredArgsConstructor
public class MunicipalityPartService {

    private final MunicipalityPartRepository municipalityPartRepository;

    @Transactional(readOnly = true)
    public Map<Long, AddressPlaceNameProjection> findAddressNamesByIds(Collection<Long> ids) {
        List<AddressPlaceNameProjection> foundMunicipalityParts = municipalityPartRepository.findAddressNamesByIds(ids);

        ensureAllMunicipalityPartsWereFound(ids, foundMunicipalityParts);

        return foundMunicipalityParts.stream()
                                   .collect(Collectors.toMap(
                                       AddressPlaceNameProjection::municipalityPartId,
                                       addressPlaceNameProjection -> addressPlaceNameProjection
                                   ));
    }

    private void ensureAllMunicipalityPartsWereFound(Collection<Long> requestedIds, List<AddressPlaceNameProjection> foundMunicipalityParts) {
        if (requestedIds.size() == foundMunicipalityParts.size()) {
            return;
        }

        List<Long> missingIds = getMissingIds(requestedIds, foundMunicipalityParts);

        if (missingIds.isEmpty()) {
            return;
        }

        String formattedMissingIds = formatMissingIdsForError(missingIds);
        String errorMessage = "Unable to find municipality parts for IDs: [" + formattedMissingIds + "]";

        log.warn(errorMessage);
        throw new MunicipalityPartsNotFoundException(errorMessage);
    }

    private List<Long> getMissingIds(Collection<Long> requestedIds, List<AddressPlaceNameProjection> foundMunicipalityParts) {
        Set<Long> foundIds = foundMunicipalityParts.stream()
                                                  .map(AddressPlaceNameProjection::municipalityPartId)
                                                  .collect(Collectors.toSet());

        return requestedIds.stream()
                           .filter(id -> !foundIds.contains(id))
                           .toList();
    }

    private String formatMissingIdsForError(List<Long> missingIds) {
        return missingIds.stream()
                         .map(String::valueOf)
                         .collect(Collectors.joining(", "));
    }

}
