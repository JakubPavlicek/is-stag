package com.stag.identity.user.grpc;

import com.stag.academics.student.v1.GetStudentPersonalNumbersRequest;
import com.stag.academics.student.v1.StudentServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceClient {

    @GrpcClient("student-service")
    private StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub;

    public List<String> getStudentPersonalNumbers(Integer personId) {
        var request = GetStudentPersonalNumbersRequest.newBuilder()
                                                      .setPersonId(personId)
                                                      .build();
        var response = studentServiceStub.getStudentPersonalNumbers(request);
        return response.getPersonalNumbersList();
    }

}
