package com.stag.academics.shared.grpc.client;

import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.GetCodelistValuesRequest;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodelistClientTest {

    @Mock
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    @InjectMocks
    private CodelistClient codelistClient;

    @Test
    @DisplayName("should return enriched data when gRPC call succeeds")
    void getStudyProgramData_Success_ReturnsMeanings() {
        StudyProgramView studyProgram = Instancio.create(StudyProgramView.class);
        String language = "en";

        CodelistMeaning meaning = CodelistMeaning.newBuilder()
            .setDomain("FORMA_OBORU_NEW")
            .setLowValue("P")
            .setMeaning("Full-time")
            .build();

        GetCodelistValuesResponse response = GetCodelistValuesResponse.newBuilder()
            .addCodelistMeanings(meaning)
            .build();

        when(codelistServiceStub.getCodelistValues(any(GetCodelistValuesRequest.class)))
            .thenReturn(response);

        CodelistMeaningsLookupData result = codelistClient.getStudyProgramData(studyProgram, language);

        assertThat(result).isNotNull();
        assertThat(result.codelistMeanings()).hasSize(1);
        verify(codelistServiceStub).getCodelistValues(any(GetCodelistValuesRequest.class));
    }

    @Test
    @DisplayName("should propagate exception when gRPC call fails")
    void getStudyProgramData_GrpcError_ThrowsException() {
        StudyProgramView studyProgram = Instancio.create(StudyProgramView.class);
        String language = "cs";
        StatusRuntimeException expectedException = new StatusRuntimeException(Status.UNAVAILABLE);

        when(codelistServiceStub.getCodelistValues(any(GetCodelistValuesRequest.class)))
            .thenThrow(expectedException);

        assertThatThrownBy(() -> codelistClient.getStudyProgramData(studyProgram, language))
            .isEqualTo(expectedException);

        verify(codelistServiceStub).getCodelistValues(any(GetCodelistValuesRequest.class));
    }
}
