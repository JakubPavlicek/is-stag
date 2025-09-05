package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.PersonMapper;
import com.stag.academics.student.repository.projection.ProfileView;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.identity.person.v1.PersonServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserClient {

    @GrpcClient("user-service")
    private PersonServiceGrpc.PersonServiceBlockingStub personServiceStub;

    /// Get person simple profile data specifically for student profile (GET /students/{studentId})
    public SimpleProfileLookupData getPersonSimpleProfileData(Integer personId, String language) {
        var request = PersonMapper.INSTANCE.toSimpleProfileDataRequest(personId, language);
        var response = personServiceStub.getPersonSimpleProfile(request);
        return PersonMapper.INSTANCE.toSimpleProfileData(response);
    }

}
