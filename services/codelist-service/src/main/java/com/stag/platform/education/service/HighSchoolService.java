package com.stag.platform.education.service;

import com.stag.platform.education.repository.HighSchoolRepository;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HighSchoolService {

    private final HighSchoolRepository highSchoolRepository;

    // TODO: Add error handling
    public HighSchoolAddressProjection findHighSchoolAddressById(String highSchoolId) {
        return highSchoolRepository.findHighSchoolAddressById(highSchoolId)
                                   .orElseThrow(() -> new IllegalArgumentException("High School not found for ID: " + highSchoolId));
    }

}
