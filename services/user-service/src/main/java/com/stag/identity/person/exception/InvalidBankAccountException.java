package com.stag.identity.person.exception;

/// **Invalid Bank Account Exception**
///
/// Thrown when bank account validation fails due to invalid field combinations
/// or missing required data (e.g., suffix without bank code, prefix without a suffix).
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public class InvalidBankAccountException extends RuntimeException {

    /// Creates an exception with a descriptive message.
    ///
    /// @param message the error message
    public InvalidBankAccountException(String message) {
        super(message);
    }

}
