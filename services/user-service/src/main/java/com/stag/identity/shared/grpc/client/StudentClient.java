package com.stag.identity.shared.grpc.client;

import com.stag.academics.student.v1.GetStudentIdsRequest;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.StudentServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StudentClient {

    @GrpcClient("student-service")
    private StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub;

    private StudentServiceGrpc.StudentServiceBlockingStub studentStub() {
        return studentServiceStub.withDeadlineAfter(1, TimeUnit.SECONDS);
    }

    @CircuitBreaker(name = "student-service")
    @Retry(name = "student-service")
    public List<String> getStudentIds(Integer personId) {
        var request = GetStudentIdsRequest.newBuilder()
                                          .setPersonId(personId)
                                          .build();
        var response = studentStub().getStudentIds(request);
        return response.getStudentIdsList();
    }

    @CircuitBreaker(name = "student-service")
    @Retry(name = "student-service")
    public Integer getStudentPersonId(String studentId) {
        var request = GetStudentPersonIdRequest.newBuilder()
                                               .setStudentId(studentId)
                                               .build();
        var response = studentStub().getStudentPersonId(request);
        return response.getPersonId();
    }

}
