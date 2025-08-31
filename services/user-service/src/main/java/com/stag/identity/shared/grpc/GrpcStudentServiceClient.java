package com.stag.identity.shared.grpc;

import com.stag.academics.student.v1.GetStudentIdsRequest;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
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

    public List<String> getStudentIds(Integer personId) {
        var request = GetStudentIdsRequest.newBuilder()
                                          .setPersonId(personId)
                                          .build();
        var response = studentServiceStub.getStudentIds(request);
        return response.getStudentIdsList();
    }

    public Integer getStudentPersonId(String studentId) {
        var request = GetStudentPersonIdRequest.newBuilder()
                                               .setStudentId(studentId)
                                               .build();
        var response = studentServiceStub.getStudentPersonId(request);
        return response.getPersonId();
    }

}
