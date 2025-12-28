package com.stag.academics.student.exception;

import lombok.Getter;

/// **Student Not Found Exception**
///
/// Exception thrown when a student with the specified ID cannot be found.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class StudentNotFoundException extends RuntimeException {

    /// The student ID that was not found
    private final String studentId;

    /// Constructs a new exception with the student ID.
    ///
    /// @param studentId the ID of the student that was not found
    public StudentNotFoundException(String studentId) {
        super("Student with ID: " + studentId + " not found");
        this.studentId = studentId;
    }

}
