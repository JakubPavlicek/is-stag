package com.stag.identity.user.grpc;

import com.stag.identity.user.grpc.mapper.CodelistMapper;
import com.stag.identity.user.model.Addresses.Address;
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

    private final CodelistMapper codelistMapper;

    /// Get codelist data specifically for person profile (GET /persons/{personId})
    public PersonProfileData getPersonProfileData(PersonProfileProjection personProfile) {
        var request = codelistMapper.toPersonProfileDataRequest(personProfile);
        var response = codelistServiceStub.getPersonProfileData(request);
        return codelistMapper.toPersonProfileData(response);
    }

    /// Get codelist data specifically for person addresses (GET /persons/{personId}/addresses)
    public PersonAddressData getPersonAddressData(Address permanentAddress, Address temporaryAddress) {
        // TODO: the addresses might be null, handle this case
        var request = codelistMapper.toPersonAddressDataRequest(permanentAddress, temporaryAddress);
        var response = codelistServiceStub.getPersonAddressData(request);
        return codelistMapper.toPersonAddressData(response, permanentAddress, temporaryAddress);
    }

}
