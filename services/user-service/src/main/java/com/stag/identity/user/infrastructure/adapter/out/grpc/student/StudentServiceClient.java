package com.stag.identity.user.infrastructure.adapter.out.grpc.student;

import com.stag.academics.student.v1.GetStudentPersonalNumbersRequest;
import com.stag.academics.student.v1.StudentServiceGrpc;
import com.stag.identity.user.application.person.port.out.StudentServicePort;
import com.stag.identity.user.domain.person.model.PersonId;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceClient implements StudentServicePort {

    @GrpcClient("student-service")
    private StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub;

    @Override
    public List<String> getStudentPersonalNumbers(PersonId personId) {
        var request = GetStudentPersonalNumbersRequest.newBuilder()
                                                      .setPersonId(personId.id())
                                                      .build();
        var response = studentServiceStub.getStudentPersonalNumbers(request);
        return response.getPersonalNumbersList();
    }

}
