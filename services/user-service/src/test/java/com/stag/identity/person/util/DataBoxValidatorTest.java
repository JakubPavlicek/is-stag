package com.stag.identity.person.util;

import com.stag.identity.person.exception.InvalidDataBoxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataBoxValidatorTest {

    @ParameterizedTest(name = "Data box ID {0} should be valid")
    @ValueSource(
        strings = {
            "aaaaaaa",
            "baaaaa9",
            "AAAAAAA"
        }
    )
    @DisplayName("Should return true for valid data box IDs")
    void isValidDataBoxId_ValidId_ReturnsTrue(String id) {
        assertThat(DataBoxValidator.isValidDataBoxId(id)).isTrue();
    }

    @ParameterizedTest(name = "Data box ID {0} should be invalid")
    @ValueSource(
        strings = {
            "aaaaaab",
            "baaaaaa"
        }
    )
    @DisplayName("Should throw InvalidDataBoxException for invalid checksums")
    void isValidDataBoxId_InvalidChecksum_ThrowsException(String id) {
        assertThatThrownBy(() -> DataBoxValidator.isValidDataBoxId(id))
            .isInstanceOf(InvalidDataBoxException.class)
            .hasMessageContaining(id.toLowerCase());
    }

    @ParameterizedTest(name = "Data box ID {0} should be invalid format (false)")
    @NullSource
    @ValueSource(
        strings = {
            "",
            "123456",
            "12345678",
            "aaaaaa!",
            "aaaaaa1"
        }
    )
    @DisplayName("Should return false for invalid format or characters")
    void isValidDataBoxId_InvalidFormat_ReturnsFalse(String id) {
        assertThat(DataBoxValidator.isValidDataBoxId(id)).isFalse();
    }

}
