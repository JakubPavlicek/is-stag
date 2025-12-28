package com.stag.identity.person.exception;

import lombok.Getter;

/// **Person Not Found Exception**
///
/// Thrown when attempting to access a person's record that doesn't exist in the database.
/// Used for both REST and gRPC endpoints to indicate a missing person entity.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class PersonNotFoundException extends RuntimeException {

    /// The person ID that was not found
    private final Integer personId;

    /// Creates exception with the missing person ID.
    ///
    /// @param personId the person identifier that was not found
    public PersonNotFoundException(Integer personId) {
        super("Person with ID: " + personId + " not found");
        this.personId = personId;
    }

}
