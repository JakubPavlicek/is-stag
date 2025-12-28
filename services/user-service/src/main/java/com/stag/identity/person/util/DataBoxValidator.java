package com.stag.identity.person.util;

import com.stag.identity.person.exception.InvalidDataBoxException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/// **Data Box ID Validator**
///
/// Utility class for validating Czech data box identifiers. Data boxes use a 7-character
/// base32-encoded format with Luhn mod-32 checksum validation. The validator ensures
/// both format correctness and checksum integrity.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public class DataBoxValidator {

    /// Private constructor to prevent instantiation
    private DataBoxValidator() {
    }

    /// Allowed characters - base32 alphabet used by Czech data boxes
    private static final String ALPHABET = "abcdefghijkmnpqrstuvwxyz23456789";
    /// Base for mod-32 calculation
    private static final int BASE = ALPHABET.length(); // 32
    /// Map of characters to their numeric values
    private static final Map<Character, Integer> CHAR_TO_VALUE = buildCharToValueMap();

    /// Builds the character-to-value mapping for base32 alphabet.
    ///
    /// @return immutable map of characters to their numeric values
    private static Map<Character, Integer> buildCharToValueMap() {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < ALPHABET.length(); i++) {
            map.put(ALPHABET.charAt(i), i);
        }
        return Collections.unmodifiableMap(map);
    }

    /// Validates a data box ID using format and checksum verification.
    ///
    /// @param id the data box ID to validate (must be 7 characters)
    /// @return true if valid
    /// @throws InvalidDataBoxException if the checksum is invalid
    public static boolean isValidDataBoxId(String id) {
        if (id == null || id.length() != 7) {
            return false;
        }

        id = id.toLowerCase();

        // Check charset validity
        for (char c : id.toCharArray()) {
            if (!CHAR_TO_VALUE.containsKey(c)) {
                return false;
            }
        }

        // Separate payload and checksum
        String payload = id.substring(0, 6);
        char checkChar = id.charAt(6);

        // Compute checksum using Luhn mod-32
        int expectedCheckValue = computeLuhn32(payload);
        int actualCheckValue = CHAR_TO_VALUE.get(checkChar);

        boolean isValid = expectedCheckValue == actualCheckValue;

        if (!isValid) {
            throw new InvalidDataBoxException(id);
        }

        return true;
    }

    /// Computes the Luhn mod-32 checksum for a given input string.
    ///
    /// @param input the payload string (6 characters)
    /// @return the computed checksum value (0-31)
    private static int computeLuhn32(String input) {
        int factor = 2;
        int sum = 0;

        for (int i = input.length() - 1; i >= 0; i--) {
            int codePoint = CHAR_TO_VALUE.get(input.charAt(i));
            int addend = factor * codePoint;

            // alternate the factor
            factor = (factor == 2) ? 1 : 2;

            // Split into a sum of digits in base 32
            addend = (addend / BASE) + (addend % BASE);
            sum += addend;
        }

        int remainder = sum % BASE;
        return (BASE - remainder) % BASE;
    }

}
