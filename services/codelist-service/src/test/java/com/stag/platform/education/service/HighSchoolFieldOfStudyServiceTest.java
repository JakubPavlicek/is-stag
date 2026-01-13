package com.stag.platform.education.service;

import com.stag.platform.education.exception.HighSchoolFieldOfStudyNotFoundException;
import com.stag.platform.education.repository.HighSchoolFieldOfStudyRepository;
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
class HighSchoolFieldOfStudyServiceTest {

    @Mock
    private HighSchoolFieldOfStudyRepository fieldOfStudyRepository;

    @InjectMocks
    private HighSchoolFieldOfStudyService fieldOfStudyService;

    @Test
    @DisplayName("should return field of study name when it exists")
    void findFieldOfStudyName_FieldOfStudyExists_ReturnsName() {
        String fieldOfStudyNumber = "79-41-K/41";
        String fieldOfStudyName = "Gymnázium";

        when(fieldOfStudyRepository.findNameById(fieldOfStudyNumber)).thenReturn(Optional.of(fieldOfStudyName));

        String result = fieldOfStudyService.findFieldOfStudyName(fieldOfStudyNumber);

        assertThat(result).isEqualTo(fieldOfStudyName);
        verify(fieldOfStudyRepository).findNameById(fieldOfStudyNumber);
    }

    @Test
    @DisplayName("should throw HighSchoolFieldOfStudyNotFoundException when field of study does not exist")
    void findFieldOfStudyName_FieldOfStudyNotFound_ThrowsException() {
        String fieldOfStudyNumber = "00-00-0/00";
        when(fieldOfStudyRepository.findNameById(fieldOfStudyNumber)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fieldOfStudyService.findFieldOfStudyName(fieldOfStudyNumber))
            .isInstanceOf(HighSchoolFieldOfStudyNotFoundException.class)
            .hasMessageContaining("Field of study not found for ID: " + fieldOfStudyNumber)
            .extracting("fieldOfStudyNumber").isEqualTo(fieldOfStudyNumber);

        verify(fieldOfStudyRepository).findNameById(fieldOfStudyNumber);
    }
}
