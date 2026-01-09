package com.stag.identity.shared.grpc.client;

import com.stag.academics.student.v1.GetStudentIdsRequest;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.StudentServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/// **Student gRPC Client**
///
/// gRPC client for student-service communication.
/// Provides bidirectional lookups between person IDs and student IDs with circuit breaker and retry patterns for resilience.
/// Used for profile enrichment and authorization checks.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@Service
public class StudentClient {

    /// Student Service stub
    private final StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub;

    /// Constructor for StudentClient
    ///
    /// @param studentServiceStub the gRPC blocking stub for student service
    public StudentClient(StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub) {
        this.studentServiceStub = studentServiceStub;
    }

    /// Retrieves all student IDs (personal numbers) for a person.
    ///
    /// @param personId the person identifier
    /// @return list of student personal numbers
    @CircuitBreaker(name = "student-service")
    @Retry(name = "student-service")
    public List<String> getStudentIds(Integer personId) {
        log.info("Fetching student ids");

        var request = GetStudentIdsRequest.newBuilder()
                                          .setPersonId(personId)
                                          .build();
        var response = studentServiceStub.getStudentIds(request);

        log.debug("Completed fetching student ids");

        return response.getStudentIdsList();
    }

    /// Resolves person ID from student ID for authorization checks.
    /// Result is used for ownership validation in @PreAuthorize expressions.
    ///
    /// @param studentId the student identifier
    /// @return person identifier associated with the student
    @Cacheable(value = "student-person-id", key = "#studentId")
    @CircuitBreaker(name = "student-service")
    @Retry(name = "student-service")
    public Integer getStudentPersonId(String studentId) {
        log.info("Fetching student person id");

        var request = GetStudentPersonIdRequest.newBuilder()
                                               .setStudentId(studentId)
                                               .build();
        var response = studentServiceStub.getStudentPersonId(request);

        log.debug("Completed fetching student person id");

        return response.getPersonId();
    }

}
