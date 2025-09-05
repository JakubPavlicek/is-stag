package com.stag.academics.fieldofstudy.exception;

import lombok.Getter;

@Getter
public class FieldOfStudyNotFoundException extends RuntimeException {

    private final Long studyPlanId;

    public FieldOfStudyNotFoundException(Long studyPlanId) {
        super("Field of study not found for Study plan with ID: " + studyPlanId);
        this.studyPlanId = studyPlanId;
    }

}
