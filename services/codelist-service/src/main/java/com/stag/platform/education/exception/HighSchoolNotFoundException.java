package com.stag.platform.education.exception;

import lombok.Getter;

@Getter
public class HighSchoolNotFoundException extends RuntimeException {

    private final String highSchoolId;

    public HighSchoolNotFoundException(String highSchoolId) {
        super("High School not found for ID: " + highSchoolId);
        this.highSchoolId = highSchoolId;
    }

}