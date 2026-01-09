package com.stag.identity.person.exception;

import lombok.Getter;

/// **Invalid Data Box Exception**
///
/// Thrown when Czech data box ID validation fails due to invalid format, incorrect checksum, or unsupported characters.
/// Data box IDs use base32 encoding with Luhn mod-32 checksum validation.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class InvalidDataBoxException extends RuntimeException {

    /// The invalid data box ID
    private final String dataBox;

    /// Creates an exception with the invalid data box ID.
    ///
    /// @param dataBox the invalid data box ID
    public InvalidDataBoxException(String dataBox) {
        super("Invalid data box: " + dataBox);
        this.dataBox = dataBox;
    }

}
