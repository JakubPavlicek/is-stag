package com.stag.identity.shared.grpc;

import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.GetStudentPersonalNumbersRequest;
import com.stag.academics.student.v1.StudentServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GrpcStudentServiceClient {

    @GrpcClient("student-service")
    private StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub;

    public List<String> getStudentPersonalNumbers(Integer personId) {
        var request = GetStudentPersonalNumbersRequest.newBuilder()
                                                      .setPersonId(personId)
                                                      .build();
        var response = studentServiceStub.getStudentPersonalNumbers(request);
        return response.getPersonalNumbersList();
    }

    public Integer getStudentPersonId(String personalNumber) {
        var request = GetStudentPersonIdRequest.newBuilder()
                                               .setPersonalNumber(personalNumber)
                                               .build();
        var response = studentServiceStub.getStudentPersonId(request);
        return response.getPersonId();
    }

}
