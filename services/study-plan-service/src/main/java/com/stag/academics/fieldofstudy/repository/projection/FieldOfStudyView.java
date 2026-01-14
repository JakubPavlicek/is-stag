package com.stag.academics.fieldofstudy.repository.projection;

/// **Field of Study View Projection**
///
/// Lightweight view of a field of study containing basic identification and organizational fields.
/// Used for efficient data transfer.
///
/// @param id Field of study ID
/// @param name Field of study name
/// @param faculty Faculty name
/// @param department Department name
/// @param code Field of study code
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record FieldOfStudyView(
    Long id,
    String name,
    String faculty,
    String department,
    String code
) {

}
