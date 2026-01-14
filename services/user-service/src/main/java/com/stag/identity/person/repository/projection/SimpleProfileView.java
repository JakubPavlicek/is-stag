package com.stag.identity.person.repository.projection;

/// **Simple Profile View Projection**
///
/// Minimal view of a person profile containing only basic identification fields.
/// Used for lightweight profile retrieval.
///
/// @param firstName the first name
/// @param lastName the last name
/// @param titlePrefix the title prefix
/// @param titleSuffix the title suffix
/// @param gender the gender
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record SimpleProfileView(
    String firstName,
    String lastName,
    String titlePrefix,
    String titleSuffix,
    String gender
) {

}
