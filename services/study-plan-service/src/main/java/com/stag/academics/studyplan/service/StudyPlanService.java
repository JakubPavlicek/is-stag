package com.stag.academics.studyplan.service;

import com.stag.academics.fieldofstudy.exception.FieldOfStudyNotFoundException;
import com.stag.academics.studyplan.repository.StudyPlanRepository;
import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/// **Study Plan Service**
///
/// Business logic layer for study plan operations. Retrieves field of study
/// information associated with study plans.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class StudyPlanService {

    /// Study plan repository
    private final StudyPlanRepository studyPlanRepository;

    /// Finds the field of study for a given study plan with localized data.
    ///
    /// @param studyPlanId the study plan identifier
    /// @param language the language code for localization
    /// @return localized field of study view
    /// @throws FieldOfStudyNotFoundException if field of study not found
    @Transactional(readOnly = true)
    public FieldOfStudyView findFieldOfStudy(Long studyPlanId, String language) {
        log.info("Fetching field of study for study plan with ID: {} and language: {}", studyPlanId, language);

        return studyPlanRepository.findFieldOfStudy(studyPlanId, language)
                                  .orElseThrow(() -> new FieldOfStudyNotFoundException(studyPlanId));
    }

}
