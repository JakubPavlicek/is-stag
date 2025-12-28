package com.stag.platform.education.exception;

import lombok.Getter;

/// **High School Field Of Study Not Found Exception**
///
/// Thrown when a field of study lookup by number fails.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class HighSchoolFieldOfStudyNotFoundException extends RuntimeException {

    /// Field of study number that was not found
    private final String fieldOfStudyNumber;

    /// Creates a new exception for a missing field of study.
    ///
    /// @param fieldOfStudyNumber Field of study number that was not found
    public HighSchoolFieldOfStudyNotFoundException(String fieldOfStudyNumber) {
        super("Field of study not found for ID: " + fieldOfStudyNumber);
        this.fieldOfStudyNumber = fieldOfStudyNumber;
    }

}