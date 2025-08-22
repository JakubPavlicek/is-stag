package com.stag.identity.person.service;

import com.stag.identity.person.repository.projection.PersonAddressProjection;
import com.stag.identity.person.repository.projection.PersonBankProjection;
import com.stag.identity.person.repository.projection.PersonEducationProjection;
import com.stag.identity.person.repository.projection.PersonProfileProjection;
import com.stag.identity.person.service.data.PersonAddressData;
import com.stag.identity.person.service.data.PersonBankingData;
import com.stag.identity.person.service.data.PersonEducationData;
import com.stag.identity.person.service.data.PersonProfileData;
import com.stag.identity.shared.grpc.GrpcCodelistServiceClient;
import com.stag.identity.shared.grpc.GrpcStudentServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonAsyncService {

    private final GrpcStudentServiceClient grpcStudentServiceClient;
    private final GrpcCodelistServiceClient grpcCodelistServiceClient;

    @Async
    public CompletableFuture<List<String>> getStudentPersonalNumbers(Integer personId) {
        log.info("Fetching student personal numbers");
        List<String> personalNumbers = grpcStudentServiceClient.getStudentPersonalNumbers(personId);
        log.debug("Completed fetching student personal numbers");

        return CompletableFuture.completedFuture(personalNumbers);
    }

    @Async
    public CompletableFuture<PersonProfileData> getPersonProfileData(PersonProfileProjection personProfile, String language) {
        log.info("Fetching person profile data");
        PersonProfileData personProfileData = grpcCodelistServiceClient.getPersonProfileData(personProfile, language);
        log.debug("Completed fetching person profile data");

        return CompletableFuture.completedFuture(personProfileData);
    }

    @Async
    public CompletableFuture<PersonAddressData> getPersonAddressData(PersonAddressProjection personAddress, String language) {
        log.info("Fetching person address data");
        PersonAddressData personAddressData = grpcCodelistServiceClient.getPersonAddressData(personAddress, language);
        log.debug("Completed fetching person address data");

        return CompletableFuture.completedFuture(personAddressData);
    }

    @Async
    public CompletableFuture<PersonBankingData> getPersonBankingData(PersonBankProjection personBank, String language) {
        log.info("Fetching person banking data");
        PersonBankingData personBankingData = grpcCodelistServiceClient.getPersonBankingData(personBank, language);
        log.debug("Completed fetching person banking data");

        return CompletableFuture.completedFuture(personBankingData);
    }

    @Async
    public CompletableFuture<PersonEducationData> getPersonEducationData(PersonEducationProjection personEducation, String language) {
        log.info("Fetching person education data");
        PersonEducationData personEducationData = grpcCodelistServiceClient.getPersonEducationData(personEducation, language);
        log.debug("Completed fetching person education data");

        return CompletableFuture.completedFuture(personEducationData);
    }

}
