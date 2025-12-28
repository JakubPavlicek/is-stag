package com.stag.platform.education.service;

import com.stag.platform.education.exception.HighSchoolNotFoundException;
import com.stag.platform.education.repository.HighSchoolRepository;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/// **High School Service**
///
/// Manages high school data retrieval.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class HighSchoolService {

    private final HighSchoolRepository highSchoolRepository;

    /// Retrieves high school address information by ID.
    ///
    /// @param highSchoolId High school identifier
    /// @return High school address projection
    /// @throws HighSchoolNotFoundException if not found
    @Transactional(readOnly = true)
    public HighSchoolAddressProjection findHighSchoolAddressById(String highSchoolId) {
        return highSchoolRepository.findHighSchoolAddressById(highSchoolId)
                                   .orElseThrow(() -> new HighSchoolNotFoundException(highSchoolId));
    }

}
