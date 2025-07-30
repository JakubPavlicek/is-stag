package com.stag.identity.user.service;

import com.stag.identity.user.grpc.CodelistServiceClient;
import com.stag.identity.user.grpc.StudentServiceClient;
import com.stag.identity.user.repository.projection.PersonAddressProjection;
import com.stag.identity.user.repository.projection.PersonBankProjection;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonBankingData;
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
    public CompletableFuture<PersonProfileData> getPersonProfileData(PersonProfileProjection personProfile) {
        log.info("getPersonProfileData thread: {}", Thread.currentThread());

        PersonProfileData personProfileData = codelistServiceClient.getPersonProfileData(personProfile);

        return CompletableFuture.completedFuture(personProfileData);
    }

    @Async
    public CompletableFuture<PersonAddressData> getPersonAddressData(PersonAddressProjection personAddressProjection) {
        log.info("getPersonAddressData thread: {}", Thread.currentThread());

        PersonAddressData personAddressData = codelistServiceClient.getPersonAddressData(personAddressProjection);

        return CompletableFuture.completedFuture(personAddressData);
    }

    @Async
    public CompletableFuture<PersonBankingData> getPersonBankingData(PersonBankProjection personBankProjection) {
        log.info("getPersonBankingData thread: {}", Thread.currentThread());

        PersonBankingData personBankingData = codelistServiceClient.getPersonBankingData(personBankProjection);

        return CompletableFuture.completedFuture(personBankingData);
    }

}
