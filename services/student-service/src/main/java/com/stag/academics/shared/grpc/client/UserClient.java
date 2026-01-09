package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.PersonMapper;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.identity.person.v1.PersonServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
public class UserClient {

    /// Person service stub
    private PersonServiceGrpc.PersonServiceBlockingStub personServiceStub;

    /// Constructs the user client with a person service stub.
    ///
    /// @param personServiceStub the gRPC blocking stub for person service
    public UserClient(PersonServiceGrpc.PersonServiceBlockingStub personServiceStub) {
        this.personServiceStub = personServiceStub;
    }

    /// Fetches simple profile data for a person via gRPC.
    ///
    /// Includes circuit breaker for fault tolerance and caching for performance.
    ///
    /// @param personId the person identifier
    /// @param language the language code for localized data
    /// @return simple profile lookup data
    @Cacheable(value = "person-simple-profile", key = "{#personId, #language}")
    @CircuitBreaker(name = "user-service")
    @Retry(name = "user-service")
    public SimpleProfileLookupData getPersonSimpleProfileData(Integer personId, String language) {
        log.info("Fetching student simple profile for personId: {}", personId);

        var request = PersonMapper.INSTANCE.toSimpleProfileDataRequest(personId, language);
        var response = personServiceStub.getPersonSimpleProfile(request);

        log.debug("Completed fetching student simple profile");

        return PersonMapper.INSTANCE.toSimpleProfileData(response);
    }

}
