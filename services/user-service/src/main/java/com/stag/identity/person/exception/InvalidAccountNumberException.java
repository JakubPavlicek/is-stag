package com.stag.identity.person.exception;

import lombok.Getter;

/// **Invalid Account Number Exception**
///
/// Thrown when Czech bank account number validation fails due to incorrect mod-11 checksum.
/// Validates account prefix and suffix using weighted checksum algorithm specific to the Czech banking system.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class InvalidAccountNumberException extends RuntimeException {

    /// The invalid account number
    private final String accountNumber;

    /// Creates an exception with the invalid account number.
    ///
    /// @param accountNumber the invalid account number (prefix or suffix)
    public InvalidAccountNumberException(String accountNumber) {
        super("Invalid account number: " + accountNumber);
        this.accountNumber = accountNumber;
    }

}
