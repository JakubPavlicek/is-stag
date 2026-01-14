package com.stag.academics.student.model;

import lombok.Builder;

import java.io.Serializable;

/// **Profile**
///
/// Complete student profile model containing personal information, academic status, and study program details.
///
/// @param studentId Student ID
/// @param personId Person ID
/// @param firstName First name
/// @param lastName Last name
/// @param titles Academic titles
/// @param gender Gender
/// @param studyStatus Study status
/// @param studyProgram Study program details
/// @param fieldOfStudy Field of study details
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
    ///
    /// @param prefix the prefix
    /// @param suffix the suffix
    public record Titles(
        String prefix,
        String suffix
    ) implements Serializable {

    }

    /// **Study Program**
    ///
    /// Study program information including code, form, and type.
    ///
    /// @param id the study program ID
    /// @param name the name
    /// @param faculty the faculty
    /// @param code the code
    /// @param form the form
    /// @param type the type
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
    ///
    /// @param id the field of study ID
    /// @param name the name
    /// @param faculty the faculty
    /// @param department the department
    /// @param code the code
    public record FieldOfStudy(
        Integer id,
        String name,
        String faculty,
        String department,
        String code
    ) implements Serializable {

    }

}
