package com.stag.platform.education.service;

import com.stag.platform.education.exception.HighSchoolFieldOfStudyNotFoundException;
import com.stag.platform.education.repository.HighSchoolFieldOfStudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/// **High School Field Of Study Service**
///
/// Manages high school field of study data retrieval.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class HighSchoolFieldOfStudyService {

    /// High School Field of Study Repository
    private final HighSchoolFieldOfStudyRepository fieldOfStudyRepository;

    /// Retrieves a field of study name by its number.
    ///
    /// @param fieldOfStudyNumber Field of study identifier
    /// @return Field of study name
    /// @throws HighSchoolFieldOfStudyNotFoundException if not found
    @Transactional(readOnly = true)
    public String findFieldOfStudyName(String fieldOfStudyNumber) {
        log.info("Finding field of study name by number: {}", fieldOfStudyNumber);

        return fieldOfStudyRepository.findNameById(fieldOfStudyNumber)
                                     .orElseThrow(() -> new HighSchoolFieldOfStudyNotFoundException(fieldOfStudyNumber));
    }

}
