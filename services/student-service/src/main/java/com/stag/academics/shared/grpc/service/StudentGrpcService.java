package com.stag.academics.shared.grpc.service;

import com.stag.academics.student.service.StudentService;
import com.stag.academics.student.v1.GetStudentIdsRequest;
import com.stag.academics.student.v1.GetStudentIdsResponse;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.GetStudentPersonIdResponse;
import com.stag.academics.student.v1.StudentServiceGrpc;
import grpcstarter.server.GrpcService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/// **Student gRPC Service**
///
/// gRPC service implementation for student operations. Exposes methods for
/// retrieving student IDs and person mappings via gRPC protocol.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@GrpcService
public class StudentGrpcService extends StudentServiceGrpc.StudentServiceImplBase {

    /// Student service
    private final StudentService studentService;

    /// Retrieves all student IDs associated with a person via gRPC.
    ///
    /// @param request the gRPC request containing person ID
    /// @param responseObserver the response stream observer
    @Override
    public void getStudentIds(
        GetStudentIdsRequest request,
        StreamObserver<GetStudentIdsResponse> responseObserver
    ) {
        // Fetch student IDs from service
        List<String> studentIds = studentService.findAllStudentIds(request.getPersonId());

        // Build and send gRPC response
        var response = GetStudentIdsResponse.newBuilder()
                                            .addAllStudentIds(studentIds)
                                            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /// Retrieves person ID for a given student via gRPC.
    ///
    /// @param request the gRPC request containing student ID
    /// @param responseObserver the response stream observer
    @Override
    public void getStudentPersonId(
        GetStudentPersonIdRequest request,
        StreamObserver<GetStudentPersonIdResponse> responseObserver
    ) {
        // Fetch person ID from service
        Integer personId = studentService.findPersonId(request.getStudentId());

        // Build and send gRPC response
        var response = GetStudentPersonIdResponse.newBuilder()
                                                 .setPersonId(personId)
                                                 .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
