package com.stag.academics.shared.grpc.client;

import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.identity.person.v1.GetPersonSimpleProfileRequest;
import com.stag.identity.person.v1.GetPersonSimpleProfileResponse;
import com.stag.identity.person.v1.PersonServiceGrpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private PersonServiceGrpc.PersonServiceBlockingStub personServiceStub;

    @InjectMocks
    private UserClient userClient;

    @Test
    @DisplayName("should return simple profile data when valid personId and language provided")
    void getPersonSimpleProfileData_ValidData_ReturnsProfileData() {
        int personId = 123;
        String language = "en";
        String firstName = "John";
        String lastName = "Doe";
        String gender = "M";

        GetPersonSimpleProfileResponse response = GetPersonSimpleProfileResponse.newBuilder()
                                                                                .setFirstName(firstName)
                                                                                .setLastName(lastName)
                                                                                .setGender(gender)
                                                                                .build();

        when(personServiceStub.getPersonSimpleProfile(any(GetPersonSimpleProfileRequest.class)))
            .thenReturn(response);

        SimpleProfileLookupData result = userClient.getPersonSimpleProfileData(personId, language);

        ArgumentCaptor<GetPersonSimpleProfileRequest> requestCaptor = ArgumentCaptor.forClass(GetPersonSimpleProfileRequest.class);
        verify(personServiceStub).getPersonSimpleProfile(requestCaptor.capture());

        GetPersonSimpleProfileRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getPersonId()).isEqualTo(personId);
        assertThat(capturedRequest.getLanguage()).isEqualTo(language);

        assertThat(result.firstName()).isEqualTo(firstName);
        assertThat(result.lastName()).isEqualTo(lastName);
        assertThat(result.gender()).isEqualTo(gender);
    }

}
