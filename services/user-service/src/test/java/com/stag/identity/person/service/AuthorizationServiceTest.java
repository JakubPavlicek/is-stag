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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private StudentClient studentClient;

    @InjectMocks
    private AuthorizationService authorizationService;

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
        when(studentClient.getStudentPersonId(studentId)).thenReturn(studentPersonId);

        boolean result = authorizationService.isStudentOwner(studentId, requestedPersonId);

        assertThat(result).isEqualTo(expectedResult);
        verify(studentClient).getStudentPersonId(studentId);
    }

    @Test
    @DisplayName("Should propagate exception when student client fails")
    void isStudentOwner_ClientThrowsException_PropagatesException() {
        String studentId = "ST123";
        Integer personId = 456;

        when(studentClient.getStudentPersonId(studentId)).thenThrow(new RuntimeException("gRPC error"));

        assertThatThrownBy(() -> authorizationService.isStudentOwner(studentId, personId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("gRPC error");

        verify(studentClient).getStudentPersonId(studentId);
    }

}
