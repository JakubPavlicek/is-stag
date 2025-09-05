package com.stag.academics.shared.grpc.service;

import com.stag.academics.student.service.StudentService;
import com.stag.academics.student.v1.GetStudentIdsRequest;
import com.stag.academics.student.v1.GetStudentIdsResponse;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.GetStudentPersonIdResponse;
import com.stag.academics.student.v1.StudentServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class StudentGrpcService extends StudentServiceGrpc.StudentServiceImplBase {

    private final StudentService studentService;

    @Override
    public void getStudentIds(
        GetStudentIdsRequest request,
        StreamObserver<GetStudentIdsResponse> responseObserver
    ) {
        List<String> studentIds = studentService.findAllStudentIds(request.getPersonId());

        var response = GetStudentIdsResponse.newBuilder()
                                            .addAllStudentIds(studentIds)
                                            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getStudentPersonId(
        GetStudentPersonIdRequest request,
        StreamObserver<GetStudentPersonIdResponse> responseObserver
    ) {
        Integer personId = studentService.findPersonId(request.getStudentId());

        var response = GetStudentPersonIdResponse.newBuilder()
                                                 .setPersonId(personId)
                                                 .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
