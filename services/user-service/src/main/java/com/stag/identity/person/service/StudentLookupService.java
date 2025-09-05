package com.stag.identity.person.service;

import com.stag.identity.shared.grpc.client.StudentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentLookupService {

    private final StudentClient studentClient;

    @Async
    public CompletableFuture<List<String>> getStudentIds(Integer personId) {
        log.info("Fetching student ids");
        List<String> personalNumbers = studentClient.getStudentIds(personId);
        log.debug("Completed fetching student ids");

        return CompletableFuture.completedFuture(personalNumbers);
    }

    @Cacheable(value = "student-person-id", key = "#personalNumber")
    public Integer getStudentPersonId(String personalNumber) {
        log.info("Fetching student person id");
        Integer personId = studentClient.getStudentPersonId(personalNumber);
        log.debug("Completed fetching student person id");

        return personId;
    }

}
