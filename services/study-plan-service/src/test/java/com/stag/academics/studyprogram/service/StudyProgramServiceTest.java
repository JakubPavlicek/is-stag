package com.stag.academics.studyprogram.service;

import com.stag.academics.shared.grpc.client.CodelistClient;
import com.stag.academics.shared.grpc.model.CodelistDomain;
import com.stag.academics.shared.grpc.model.CodelistEntryId;
import com.stag.academics.studyprogram.exception.StudyProgramNotFoundException;
import com.stag.academics.studyprogram.repository.StudyProgramRepository;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyProgramServiceTest {

    @Mock
    private StudyProgramRepository studyProgramRepository;

    @Mock
    private CodelistClient codelistClient;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private StudyProgramService studyProgramService;

    @BeforeEach
    void setUp() {
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        });
    }

    @Test
    @DisplayName("should return enriched study program view when program exists")
    void findStudyProgram_ProgramExists_ReturnsEnrichedView() {
        Long programId = 1L;
        String language = "en";
        String formCode = "P";
        String typeCode = "B";
        String formMeaning = "Full-time";
        String typeMeaning = "Bachelor";

        StudyProgramView rawView = Instancio.of(StudyProgramView.class)
                                            .set(field(StudyProgramView::id), programId)
                                            .set(field(StudyProgramView::form), formCode)
                                            .set(field(StudyProgramView::type), typeCode)
                                            .create();

        CodelistMeaningsLookupData lookupData = CodelistMeaningsLookupData.builder()
                                                                          .codelistMeanings(Map.of(
                                                                              new CodelistEntryId(CodelistDomain.FORMA_OBORU_NEW.name(), formCode), formMeaning,
                                                                              new CodelistEntryId(CodelistDomain.TYP_OBORU.name(), typeCode), typeMeaning
                                                                          ))
                                                                          .build();

        when(studyProgramRepository.findStudyProgramViewById(programId, language))
            .thenReturn(Optional.of(rawView));
        when(codelistClient.getStudyProgramData(rawView, language))
            .thenReturn(lookupData);

        StudyProgramView result = studyProgramService.findStudyProgram(programId, language);

        assertThat(result.id()).isEqualTo(programId);
        assertThat(result.form()).isEqualTo(formMeaning);
        assertThat(result.type()).isEqualTo(typeMeaning);
        assertThat(result.name()).isEqualTo(rawView.name());
        assertThat(result.faculty()).isEqualTo(rawView.faculty());
        assertThat(result.code()).isEqualTo(rawView.code());

        verify(studyProgramRepository).findStudyProgramViewById(programId, language);
        verify(codelistClient).getStudyProgramData(rawView, language);
    }

    @Test
    @DisplayName("should throw StudyProgramNotFoundException when program does not exist")
    void findStudyProgram_ProgramNotFound_ThrowsException() {
        Long programId = 999L;
        String language = "en";

        when(studyProgramRepository.findStudyProgramViewById(programId, language))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> studyProgramService.findStudyProgram(programId, language))
            .isInstanceOf(StudyProgramNotFoundException.class)
            .hasMessageContaining(String.valueOf(programId));

        verify(studyProgramRepository).findStudyProgramViewById(programId, language);
        verifyNoInteractions(codelistClient);
    }

    @Test
    @DisplayName("should propagate exception when repository throws exception")
    void findStudyProgram_RepositoryThrowsException_PropagatesException() {
        Long programId = 1L;
        String language = "en";
        RuntimeException expectedException = new RuntimeException("DB Error");

        when(studyProgramRepository.findStudyProgramViewById(programId, language))
            .thenThrow(expectedException);

        assertThatThrownBy(() -> studyProgramService.findStudyProgram(programId, language))
            .isEqualTo(expectedException);

        verify(studyProgramRepository).findStudyProgramViewById(programId, language);
        verifyNoInteractions(codelistClient);
    }

    @Test
    @DisplayName("should propagate exception when client throws exception")
    void findStudyProgram_ClientThrowsException_PropagatesException() {
        Long programId = 1L;
        String language = "en";
        StudyProgramView rawView = Instancio.create(StudyProgramView.class);
        RuntimeException expectedException = new RuntimeException("Client Error");

        when(studyProgramRepository.findStudyProgramViewById(programId, language))
            .thenReturn(Optional.of(rawView));
        when(codelistClient.getStudyProgramData(rawView, language))
            .thenThrow(expectedException);

        assertThatThrownBy(() -> studyProgramService.findStudyProgram(programId, language))
            .isEqualTo(expectedException);

        verify(studyProgramRepository).findStudyProgramViewById(programId, language);
        verify(codelistClient).getStudyProgramData(rawView, language);
    }

    @Test
    @DisplayName("should return original codes when codelist data is missing")
    void findStudyProgram_MissingCodelistData_ReturnsOriginalCodes() {
        Long programId = 1L;
        String language = "en";
        String formCode = "P";
        String typeCode = "B";

        StudyProgramView rawView = Instancio.of(StudyProgramView.class)
                                            .set(field(StudyProgramView::id), programId)
                                            .set(field(StudyProgramView::form), formCode)
                                            .set(field(StudyProgramView::type), typeCode)
                                            .create();

        CodelistMeaningsLookupData emptyLookupData = CodelistMeaningsLookupData.builder()
                                                                               .codelistMeanings(Map.of())
                                                                               .build();

        when(studyProgramRepository.findStudyProgramViewById(programId, language))
            .thenReturn(Optional.of(rawView));
        when(codelistClient.getStudyProgramData(rawView, language))
            .thenReturn(emptyLookupData);

        StudyProgramView result = studyProgramService.findStudyProgram(programId, language);

        assertThat(result.form()).isNull();
        assertThat(result.type()).isNull();
    }

    @Test
    @DisplayName("should return original codes when codelist data is null")
    void findStudyProgram_NullCodelistData_ReturnsNullFields() {
        Long programId = 1L;
        String language = "en";
        StudyProgramView rawView = Instancio.create(StudyProgramView.class);

        when(studyProgramRepository.findStudyProgramViewById(programId, language))
            .thenReturn(Optional.of(rawView));
        when(codelistClient.getStudyProgramData(rawView, language))
            .thenReturn(null);

        StudyProgramView result = studyProgramService.findStudyProgram(programId, language);

        assertThat(result.form()).isNull();
        assertThat(result.type()).isNull();
    }

}
