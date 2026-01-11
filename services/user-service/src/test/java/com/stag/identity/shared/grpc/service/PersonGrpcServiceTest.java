package com.stag.identity.shared.grpc.service;

import com.stag.identity.person.model.Profile;
import com.stag.identity.person.model.SimpleProfile;
import com.stag.identity.person.service.ProfileService;
import com.stag.identity.person.v1.GetPersonSimpleProfileRequest;
import com.stag.identity.person.v1.GetPersonSimpleProfileResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonGrpcServiceTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private StreamObserver<GetPersonSimpleProfileResponse> responseObserver;

    @InjectMocks
    private PersonGrpcService personGrpcService;

    @Test
    @DisplayName("getPersonSimpleProfile should return mapped profile and complete observer")
    void shouldReturnPersonSimpleProfile() {
        int personId = 123;
        String language = "cs";

        GetPersonSimpleProfileRequest request = GetPersonSimpleProfileRequest.newBuilder()
                                                                             .setPersonId(personId)
                                                                             .setLanguage(language)
                                                                             .build();

        SimpleProfile simpleProfile = SimpleProfile.builder()
                                                   .firstName("John")
                                                   .lastName("Doe")
                                                   .titles(new Profile.Titles("Ing.", "Ph.D."))
                                                   .gender("M")
                                                   .build();

        when(profileService.getPersonSimpleProfile(personId, language))
            .thenReturn(simpleProfile);

        personGrpcService.getPersonSimpleProfile(request, responseObserver);

        verify(profileService).getPersonSimpleProfile(personId, language);

        verify(responseObserver).onNext(any(GetPersonSimpleProfileResponse.class));
        verify(responseObserver).onCompleted();
    }

}
