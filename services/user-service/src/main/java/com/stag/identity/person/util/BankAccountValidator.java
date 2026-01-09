package com.stag.identity.person.util;

import com.stag.identity.person.exception.InvalidAccountNumberException;

/// **Bank Account Number Validator**
///
/// Utility class for validating Czech bank account numbers using mod-11 checksum.
/// The validator uses standard weights defined by Czech banking regulations to verify account number integrity.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public class BankAccountValidator {

    /// Mod-11 weights applied from right to left
    private static final int[] WEIGHTS = { 1, 2, 4, 8, 5, 10, 9, 7, 3, 6 };

    /// Private constructor to prevent instantiation
    private BankAccountValidator() {
    }

    /// Validates a bank account number using mod-11 checksum.
    ///
    /// @param number the account number to validate (max 10 digits)
    /// @return true if the checksum is valid
    /// @throws InvalidAccountNumberException if the checksum is invalid
    public static boolean isValidChecksum(String number) {
        if (number == null) {
            return false;
        }

        int sum = 0;
        int weightIndex = 0;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = number.charAt(i) - '0';
            if (weightIndex >= WEIGHTS.length) {
                return false; // too long
            }
            sum += digit * WEIGHTS[weightIndex++];
        }

        boolean isValid = sum % 11 == 0;

        if (!isValid) {
            throw new InvalidAccountNumberException(number);
        }

        return true;
    }

}
