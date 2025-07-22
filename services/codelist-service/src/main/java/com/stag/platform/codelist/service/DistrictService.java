package com.stag.platform.codelist.service;

import com.stag.platform.codelist.entity.District;
import com.stag.platform.codelist.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;

    public Optional<District> findById(Integer id) {
        return districtRepository.findById(id);
    }

}
