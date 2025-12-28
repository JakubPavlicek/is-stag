package com.stag.identity.shared.grpc.client;

import com.stag.academics.student.v1.GetStudentIdsRequest;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.StudentServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StudentClient {

    private final StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub;

    public StudentClient(StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub) {
        this.studentServiceStub = studentServiceStub;
    }

    @CircuitBreaker(name = "student-service")
    @Retry(name = "student-service")
    public List<String> getStudentIds(Integer personId) {
        var request = GetStudentIdsRequest.newBuilder()
                                          .setPersonId(personId)
                                          .build();
        var response = studentServiceStub.getStudentIds(request);
        return response.getStudentIdsList();
    }

    @CircuitBreaker(name = "student-service")
    @Retry(name = "student-service")
    public Integer getStudentPersonId(String studentId) {
        var request = GetStudentPersonIdRequest.newBuilder()
                                               .setStudentId(studentId)
                                               .build();
        var response = studentServiceStub.getStudentPersonId(request);
        return response.getPersonId();
    }

}
