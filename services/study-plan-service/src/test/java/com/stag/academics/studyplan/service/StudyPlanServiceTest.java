package com.stag.academics.studyplan.service;

import com.stag.academics.fieldofstudy.exception.FieldOfStudyNotFoundException;
import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import com.stag.academics.studyplan.repository.StudyPlanRepository;
import org.instancio.Instancio;
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
class StudyPlanServiceTest {

    @Mock
    private StudyPlanRepository studyPlanRepository;

    @InjectMocks
    private StudyPlanService studyPlanService;

    @Test
    @DisplayName("should return field of study view when study plan exists")
    void findFieldOfStudy_StudyPlanExists_ReturnsFieldOfStudyView() {
        Long studyPlanId = 1L;
        String language = "en";
        FieldOfStudyView expectedView = Instancio.create(FieldOfStudyView.class);

        when(studyPlanRepository.findFieldOfStudy(studyPlanId, language))
            .thenReturn(Optional.of(expectedView));

        FieldOfStudyView result = studyPlanService.findFieldOfStudy(studyPlanId, language);

        assertThat(result).isEqualTo(expectedView);
        verify(studyPlanRepository).findFieldOfStudy(studyPlanId, language);
    }

    @Test
    @DisplayName("should throw FieldOfStudyNotFoundException when study plan does not exist")
    void findFieldOfStudy_StudyPlanDoesNotExist_ThrowsException() {
        Long studyPlanId = 999L;
        String language = "cs";

        when(studyPlanRepository.findFieldOfStudy(studyPlanId, language))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> studyPlanService.findFieldOfStudy(studyPlanId, language))
            .isInstanceOf(FieldOfStudyNotFoundException.class)
            .hasMessageContaining(String.valueOf(studyPlanId));

        verify(studyPlanRepository).findFieldOfStudy(studyPlanId, language);
    }

    @Test
    @DisplayName("should propagate exception when repository throws exception")
    void findFieldOfStudy_RepositoryThrowsException_PropagatesException() {
        Long studyPlanId = 1L;
        String language = "en";
        RuntimeException expectedException = new RuntimeException("Database error");

        when(studyPlanRepository.findFieldOfStudy(studyPlanId, language))
            .thenThrow(expectedException);

        assertThatThrownBy(() -> studyPlanService.findFieldOfStudy(studyPlanId, language))
            .isEqualTo(expectedException);

        verify(studyPlanRepository).findFieldOfStudy(studyPlanId, language);
    }

}
