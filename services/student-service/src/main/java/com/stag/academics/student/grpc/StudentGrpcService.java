package com.stag.academics.student.grpc;

import com.stag.academics.student.service.StudentService;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.GetStudentPersonIdResponse;
import com.stag.academics.student.v1.GetStudentPersonalNumbersRequest;
import com.stag.academics.student.v1.GetStudentPersonalNumbersResponse;
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
    public void getStudentPersonalNumbers(
        GetStudentPersonalNumbersRequest request,
        StreamObserver<GetStudentPersonalNumbersResponse> responseObserver
    ) {
        List<String> personalNumbers = studentService.findAllPersonalNumbers(request.getPersonId());

        var response = GetStudentPersonalNumbersResponse.newBuilder()
                                                        .addAllPersonalNumbers(personalNumbers)
                                                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getStudentPersonId(
        GetStudentPersonIdRequest request,
        StreamObserver<GetStudentPersonIdResponse> responseObserver
    ) {
        Integer personId = studentService.findPersonIdByPersonalNumber(request.getPersonalNumber());

        var response = GetStudentPersonIdResponse.newBuilder()
                                                 .setPersonId(personId)
                                                 .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
