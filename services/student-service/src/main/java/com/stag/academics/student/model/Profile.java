package com.stag.academics.student.model;

import lombok.Builder;

import java.io.Serializable;

/// **Profile**
///
/// Complete student profile model containing personal information, academic status, and study program details.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
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

    /// **Titles**
    ///
    /// Academic titles (prefix and suffix) for a person.
    public record Titles(
        String prefix,
        String suffix
    ) implements Serializable {

    }

    /// **Study Program**
    ///
    /// Study program information including code, form, and type.
    public record StudyProgram(
        Integer id,
        String name,
        String faculty,
        String code,
        String form,
        String type
    ) implements Serializable {

    }

    /// **Field Of Study**
    ///
    /// Field of study (major) information with department details.
    public record FieldOfStudy(
        Integer id,
        String name,
        String faculty,
        String department,
        String code
    ) implements Serializable {

    }

}
