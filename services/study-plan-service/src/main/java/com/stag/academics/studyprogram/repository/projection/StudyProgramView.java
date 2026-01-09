package com.stag.academics.studyprogram.repository.projection;

/// **Study Program View Projection**
///
/// Lightweight view of the study program containing basic identification and classification fields.
/// Used for efficient data transfer.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record StudyProgramView(
    Long id,
    String name,
    String faculty,
    String code,
    String form,
    String type
) {

}
