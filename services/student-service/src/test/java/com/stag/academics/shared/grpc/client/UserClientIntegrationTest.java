package com.stag.academics.shared.grpc.client;

import com.stag.academics.config.TestCacheConfig;
import com.stag.academics.config.TestOracleContainerConfig;
import com.stag.academics.shared.grpc.server.FakePersonService;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.identity.person.v1.GetPersonSimpleProfileResponse;
import com.stag.identity.person.v1.PersonServiceGrpc;
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
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class, FakePersonService.class })
@ActiveProfiles("test")
class UserClientIntegrationTest {

    @Autowired
    private UserClient userClient;

    @Autowired
    private FakePersonService fakePersonService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        fakePersonService.clear();
        circuitBreakerRegistry.circuitBreaker("user-service")
                              .transitionToClosedState();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public PersonServiceGrpc.PersonServiceBlockingStub personServiceStub() {
            // Create a channel to the in-process server named "test"
            ManagedChannel channel = InProcessChannelBuilder.forName("test")
                                                            .directExecutor()
                                                            .build();

            return PersonServiceGrpc.newBlockingStub(channel);
        }

    }

    @Test
    @DisplayName("getPersonSimpleProfileData should return profile from fake server")
    void getPersonSimpleProfileData_ReturnsProfile() {
        Integer personId = 123;
        String language = "en";
        GetPersonSimpleProfileResponse expectedResponse = GetPersonSimpleProfileResponse.newBuilder()
                                                                                        .setFirstName("John")
                                                                                        .setLastName("Doe")
                                                                                        .setGender("M")
                                                                                        .build();
        fakePersonService.addProfile(personId, expectedResponse);

        SimpleProfileLookupData result = userClient.getPersonSimpleProfileData(personId, language);

        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.gender()).isEqualTo("M");
    }

    @Test
    @DisplayName("getPersonSimpleProfileData should throw exception when profile not found")
    void getPersonSimpleProfileData_NotFound_ThrowsException() {
        Integer personId = 999;
        String language = "en";

        assertThatThrownBy(() -> userClient.getPersonSimpleProfileData(personId, language))
            .isInstanceOf(StatusRuntimeException.class)
            .extracting(e -> ((StatusRuntimeException) e).getStatus().getCode())
            .isEqualTo(Status.Code.NOT_FOUND);
    }

    @Test
    @DisplayName("getPersonSimpleProfileData should retry on failure")
    void getPersonSimpleProfileData_RetriesOnFailure() {
        Integer personId = 123;
        String language = "en";
        // Fail 2 times, succeed on 3rd (max attempts are 3)
        fakePersonService.setFailNextCalls(2, Status.UNAVAILABLE);
        fakePersonService.addProfile(personId, GetPersonSimpleProfileResponse.newBuilder()
                                                                             .setFirstName("John")
                                                                             .setLastName("Doe")
                                                                             .setGender("M")
                                                                             .build());

        SimpleProfileLookupData result = userClient.getPersonSimpleProfileData(personId, language);

        assertThat(result.firstName()).isEqualTo("John");
        // Initial call + 2 retries = 3 calls
        assertThat(fakePersonService.getGetPersonSimpleProfileCallCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("getPersonSimpleProfileData should open circuit breaker after threshold failures")
    void getPersonSimpleProfileData_CircuitBreakerOpens() {
        Integer personId = 123;
        String language = "en";
        // Fail enough times to trip circuit breaker (sliding window size 5, threshold 50%)
        fakePersonService.setFailNextCalls(10, Status.UNAVAILABLE);

        // Call until CB opens (5 calls should be enough as per application-test.yaml)
        for (int i = 0; i < 5; i++) {
            try {
                userClient.getPersonSimpleProfileData(personId, language);
            } catch (Exception _) {
                // ignore
            }
        }

        // Verify CB is OPEN
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("user-service");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // The next call should fail fast with CallNotPermittedException
        assertThatThrownBy(() -> userClient.getPersonSimpleProfileData(personId, language))
            .isInstanceOf(CallNotPermittedException.class);
    }

}
