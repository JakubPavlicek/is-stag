package com.stag.identity.person.service;

import com.stag.identity.person.repository.projection.PersonAddressProjection;
import com.stag.identity.person.repository.projection.PersonBankProjection;
import com.stag.identity.person.repository.projection.PersonEducationProjection;
import com.stag.identity.person.repository.projection.PersonProfileProjection;
import com.stag.identity.person.service.data.PersonAddressData;
import com.stag.identity.person.service.data.PersonBankingData;
import com.stag.identity.person.service.data.PersonEducationData;
import com.stag.identity.person.service.data.PersonProfileData;
import com.stag.identity.shared.grpc.CodelistServiceClient;
import com.stag.identity.shared.grpc.StudentServiceClient;
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
        List<String> personalNumbers = studentServiceClient.getStudentPersonalNumbers(personId);

        return CompletableFuture.completedFuture(personalNumbers);
    }

    @Async
    public CompletableFuture<PersonProfileData> getPersonProfileData(PersonProfileProjection personProfile, String language) {
        PersonProfileData personProfileData = codelistServiceClient.getPersonProfileData(personProfile, language);

        return CompletableFuture.completedFuture(personProfileData);
    }

    @Async
    public CompletableFuture<PersonAddressData> getPersonAddressData(PersonAddressProjection personAddress, String language) {
        PersonAddressData personAddressData = codelistServiceClient.getPersonAddressData(personAddress, language);

        return CompletableFuture.completedFuture(personAddressData);
    }

    @Async
    public CompletableFuture<PersonBankingData> getPersonBankingData(PersonBankProjection personBank, String language) {
        PersonBankingData personBankingData = codelistServiceClient.getPersonBankingData(personBank, language);

        return CompletableFuture.completedFuture(personBankingData);
    }

    @Async
    public CompletableFuture<PersonEducationData> getPersonEducationData(PersonEducationProjection personEducation, String language) {
        PersonEducationData personEducationData = codelistServiceClient.getPersonEducationData(personEducation, language);

        return CompletableFuture.completedFuture(personEducationData);
    }

}
