package com.stag.platform.codelist.service;

import com.stag.platform.codelist.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.codelist.repository.MunicipalityPartRepository;
import com.stag.platform.codelist.repository.projection.CountryNameProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MunicipalityPartService {

    private final MunicipalityPartRepository municipalityPartRepository;

    public Map<Long, AddressPlaceNameProjection> findAddressNamesByIds(Collection<Long> ids) {
        return municipalityPartRepository.findAddressNamesByIds(ids)
                                         .stream()
                                         .collect(Collectors.toMap(
                                             AddressPlaceNameProjection::municipalityPartId,
                                             addressPlaceNameProjection -> addressPlaceNameProjection
                                         ));
    }

}
