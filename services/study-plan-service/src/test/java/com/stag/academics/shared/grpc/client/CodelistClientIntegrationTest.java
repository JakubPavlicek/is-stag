package com.stag.academics.shared.grpc.client;

import com.stag.academics.config.TestCacheConfig;
import com.stag.academics.config.TestOracleContainerConfig;
import com.stag.academics.shared.grpc.server.FakeCodelistService;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import org.instancio.Instancio;
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
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class, FakeCodelistService.class })
@ActiveProfiles("test")
class CodelistClientIntegrationTest {

    @Autowired
    private CodelistClient codelistClient;

    @Autowired
    private FakeCodelistService fakeCodelistService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        fakeCodelistService.resetStats();
        circuitBreakerRegistry.circuitBreaker("codelist-service")
                              .transitionToClosedState();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub() {
            ManagedChannel channel = InProcessChannelBuilder.forName("test")
                                                            .directExecutor()
                                                            .build();
            return CodelistServiceGrpc.newBlockingStub(channel);
        }

    }

    @Test
    @DisplayName("getStudyProgramData should return enriched data")
    void getStudyProgramData_ReturnsData() {
        StudyProgramView studyProgram = Instancio.create(StudyProgramView.class);

        CodelistMeaning codelistMeaning = CodelistMeaning.newBuilder()
                                                         .setDomain("FORMA_OBORU_NEW")
                                                         .setLowValue(studyProgram.form() != null ? studyProgram.form() : "P")
                                                         .setMeaning("Full-time")
                                                         .build();

        fakeCodelistService.setNextCodelistValuesResponse(
            GetCodelistValuesResponse.newBuilder()
                                     .addCodelistMeanings(codelistMeaning)
                                     .build()
        );

        CodelistMeaningsLookupData result = codelistClient.getStudyProgramData(studyProgram, "en");

        assertThat(result).isNotNull();
        assertThat(result.codelistMeanings()).hasSize(1);
    }

    @Test
    @DisplayName("getStudyProgramData should retry on failure")
    void getStudyProgramData_RetriesOnFailure() {
        StudyProgramView studyProgram = Instancio.create(StudyProgramView.class);

        // Fail 2 times, succeed on 3rd
        fakeCodelistService.setFailNextCalls(2, Status.UNAVAILABLE);

        codelistClient.getStudyProgramData(studyProgram, "en");

        // Initial call + 2 retries = 3 calls
        assertThat(fakeCodelistService.getCallCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("getStudyProgramData should fail after max retries")
    void getStudyProgramData_FailsAfterMaxRetries() {
        StudyProgramView studyProgram = Instancio.create(StudyProgramView.class);

        // Fail 3 times (max attempts are 3)
        fakeCodelistService.setFailNextCalls(3, Status.UNAVAILABLE);

        assertThatThrownBy(() -> codelistClient.getStudyProgramData(studyProgram, "en"))
            .isInstanceOf(StatusRuntimeException.class);

        assertThat(fakeCodelistService.getCallCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("getStudyProgramData should open circuit breaker after threshold failures")
    void getStudyProgramData_CircuitBreakerOpens() {
        StudyProgramView studyProgram = Instancio.create(StudyProgramView.class);

        // Fail enough times to trip circuit breaker (min calls 5, threshold 50%)
        fakeCodelistService.setFailNextCalls(10, Status.UNAVAILABLE);

        // Call until CB opens
        for (int i = 0; i < 5; i++) {
            try {
                codelistClient.getStudyProgramData(studyProgram, "en");
            } catch (Exception _) {
                // ignore
            }
        }

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("codelist-service");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // The next call should fail fast
        assertThatThrownBy(() -> codelistClient.getStudyProgramData(studyProgram, "en"))
            .isInstanceOf(CallNotPermittedException.class);
    }

}
