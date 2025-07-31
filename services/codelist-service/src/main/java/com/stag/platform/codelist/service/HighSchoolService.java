package com.stag.platform.codelist.service;

import com.stag.platform.codelist.repository.HighSchoolRepository;
import com.stag.platform.codelist.repository.projection.HighSchoolAddressProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HighSchoolService {

    private final HighSchoolRepository highSchoolRepository;

    // TODO: Add error handling
    public HighSchoolAddressProjection getHighSchoolName(String highSchoolId) {
        return highSchoolRepository.findHighSchoolAddressById(highSchoolId)
                                   .orElseThrow(() -> new IllegalArgumentException("High School not found for ID: " + highSchoolId));
    }

}
