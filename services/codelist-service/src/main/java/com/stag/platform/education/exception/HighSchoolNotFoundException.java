package com.stag.platform.education.exception;

import lombok.Getter;

/// **High School Not Found Exception**
///
/// Thrown when a high school lookup by ID fails.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class HighSchoolNotFoundException extends RuntimeException {

    /// High school ID that was not found
    private final String highSchoolId;

    /// Creates a new exception for a missing high school.
    ///
    /// @param highSchoolId High school ID that was not found
    public HighSchoolNotFoundException(String highSchoolId) {
        super("High School not found for ID: " + highSchoolId);
        this.highSchoolId = highSchoolId;
    }

}