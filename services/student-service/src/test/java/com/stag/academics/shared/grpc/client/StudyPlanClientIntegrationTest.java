package com.stag.academics.shared.grpc.client;

import com.stag.academics.config.TestCacheConfig;
import com.stag.academics.config.TestOracleContainerConfig;
import com.stag.academics.shared.grpc.server.FakeStudyPlanService;
import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import com.stag.academics.studyplan.v1.FieldOfStudy;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import com.stag.academics.studyplan.v1.StudyProgram;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class, FakeStudyPlanService.class })
@ActiveProfiles("test")
class StudyPlanClientIntegrationTest {

    @Autowired
    private StudyPlanClient studyPlanClient;

    @Autowired
    private FakeStudyPlanService fakeStudyPlanService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        fakeStudyPlanService.clear();
        circuitBreakerRegistry.circuitBreaker("study-plan-service")
                              .transitionToClosedState();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public StudyPlanServiceGrpc.StudyPlanServiceBlockingStub studyPlanServiceStub() {
            // Create a channel to the in-process server named "test"
            ManagedChannel channel = InProcessChannelBuilder.forName("test")
                                                            .directExecutor()
                                                            .build();

            return StudyPlanServiceGrpc.newBlockingStub(channel);
        }

    }

    @Test
    @DisplayName("getStudyProgramAndField should return data from fake server")
    void getStudyProgramAndField_ReturnsData() {
        Long programId = 1L;
        Long planId = 2L;
        String language = "en";

        StudyProgram studyProgram = StudyProgram.newBuilder()
                                                .setId(programId)
                                                .setName("Program Name")
                                                .setFaculty("FAC")
                                                .setForm("Form")
                                                .setType("Type")
                                                .build();
        FieldOfStudy fieldOfStudy = FieldOfStudy.newBuilder()
                                         .setId(10L)
                                         .setName("Field Name")
                                         .setFaculty("FAC")
                                         .setDepartment("DEP")
                                         .setCode("C2")
                                         .build();

        GetStudyProgramAndFieldResponse expectedResponse = GetStudyProgramAndFieldResponse.newBuilder()
                                                                                          .setStudyProgram(studyProgram)
                                                                                          .setFieldOfStudy(fieldOfStudy)
                                                                                          .build();
        fakeStudyPlanService.addResponse(programId, planId, language, expectedResponse);

        StudyProgramAndFieldLookupData result = studyPlanClient.getStudyProgramAndField(programId, planId, language);

        assertThat(result.studyProgram().name()).isEqualTo("Program Name");
        assertThat(result.fieldOfStudy().name()).isEqualTo("Field Name");
    }

    @Test
    @DisplayName("getStudyProgramAndField should throw exception when not found")
    void getStudyProgramAndField_NotFound_ThrowsException() {
        Long programId = 999L;
        Long planId = 888L;
        String language = "en";

        assertThatThrownBy(() -> studyPlanClient.getStudyProgramAndField(programId, planId, language))
            .isInstanceOf(StatusRuntimeException.class)
            .extracting(e -> ((StatusRuntimeException) e).getStatus().getCode())
            .isEqualTo(Status.Code.NOT_FOUND);
    }

    @Test
    @DisplayName("getStudyProgramAndField should retry on failure")
    void getStudyProgramAndField_RetriesOnFailure() {
        Long programId = 1L;
        Long planId = 2L;
        String language = "en";

        StudyProgram studyProgram = StudyProgram.newBuilder()
                                                .setName("Success")
                                                .build();
        FieldOfStudy fie = FieldOfStudy.newBuilder()
                                       .setName("Success")
                                       .build();

        // Fail 2 times, succeed on 3rd
        fakeStudyPlanService.setFailNextCalls(2, Status.UNAVAILABLE);

        GetStudyProgramAndFieldResponse response = GetStudyProgramAndFieldResponse.newBuilder()
                                                                                  .setStudyProgram(studyProgram)
                                                                                  .setFieldOfStudy(fie)
                                                                                  .build();
        fakeStudyPlanService.addResponse(programId, planId, language, response);

        StudyProgramAndFieldLookupData result = studyPlanClient.getStudyProgramAndField(programId, planId, language);

        assertThat(result.studyProgram().name()).isEqualTo("Success");
        assertThat(fakeStudyPlanService.getGetStudyProgramAndFieldCallCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("getStudyProgramAndField should open circuit breaker after threshold failures")
    void getStudyProgramAndField_CircuitBreakerOpens() {
        Long programId = 1L;
        Long planId = 2L;
        String language = "en";

        fakeStudyPlanService.setFailNextCalls(10, Status.UNAVAILABLE);

        // Call until CB opens (5 calls as per application-test.yaml)
        for (int i = 0; i < 5; i++) {
            try {
                studyPlanClient.getStudyProgramAndField(programId, planId, language);
            } catch (Exception _) {
                // ignore
            }
        }

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("study-plan-service");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        assertThatThrownBy(() -> studyPlanClient.getStudyProgramAndField(programId, planId, language))
            .isInstanceOf(CallNotPermittedException.class);
    }

}
