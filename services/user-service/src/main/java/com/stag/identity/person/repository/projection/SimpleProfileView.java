package com.stag.identity.person.repository.projection;

/// **Simple Profile View Projection**
///
/// Minimal view of a person profile containing only basic identification
/// fields. Used for lightweight profile retrieval.
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
