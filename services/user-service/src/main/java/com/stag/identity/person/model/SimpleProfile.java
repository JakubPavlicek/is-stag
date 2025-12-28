package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;

/// **Simple Profile Model**
///
/// Lightweight person profile containing only essential personal information.
/// Used for basic profile retrieval without full contact or citizenship details.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record SimpleProfile(
    String firstName,
    String lastName,
    Profile.Titles titles,
    String gender
) implements Serializable {

}
