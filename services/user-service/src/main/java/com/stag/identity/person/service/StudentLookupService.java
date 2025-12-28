package com.stag.identity.person.service;

import com.stag.identity.shared.grpc.client.StudentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/// **Student Lookup Service**
///
/// Asynchronous service for retrieving student-related data from student-service via gRPC.
/// Provides bidirectional lookups between person IDs and student IDs with caching support.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class StudentLookupService {

    /// Student Client
    private final StudentClient studentClient;

    /// Fetches all student IDs (personal numbers) associated with a person.
    /// Asynchronous operation for parallel data loading with profile retrieval.
    ///
    /// @param personId the person identifier
    /// @return future containing list of student personal numbers
    @Async
    public CompletableFuture<List<String>> getStudentIds(Integer personId) {
        log.info("Fetching student ids");
        List<String> personalNumbers = studentClient.getStudentIds(personId);
        log.debug("Completed fetching student ids");

        return CompletableFuture.completedFuture(personalNumbers);
    }

    /// Resolves person ID from student ID for authorization checks.
    /// Result is cached to optimize frequent authorization validations.
    ///
    /// @param studentId the student identifier
    /// @return person identifier associated with the student
    @Cacheable(value = "student-person-id", key = "#studentId")
    public Integer getStudentPersonId(String studentId) {
        log.info("Fetching student person id");
        Integer personId = studentClient.getStudentPersonId(studentId);
        log.debug("Completed fetching student person id");

        return personId;
    }

}
