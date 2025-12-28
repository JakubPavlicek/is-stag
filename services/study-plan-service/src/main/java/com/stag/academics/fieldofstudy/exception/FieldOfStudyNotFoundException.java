package com.stag.academics.fieldofstudy.exception;

import lombok.Getter;

/// **Field of Study Not Found Exception**
///
/// Exception thrown when a field of study for a given study plan does not exist.
/// Contains the study plan ID for error context.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class FieldOfStudyNotFoundException extends RuntimeException {

    /// Study plan ID for which field of study was not found
    private final Long studyPlanId;

    /// Creates exception with study plan ID.
    ///
    /// @param studyPlanId the study plan identifier for which field of study was not found
    public FieldOfStudyNotFoundException(Long studyPlanId) {
        super("Field of study not found for Study plan with ID: " + studyPlanId);
        this.studyPlanId = studyPlanId;
    }

}
