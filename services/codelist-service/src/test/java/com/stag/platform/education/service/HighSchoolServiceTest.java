package com.stag.platform.education.service;

import com.stag.platform.education.exception.HighSchoolNotFoundException;
import com.stag.platform.education.repository.HighSchoolRepository;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HighSchoolServiceTest {

    @Mock
    private HighSchoolRepository highSchoolRepository;

    @InjectMocks
    private HighSchoolService highSchoolService;

    @Test
    @DisplayName("should return high school address when high school exists")
    void findHighSchoolAddressById_HighSchoolExists_ReturnsAddress() {
        String highSchoolId = "12345";
        HighSchoolAddressProjection projection = new HighSchoolAddressProjection(
            "Gymnazium",
            "Studentska 1",
            "12300",
            "Mesto",
            "Okres"
        );

        when(highSchoolRepository.findHighSchoolAddressById(highSchoolId)).thenReturn(Optional.of(projection));

        HighSchoolAddressProjection result = highSchoolService.findHighSchoolAddressById(highSchoolId);

        assertThat(result).isNotNull().isEqualTo(projection);
        verify(highSchoolRepository).findHighSchoolAddressById(highSchoolId);
    }

    @Test
    @DisplayName("should throw HighSchoolNotFoundException when high school does not exist")
    void findHighSchoolAddressById_HighSchoolNotFound_ThrowsException() {
        String highSchoolId = "99999";
        when(highSchoolRepository.findHighSchoolAddressById(highSchoolId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> highSchoolService.findHighSchoolAddressById(highSchoolId))
            .isInstanceOf(HighSchoolNotFoundException.class)
            .hasMessageContaining("High School not found for ID: " + highSchoolId)
            .extracting("highSchoolId").isEqualTo(highSchoolId);

        verify(highSchoolRepository).findHighSchoolAddressById(highSchoolId);
    }
}
