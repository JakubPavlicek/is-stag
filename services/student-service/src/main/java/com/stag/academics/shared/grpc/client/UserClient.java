package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.exception.ServiceUnavailableException;
import com.stag.academics.shared.grpc.mapper.PersonMapper;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.identity.person.v1.PersonServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserClient {

    @GrpcClient("user-service")
    private PersonServiceGrpc.PersonServiceBlockingStub personServiceStub;

    @CircuitBreaker(name = "user-service", fallbackMethod = "userFallback")
    @Retry(name = "user-service")
    public SimpleProfileLookupData getPersonSimpleProfileData(Integer personId, String language) {
        log.info("Fetching student simple profile for personId: {}", personId);

        var request = PersonMapper.INSTANCE.toSimpleProfileDataRequest(personId, language);
        var response = personServiceStub.withDeadlineAfter(300, TimeUnit.MILLISECONDS)
                                        .getPersonSimpleProfile(request);

        log.debug("Completed fetching student simple profile");

        return PersonMapper.INSTANCE.toSimpleProfileData(response);
    }

    private SimpleProfileLookupData userFallback(Throwable t) {
        throw new ServiceUnavailableException("User service", t);
    }

}
