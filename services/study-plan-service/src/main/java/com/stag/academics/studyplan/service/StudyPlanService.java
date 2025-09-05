package com.stag.academics.studyplan.service;

import com.stag.academics.fieldofstudy.exception.FieldOfStudyNotFoundException;
import com.stag.academics.studyplan.repository.StudyPlanRepository;
import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;

    @Transactional(readOnly = true)
    public FieldOfStudyView findFieldOfStudy(Long studyPlanId, String language) {
        return studyPlanRepository.findFieldOfStudy(studyPlanId, language)
                                  .orElseThrow(() -> new FieldOfStudyNotFoundException(studyPlanId));
    }

}
