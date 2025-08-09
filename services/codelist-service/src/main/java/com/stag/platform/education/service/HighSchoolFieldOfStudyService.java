package com.stag.platform.education.service;

import com.stag.platform.education.exception.HighSchoolFieldOfStudyNotFoundException;
import com.stag.platform.education.repository.HighSchoolFieldOfStudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class HighSchoolFieldOfStudyService {

    private final HighSchoolFieldOfStudyRepository fieldOfStudyRepository;

    @Transactional(readOnly = true)
    public String findFieldOfStudyName(String fieldOfStudyNumber) {
        return fieldOfStudyRepository.findNameById(fieldOfStudyNumber)
                                     .orElseThrow(() -> {
                                         String errorMessage = "Field of study not found for ID: " + fieldOfStudyNumber;
                                         log.warn(errorMessage);
                                         return new HighSchoolFieldOfStudyNotFoundException(errorMessage);
                                     });
    }

}
