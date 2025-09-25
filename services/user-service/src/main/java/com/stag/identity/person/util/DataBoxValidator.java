package com.stag.identity.person.util;

import com.stag.identity.person.exception.InvalidDataBoxException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataBoxValidator {

    private DataBoxValidator() {
    }

    // Allowed characters (base32 alphabet used by data boxes)
    private static final String ALPHABET = "abcdefghijkmnpqrstuvwxyz23456789";
    private static final int BASE = ALPHABET.length(); // 32
    private static final Map<Character, Integer> CHAR_TO_VALUE = buildCharToValueMap();

    private static Map<Character, Integer> buildCharToValueMap() {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < ALPHABET.length(); i++) {
            map.put(ALPHABET.charAt(i), i);
        }
        return Collections.unmodifiableMap(map);
    }

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
