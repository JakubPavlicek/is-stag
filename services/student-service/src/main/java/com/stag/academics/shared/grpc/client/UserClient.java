package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.PersonMapper;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.identity.user.v1.UserServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/// **User Client**
///
/// gRPC client for communicating with the Person service to fetch person profile data.
/// Includes circuit breaker, retry logic, and caching.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@Service
@RequiredArgsConstructor
public class UserClient {

    /// Person service stub
    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    /// Fetches simple profile data for a person via gRPC.
    ///
    /// Includes circuit breaker for fault tolerance.
    ///
    /// @param personId the person identifier
    /// @param language the language code for localized data
    /// @return simple profile lookup data
    @CircuitBreaker(name = "user-service")
    @Retry(name = "user-service")
    public SimpleProfileLookupData getPersonSimpleProfileData(Integer personId, String language) {
        log.info("Fetching student simple profile for personId: {}", personId);

        var request = PersonMapper.INSTANCE.toSimpleProfileDataRequest(personId, language);
        var response = userServiceStub.getPersonSimpleProfile(request);

        log.debug("Completed fetching student simple profile");

        return PersonMapper.INSTANCE.toSimpleProfileData(response);
    }

}
