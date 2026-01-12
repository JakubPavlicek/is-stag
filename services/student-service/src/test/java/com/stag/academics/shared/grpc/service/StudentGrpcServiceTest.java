package com.stag.academics.shared.grpc.service;

import com.stag.academics.student.service.StudentService;
import com.stag.academics.student.v1.GetStudentIdsRequest;
import com.stag.academics.student.v1.GetStudentIdsResponse;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.GetStudentPersonIdResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentGrpcServiceTest {

    @Mock
    private StudentService studentService;

    @Mock
    private StreamObserver<GetStudentIdsResponse> idsResponseObserver;

    @Mock
    private StreamObserver<GetStudentPersonIdResponse> personIdResponseObserver;

    @InjectMocks
    private StudentGrpcService studentGrpcService;

    @Test
    @DisplayName("should return student IDs when valid person ID provided")
    void getStudentIds_ValidPersonId_ReturnsStudentIds() {
        int personId = 123;
        List<String> expectedIds = List.of("S123", "S456");
        GetStudentIdsRequest request = GetStudentIdsRequest.newBuilder()
                                                           .setPersonId(personId)
                                                           .build();

        when(studentService.findAllStudentIds(personId)).thenReturn(expectedIds);

        studentGrpcService.getStudentIds(request, idsResponseObserver);

        verify(studentService).findAllStudentIds(personId);

        ArgumentCaptor<GetStudentIdsResponse> responseCaptor = ArgumentCaptor.forClass(GetStudentIdsResponse.class);
        verify(idsResponseObserver).onNext(responseCaptor.capture());
        GetStudentIdsResponse response = responseCaptor.getValue();
        assertThat(response.getStudentIdsList()).containsExactlyElementsOf(expectedIds);

        verify(idsResponseObserver).onCompleted();
    }

    @Test
    @DisplayName("should return person ID when valid student ID provided")
    void getStudentPersonId_ValidStudentId_ReturnsPersonId() {
        String studentId = "S123";
        int expectedPersonId = 123;
        GetStudentPersonIdRequest request = GetStudentPersonIdRequest.newBuilder()
                                                                     .setStudentId(studentId)
                                                                     .build();

        when(studentService.findPersonId(studentId)).thenReturn(expectedPersonId);

        studentGrpcService.getStudentPersonId(request, personIdResponseObserver);

        verify(studentService).findPersonId(studentId);

        ArgumentCaptor<GetStudentPersonIdResponse> responseCaptor = ArgumentCaptor.forClass(GetStudentPersonIdResponse.class);
        verify(personIdResponseObserver).onNext(responseCaptor.capture());
        GetStudentPersonIdResponse response = responseCaptor.getValue();
        assertThat(response.getPersonId()).isEqualTo(expectedPersonId);

        verify(personIdResponseObserver).onCompleted();
    }

}
