package com.stag.academics.student.service.data;

import java.io.Serializable;

/// **Study Program And Field Lookup Data**
///
/// Combined study program and field of study data retrieved from the Study Plan service.
/// Contains details about the student's academic program and major.
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
    public record FieldOfStudy(
        Long id,
        String name,
        String faculty,
        String department,
        String code
    ) implements Serializable {

    }

}
