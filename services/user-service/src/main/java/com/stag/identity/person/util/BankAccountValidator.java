package com.stag.identity.person.util;

import com.stag.identity.person.exception.InvalidAccountNumberException;

public class BankAccountValidator {

    // Mod-11 weights (from right to left)
    private static final int[] WEIGHTS = { 1, 2, 4, 8, 5, 10, 9, 7, 3, 6 };

    private BankAccountValidator() {
    }

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
