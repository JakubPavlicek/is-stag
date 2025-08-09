package com.stag.platform.education.exception;

import lombok.Getter;

@Getter
public class HighSchoolFieldOfStudyNotFoundException extends RuntimeException {

    private final String fieldOfStudyNumber;

    public HighSchoolFieldOfStudyNotFoundException(String fieldOfStudyNumber) {
        super("Field of study not found for ID: " + fieldOfStudyNumber);
        this.fieldOfStudyNumber = fieldOfStudyNumber;
    }

}