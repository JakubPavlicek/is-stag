package com.stag.academics.studyprogram.exception;

import lombok.Getter;

@Getter
public class StudyProgramNotFoundException extends RuntimeException {

    private final Long studyProgramId;

    public StudyProgramNotFoundException(Long studyProgramId) {
        super("Study program with ID: " + studyProgramId + " not found");
        this.studyProgramId = studyProgramId;
    }

}
