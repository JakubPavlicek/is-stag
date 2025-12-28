package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.PersonMapper;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.identity.person.v1.PersonServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserClient {

    private PersonServiceGrpc.PersonServiceBlockingStub personServiceStub;

    public UserClient(PersonServiceGrpc.PersonServiceBlockingStub personServiceStub) {
        this.personServiceStub = personServiceStub;
    }

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
