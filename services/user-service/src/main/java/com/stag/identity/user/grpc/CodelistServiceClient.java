package com.stag.identity.user.grpc;

import com.stag.identity.user.grpc.mapper.CodelistMapper;
import com.stag.identity.user.repository.projection.AddressProjection;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonProfileData;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodelistServiceClient {

    @GrpcClient("codelist-service")
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    private final CodelistMapper mapper;

    /// Get codelist data specifically for person profile (GET /persons/{personId})
    public PersonProfileData getPersonProfileData(PersonProfileProjection personProfile) {
        var request = mapper.toPersonProfileDataRequest(personProfile);
        var response = codelistServiceStub.getPersonProfileData(request);
        return mapper.toPersonProfileData(response);
    }

    /// Get codelist data specifically for person addresses (GET /persons/{personId}/addresses)
    public PersonAddressData getPersonAddressData(AddressProjection permanentAddress, AddressProjection temporaryAddress) {
        var request = mapper.toPersonAddressDataRequest(permanentAddress, temporaryAddress);
        var response = codelistServiceStub.getPersonAddressData(request);
        return mapper.toPersonAddressData(response, permanentAddress, temporaryAddress);
    }
}
