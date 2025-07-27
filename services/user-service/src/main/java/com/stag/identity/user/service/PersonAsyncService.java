package com.stag.identity.user.service;

import com.stag.identity.user.grpc.CodelistServiceClient;
import com.stag.identity.user.grpc.StudentServiceClient;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
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
    public CompletableFuture<List<String>> getStudentPersonalNumbersAsync(Integer personId) {
        log.info("personalNumbersFuture thread: {}", Thread.currentThread().toString());
        return CompletableFuture.completedFuture(studentServiceClient.getStudentPersonalNumbers(personId));
    }

    @Async
    public CompletableFuture<PersonProfileData> getPersonProfileDataAsync(PersonProfileProjection personProfile) {
        log.info("profileCodelistDataFuture thread: {}", Thread.currentThread().toString());
        return CompletableFuture.completedFuture(codelistServiceClient.getPersonProfileData(personProfile));
    }

}
