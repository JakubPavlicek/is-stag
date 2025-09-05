package com.stag.academics.student.service.data;

public record StudyProgramAndFieldLookupData(
    StudyProgram studyProgram,
    FieldOfStudy fieldOfStudy
) {

    public record StudyProgram(
        Long id,
        String name,
        String faculty,
        String code,
        String form,
        String type
    ) {

    }

    public record FieldOfStudy(
        Long id,
        String name,
        String faculty,
        String department,
        String code
    ) {

    }

}
