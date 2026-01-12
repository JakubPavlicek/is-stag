package com.stag.academics.shared.grpc.client;

import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import com.stag.academics.studyplan.v1.FieldOfStudy;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldRequest;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import com.stag.academics.studyplan.v1.StudyProgram;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyPlanClientTest {

    @Mock
    private StudyPlanServiceGrpc.StudyPlanServiceBlockingStub studyPlanServiceStub;

    @InjectMocks
    private StudyPlanClient studyPlanClient;

    @Test
    @DisplayName("should return study program and field data when valid inputs provided")
    void getStudyProgramAndField_ValidInputs_ReturnsData() {
        long studyProgramId = 1L;
        long studyPlanId = 2L;
        String language = "en";

        StudyProgram studyProgram = StudyProgram.newBuilder()
                                                .setId(studyProgramId)
                                                .setName("Program Name")
                                                .setFaculty("FAC")
                                                .setForm("Form")
                                                .setType("Type")
                                                .setCode("C1")
                                                .build();

        FieldOfStudy fieldOfStudy = FieldOfStudy.newBuilder()
                                                .setId(10L)
                                                .setName("Field Name")
                                                .setFaculty("FAC")
                                                .setDepartment("DEP")
                                                .setCode("C2")
                                                .build();

        GetStudyProgramAndFieldResponse response = GetStudyProgramAndFieldResponse.newBuilder()
                                                                                  .setStudyProgram(studyProgram)
                                                                                  .setFieldOfStudy(fieldOfStudy)
                                                                                  .build();

        when(studyPlanServiceStub.getStudyProgramAndField(any(GetStudyProgramAndFieldRequest.class)))
            .thenReturn(response);

        StudyProgramAndFieldLookupData result = studyPlanClient.getStudyProgramAndField(studyProgramId, studyPlanId, language);

        ArgumentCaptor<GetStudyProgramAndFieldRequest> requestCaptor = ArgumentCaptor.forClass(GetStudyProgramAndFieldRequest.class);
        verify(studyPlanServiceStub).getStudyProgramAndField(requestCaptor.capture());

        GetStudyProgramAndFieldRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getStudyProgramId()).isEqualTo(studyProgramId);
        assertThat(capturedRequest.getStudyPlanId()).isEqualTo(studyPlanId);
        assertThat(capturedRequest.getLanguage()).isEqualTo(language);

        assertThat(result.studyProgram().name()).isEqualTo("Program Name");
        assertThat(result.fieldOfStudy().name()).isEqualTo("Field Name");
    }

}
