package com.stag.identity.shared.grpc.client;

import com.stag.academics.student.v1.GetStudentIdsRequest;
import com.stag.academics.student.v1.GetStudentIdsResponse;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.GetStudentPersonIdResponse;
import com.stag.academics.student.v1.StudentServiceGrpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentClientTest {

    @Mock
    private StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub;

    @InjectMocks
    private StudentClient studentClient;

    @Test
    @DisplayName("getStudentIds should return list of student IDs from gRPC response")
    void getStudentIds_ValidPersonId_ReturnsStudentIdsList() {
        Integer personId = 123;
        List<String> studentIds = List.of("S001", "S002");
        GetStudentIdsResponse response = GetStudentIdsResponse.newBuilder()
                                                              .addAllStudentIds(studentIds)
                                                              .build();

        when(studentServiceStub.getStudentIds(any(GetStudentIdsRequest.class))).thenReturn(response);

        List<String> result = studentClient.getStudentIds(personId);

        assertThat(result).containsExactly("S001", "S002");
        verify(studentServiceStub).getStudentIds(any(GetStudentIdsRequest.class));
    }

    @Test
    @DisplayName("getStudentPersonId should return person ID from gRPC response")
    void getStudentPersonId_ValidStudentId_ReturnsPersonId() {
        String studentId = "S001";
        Integer personId = 123;
        GetStudentPersonIdResponse response = GetStudentPersonIdResponse.newBuilder()
                                                                        .setPersonId(personId)
                                                                        .build();

        when(studentServiceStub.getStudentPersonId(any(GetStudentPersonIdRequest.class))).thenReturn(response);

        Integer result = studentClient.getStudentPersonId(studentId);

        assertThat(result).isEqualTo(personId);
        verify(studentServiceStub).getStudentPersonId(any(GetStudentPersonIdRequest.class));
    }

}
