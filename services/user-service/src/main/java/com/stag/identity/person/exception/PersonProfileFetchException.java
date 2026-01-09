package com.stag.identity.person.exception;

import lombok.Getter;

/// **Person Profile Fetch Exception**
///
/// Thrown when concurrently fetching data using Structured Concurrency.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class PersonProfileFetchException extends RuntimeException {

    /// Person's ID
    private final Integer personId;

    /// Creates exception with the missing person ID.
    ///
    /// @param personId the person identifier
    /// @param cause the cause
    public PersonProfileFetchException(Integer personId, Throwable cause) {
        super("Failed to fetch person profile for personId=" + personId, cause);
        this.personId = personId;
    }

}
