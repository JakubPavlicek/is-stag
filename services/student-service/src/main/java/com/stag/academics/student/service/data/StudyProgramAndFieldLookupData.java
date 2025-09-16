package com.stag.academics.student.service.data;

import java.io.Serializable;

public record StudyProgramAndFieldLookupData(
    StudyProgram studyProgram,
    FieldOfStudy fieldOfStudy
) implements Serializable {

    public record StudyProgram(
        Long id,
        String name,
        String faculty,
        String code,
        String form,
        String type
    ) implements Serializable {

    }

    public record FieldOfStudy(
        Long id,
        String name,
        String faculty,
        String department,
        String code
    ) implements Serializable {

    }

}
