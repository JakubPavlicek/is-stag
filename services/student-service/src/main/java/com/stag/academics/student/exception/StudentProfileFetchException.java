package com.stag.academics.student.exception;

import lombok.Getter;

/// **Student Profile Fetch Exception**
///
/// Thrown when concurrently fetching data using Structured Concurrency.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class StudentProfileFetchException extends RuntimeException {

    /// Student's ID
    private final String studentId;

    /// Creates exception with the missing person ID.
    ///
    /// @param studentId the student identifier
    /// @param cause the cause
    public StudentProfileFetchException(String studentId, Throwable cause) {
        super("Failed to fetch student profile for studentId=" + studentId, cause);
        this.studentId = studentId;
    }

}
