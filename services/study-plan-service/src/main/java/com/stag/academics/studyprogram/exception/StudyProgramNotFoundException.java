package com.stag.academics.studyprogram.exception;

import lombok.Getter;

/// **Study Program Not Found Exception**
///
/// Exception thrown when a requested study program does not exist.
/// Contains the study program ID for error context.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class StudyProgramNotFoundException extends RuntimeException {

    /// Study program ID that was not found
    private final Long studyProgramId;

    /// Creates exception with study program ID.
    ///
    /// @param studyProgramId the study program identifier that was not found
    public StudyProgramNotFoundException(Long studyProgramId) {
        super("Study program with ID: " + studyProgramId + " not found");
        this.studyProgramId = studyProgramId;
    }

}
