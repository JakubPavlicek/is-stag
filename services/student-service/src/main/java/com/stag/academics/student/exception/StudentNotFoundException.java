package com.stag.academics.student.exception;

import lombok.Getter;

@Getter
public class StudentNotFoundException extends RuntimeException {

    private final String studentId;

    public StudentNotFoundException(String studentId) {
        super("Student with ID: " + studentId + " not found");
        this.studentId = studentId;
    }

}
