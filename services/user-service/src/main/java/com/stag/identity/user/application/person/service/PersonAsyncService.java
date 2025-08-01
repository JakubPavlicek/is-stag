package com.stag.identity.user.application.person.service;

import com.stag.identity.user.application.person.dto.PersonAddressData;
import com.stag.identity.user.application.person.dto.PersonBankingData;
import com.stag.identity.user.application.person.dto.PersonEducationData;
import com.stag.identity.user.application.person.dto.PersonProfileData;
import com.stag.identity.user.application.person.port.out.CodelistServicePort;
import com.stag.identity.user.application.person.port.out.StudentServicePort;
import com.stag.identity.user.domain.person.model.PersonAddress;
import com.stag.identity.user.domain.person.model.PersonBank;
import com.stag.identity.user.domain.person.model.PersonEducation;
import com.stag.identity.user.domain.person.model.PersonId;
import com.stag.identity.user.domain.person.model.PersonProfile;
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

    private final StudentServicePort studentService;
    private final CodelistServicePort codelistService;

    @Async
    public CompletableFuture<List<String>> getStudentPersonalNumbers(PersonId personId) {
        log.info("getStudentPersonalNumbers thread: {}", Thread.currentThread());
        List<String> studentPersonalNumbers = studentService.getStudentPersonalNumbers(personId);
        return CompletableFuture.completedFuture(studentPersonalNumbers);
    }

    @Async
    public CompletableFuture<PersonProfileData> getPersonProfileData(PersonProfile personProfile) {
        log.info("getPersonProfileData thread: {}", Thread.currentThread());
        PersonProfileData personProfileData = codelistService.getPersonProfileData(personProfile);
        return CompletableFuture.completedFuture(personProfileData);
    }

    @Async
    public CompletableFuture<PersonAddressData> getPersonAddressData(PersonAddress personAddress) {
        log.info("getPersonAddressData thread: {}", Thread.currentThread());
        PersonAddressData personAddressData = codelistService.getPersonAddressData(personAddress);
        return CompletableFuture.completedFuture(personAddressData);
    }

    @Async
    public CompletableFuture<PersonBankingData> getPersonBankingData(PersonBank personBank) {
        log.info("getPersonBankingData thread: {}", Thread.currentThread());
        PersonBankingData personBankingData = codelistService.getPersonBankingData(personBank);
        return CompletableFuture.completedFuture(personBankingData);
    }

    @Async
    public CompletableFuture<PersonEducationData> getPersonEducationData(PersonEducation personEducation) {
        log.info("getPersonEducationData thread: {}", Thread.currentThread());
        PersonEducationData personEducationData = codelistService.getPersonEducationData(personEducation);
        return CompletableFuture.completedFuture(personEducationData);
    }

}
