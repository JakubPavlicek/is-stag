package com.stag.academics.student.service.data;

import java.io.Serializable;

/// **Study Program And Field Lookup Data**
///
/// Combined study program and field of study data retrieved from the Study Plan service.
/// Contains details about the student's academic program and major.
///
/// @param studyProgram Study program information
/// @param fieldOfStudy Field of study information
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record StudyProgramAndFieldLookupData(
    StudyProgram studyProgram,
    FieldOfStudy fieldOfStudy
) implements Serializable {

    /// **Study Program**
    ///
    /// Study program information from external service.
    ///
    /// @param id the study program ID
    /// @param name the name
    /// @param faculty the faculty
    /// @param code the code
    /// @param form the form
    /// @param type the type
    public record StudyProgram(
        Long id,
        String name,
        String faculty,
        String code,
        String form,
        String type
    ) implements Serializable {

    }

    /// **Field Of Study**
    ///
    /// Field of study (major) information from external service.
    ///
    /// @param id the field of study ID
    /// @param name the name
    /// @param faculty the faculty
    /// @param department the department
    /// @param code the code
    public record FieldOfStudy(
        Long id,
        String name,
        String faculty,
        String department,
        String code
    ) implements Serializable {

    }

}
