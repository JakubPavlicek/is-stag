package com.stag.platform.codelist.service;

import com.stag.platform.codelist.entity.MunicipalityPart;
import com.stag.platform.codelist.repository.MunicipalityPartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MunicipalityPartService {

    private final MunicipalityPartRepository municipalityPartRepository;

    public Optional<MunicipalityPart> findById(Long id) {
        return municipalityPartRepository.findById(id);
    }

}
