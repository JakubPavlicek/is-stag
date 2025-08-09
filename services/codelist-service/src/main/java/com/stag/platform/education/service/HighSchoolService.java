package com.stag.platform.education.service;

import com.stag.platform.education.exception.HighSchoolNotFoundException;
import com.stag.platform.education.repository.HighSchoolRepository;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class HighSchoolService {

    private final HighSchoolRepository highSchoolRepository;

    @Transactional(readOnly = true)
    public HighSchoolAddressProjection findHighSchoolAddressById(String highSchoolId) {
        return highSchoolRepository.findHighSchoolAddressById(highSchoolId)
                                   .orElseThrow(() -> {
                                       String errorMessage = "High School not found for ID: " + highSchoolId;
                                       log.warn(errorMessage);
                                       return new HighSchoolNotFoundException(errorMessage);
                                   });
    }

}
