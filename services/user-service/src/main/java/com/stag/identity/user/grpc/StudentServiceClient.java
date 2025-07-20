package com.stag.identity.user.grpc;

import com.stag.academics.student.v1.GetStudentPersonalNumbersRequest;
import com.stag.academics.student.v1.GetStudentPersonalNumbersResponse;
import com.stag.academics.student.v1.StudentServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceClient {

    private final StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub;

    public List<String> getStudentPersonalNumbers(Integer personId) {
        GetStudentPersonalNumbersRequest request = GetStudentPersonalNumbersRequest.newBuilder()
                                                                                   .setPersonId(personId)
                                                                                   .build();
        GetStudentPersonalNumbersResponse personalNumbers = studentServiceStub.getStudentPersonalNumbers(request);
        return personalNumbers.getPersonalNumbersList();
    }

}
