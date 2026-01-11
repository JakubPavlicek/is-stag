package com.stag.identity.person.service;

import com.stag.identity.shared.grpc.client.StudentClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private StudentClient studentClient;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    @DisplayName("Should return false when user is not a student")
    void isStudentAndOwner_NotStudent_ReturnsFalse() {
        boolean isStudent = false;
        String studentId = "ST123";
        Integer personId = 456;

        boolean result = authorizationService.isStudentAndOwner(isStudent, studentId, personId);

        assertThat(result).isFalse();
        verifyNoInteractions(studentClient);
    }

    @ParameterizedTest(name = "Should return {3} when studentPersonId={1} and requested personId={2}")
    @CsvSource(
        {
            "ST123, 456, 456, true",
            "ST123, 456, 789, false"
        }
    )
    @DisplayName("Should return correct ownership status for student")
    void isStudentAndOwner_Student_ReturnsOwnershipStatus(
        String studentId, Integer studentPersonId, Integer requestedPersonId, boolean expectedResult
    ) {
        boolean isStudent = true;
        when(studentClient.getStudentPersonId(studentId)).thenReturn(studentPersonId);

        boolean result = authorizationService.isStudentAndOwner(isStudent, studentId, requestedPersonId);

        assertThat(result).isEqualTo(expectedResult);
        verify(studentClient).getStudentPersonId(studentId);
    }

    @Test
    @DisplayName("Should propagate exception when student client fails")
    void isStudentAndOwner_ClientThrowsException_PropagatesException() {
        boolean isStudent = true;
        String studentId = "ST123";
        Integer personId = 456;

        when(studentClient.getStudentPersonId(studentId)).thenThrow(new RuntimeException("gRPC error"));

        assertThatThrownBy(() -> authorizationService.isStudentAndOwner(isStudent, studentId, personId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("gRPC error");

        verify(studentClient).getStudentPersonId(studentId);
    }

}
