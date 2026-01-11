package com.stag.identity.person.util;

import com.stag.identity.person.exception.InvalidAccountNumberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountValidatorTest {

    @ParameterizedTest(name = "Account number {0} should be valid")
    @ValueSource(
        strings = {
            "0",
            "19",
            "115",
            "000000019",
            "0000000000"
        }
    )
    @DisplayName("Should return true for valid checksums")
    void isValidChecksum_ValidNumber_ReturnsTrue(String accountNumber) {
        assertThat(BankAccountValidator.isValidChecksum(accountNumber)).isTrue();
    }

    @ParameterizedTest(name = "Account number {0} should be invalid")
    @ValueSource(
        strings = {
            "1",
            "12",
            "1234567890",
            "9999999998"
        }
    )
    @DisplayName("Should throw InvalidAccountNumberException for invalid checksums")
    void isValidChecksum_InvalidChecksum_ThrowsException(String accountNumber) {
        assertThatThrownBy(() -> BankAccountValidator.isValidChecksum(accountNumber))
            .isInstanceOf(InvalidAccountNumberException.class)
            .hasMessageContaining(accountNumber);
    }

    @ParameterizedTest(name = "Account number {0} should be invalid format (false)")
    @NullAndEmptySource
    @ValueSource(
        strings = {
            " ",
            "\t",
            "\n"
        }
    )
    @DisplayName("Should return false for null or empty inputs")
    void isValidChecksum_NullOrEmpty_ReturnsFalse(String accountNumber) {
        assertThat(BankAccountValidator.isValidChecksum(accountNumber)).isFalse();
    }

    @ParameterizedTest(name = "Account number {0} should be too long (false)")
    @ValueSource(
        strings = {
            "12345678901",
            "00000000000"
        }
    )
    @DisplayName("Should return false for numbers exceeding max length")
    void isValidChecksum_TooLong_ReturnsFalse(String accountNumber) {
        assertThat(BankAccountValidator.isValidChecksum(accountNumber)).isFalse();
    }

}
