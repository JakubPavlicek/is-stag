package com.stag.platform.codelist.service;

import com.stag.platform.codelist.entity.Municipality;
import com.stag.platform.codelist.repository.MunicipalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MunicipalityService {

    private final MunicipalityRepository municipalityRepository;

    public Optional<Municipality> findById(Long id) {
        return municipalityRepository.findById(id);
    }

}
