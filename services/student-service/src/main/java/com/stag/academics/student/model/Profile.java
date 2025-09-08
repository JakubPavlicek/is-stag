package com.stag.academics.student.model;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record Profile(
    String studentId,
    Integer personId,
    String firstName,
    String lastName,
    Titles titles,
    String gender,
    String studyStatus,
    StudyProgram studyProgram,
    FieldOfStudy fieldOfStudy
) implements Serializable {

    public record Titles(
        String prefix,
        String suffix
    ) implements Serializable {

    }

    public record StudyProgram(
        Integer id,
        String name,
        String faculty,
        String code,
        String form,
        String type
    ) implements Serializable {

    }

    public record FieldOfStudy(
        Integer id,
        String name,
        String faculty,
        String department,
        String code
    ) implements Serializable {

    }

}
