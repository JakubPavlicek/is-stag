package com.stag.identity.shared.grpc.client;

import com.stag.academics.student.v1.StudentServiceGrpc;
import com.stag.identity.config.TestCacheConfig;
import com.stag.identity.config.TestOracleContainerConfig;
import com.stag.identity.shared.grpc.server.FakeStudentService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class, FakeStudentService.class })
@ActiveProfiles("test")
class StudentClientIntegrationTest {

    @Autowired
    private StudentClient studentClient;

    @Autowired
    private FakeStudentService fakeStudentService;
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        fakeStudentService.clear();
        circuitBreakerRegistry.circuitBreaker("student-service").transitionToClosedState();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub() {
            // Create a channel to the in-process server named "test"
            ManagedChannel channel = InProcessChannelBuilder.forName("test")
                    .directExecutor()
                    .build();
            
            return StudentServiceGrpc.newBlockingStub(channel);
        }
    }

    @Test
    @DisplayName("getStudentIds should return list of student IDs from fake server")
    void getStudentIds_ReturnsIds() {
        Integer personId = 123;
        List<String> expectedIds = List.of("S001", "S002");
        fakeStudentService.addStudentIds(personId, expectedIds);

        List<String> result = studentClient.getStudentIds(personId);

        assertThat(result).containsExactlyElementsOf(expectedIds);
    }

    @Test
    @DisplayName("getStudentIds should return empty list when no students found")
    void getStudentIds_ReturnsEmpty() {
        Integer personId = 999;
        List<String> result = studentClient.getStudentIds(personId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getStudentPersonId should return person ID from fake server")
    void getStudentPersonId_ReturnsPersonId() {
        String studentId = "S001";
        Integer expectedPersonId = 123;
        fakeStudentService.addStudentPersonId(studentId, expectedPersonId);

        Integer result = studentClient.getStudentPersonId(studentId);

        assertThat(result).isEqualTo(expectedPersonId);
    }

    @Test
    @DisplayName("getStudentPersonId should throw exception when student not found")
    void getStudentPersonId_NotFound_ThrowsException() {
        String studentId = "UNKNOWN";

        assertThatThrownBy(() -> studentClient.getStudentPersonId(studentId))
            .isInstanceOf(StatusRuntimeException.class)
            .extracting(e -> ((StatusRuntimeException) e).getStatus().getCode())
            .isEqualTo(Status.Code.NOT_FOUND);
    }
    
    @Test
    @DisplayName("getStudentIds should retry on failure")
    void getStudentIds_RetriesOnFailure() {
        Integer personId = 123;
        // Fail 2 times, succeed on 3rd (max attempts are 3)
        fakeStudentService.setFailNextIdsCalls(2, Status.UNAVAILABLE);
        fakeStudentService.addStudentIds(personId, List.of("S001"));

        List<String> result = studentClient.getStudentIds(personId);

        assertThat(result).contains("S001");
        // Initial call + 2 retries = 3 calls
        assertThat(fakeStudentService.getGetStudentIdsCallCount()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("getStudentIds should fail after max retries")
    void getStudentIds_FailsAfterMaxRetries() {
        Integer personId = 123;
        // Fail 3 times (max attempts are 3)
        fakeStudentService.setFailNextIdsCalls(3, Status.UNAVAILABLE);

        assertThatThrownBy(() -> studentClient.getStudentIds(personId))
            .isInstanceOf(StatusRuntimeException.class);

        assertThat(fakeStudentService.getGetStudentIdsCallCount()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("getStudentIds should open circuit breaker after threshold failures")
    void getStudentIds_CircuitBreakerOpens() {
        Integer personId = 123;
        // Fail enough times to trip circuit breaker (min calls 5, threshold 50%)
        // We set 10 failures to be sure
        fakeStudentService.setFailNextIdsCalls(10, Status.UNAVAILABLE);

        // Call until CB opens
        for (int i = 0; i < 5; i++) {
            try {
                studentClient.getStudentIds(personId);
            } catch (Exception _) {
                // ignore
            }
        }
        
        // Verify CB is OPEN
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("student-service");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        
        // The next call should fail fast with CallNotPermittedException
        assertThatThrownBy(() -> studentClient.getStudentIds(personId))
            .isInstanceOf(CallNotPermittedException.class);
    }
}