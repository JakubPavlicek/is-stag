package com.stag.identity.user.service;

import com.stag.identity.user.grpc.CodelistServiceClient;
import com.stag.identity.user.grpc.StudentServiceClient;
import com.stag.identity.user.repository.projection.PersonAddressProjection;
import com.stag.identity.user.repository.projection.PersonBankProjection;
import com.stag.identity.user.repository.projection.PersonEducationProjection;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonBankingData;
import com.stag.identity.user.service.data.PersonEducationData;
import com.stag.identity.user.service.data.PersonProfileData;
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

    private final StudentServiceClient studentServiceClient;
    private final CodelistServiceClient codelistServiceClient;

    @Async
    public CompletableFuture<List<String>> getStudentPersonalNumbers(Integer personId) {
        log.info("getStudentPersonalNumbers thread: {}", Thread.currentThread());

        List<String> personalNumbers = studentServiceClient.getStudentPersonalNumbers(personId);

        return CompletableFuture.completedFuture(personalNumbers);
    }

    @Async
    public CompletableFuture<PersonProfileData> getPersonProfileData(PersonProfileProjection personProfile, String language) {
        log.info("getPersonProfileData thread: {}", Thread.currentThread());

        PersonProfileData personProfileData = codelistServiceClient.getPersonProfileData(personProfile, language);

        return CompletableFuture.completedFuture(personProfileData);
    }

    @Async
    public CompletableFuture<PersonAddressData> getPersonAddressData(PersonAddressProjection personAddress, String language) {
        log.info("getPersonAddressData thread: {}", Thread.currentThread());

        PersonAddressData personAddressData = codelistServiceClient.getPersonAddressData(personAddress, language);

        return CompletableFuture.completedFuture(personAddressData);
    }

    @Async
    public CompletableFuture<PersonBankingData> getPersonBankingData(PersonBankProjection personBank, String language) {
        log.info("getPersonBankingData thread: {}", Thread.currentThread());

        PersonBankingData personBankingData = codelistServiceClient.getPersonBankingData(personBank, language);

        return CompletableFuture.completedFuture(personBankingData);
    }

    @Async
    public CompletableFuture<PersonEducationData> getPersonEducationData(PersonEducationProjection personEducation, String language) {
        log.info("getPersonEducationData thread: {}", Thread.currentThread());

        PersonEducationData personEducationData = codelistServiceClient.getPersonEducationData(personEducation, language);

        return CompletableFuture.completedFuture(personEducationData);
    }

}
