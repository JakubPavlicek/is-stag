package com.stag.identity.user.grpc;

import com.stag.identity.user.grpc.mapper.CodelistMapper;
import com.stag.identity.user.repository.projection.PersonAddressProjection;
import com.stag.identity.user.repository.projection.PersonBankProjection;
import com.stag.identity.user.repository.projection.PersonEducationProjection;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonBankingData;
import com.stag.identity.user.service.data.PersonEducationData;
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
    public PersonProfileData getPersonProfileData(PersonProfileProjection personProfile, String language) {
        var request = codelistMapper.toPersonProfileDataRequest(personProfile, language);
        var response = codelistServiceStub.getPersonProfileData(request);
        return codelistMapper.toPersonProfileData(response);
    }

    /// Get codelist data specifically for person addresses (GET /persons/{personId}/addresses)
    public PersonAddressData getPersonAddressData(PersonAddressProjection personAddressProjection, String language) {
        var request = codelistMapper.toPersonAddressDataRequest(personAddressProjection, language);
        var response = codelistServiceStub.getPersonAddressData(request);
        return codelistMapper.toPersonAddressData(response, personAddressProjection);
    }

    /// Get codelist data specifically for person banking (GET /persons/{personId}/banking)
    public PersonBankingData getPersonBankingData(PersonBankProjection personBankProjection, String language) {
        var request = codelistMapper.toPersonBankingDataRequest(personBankProjection, language);
        var response = codelistServiceStub.getPersonBankingData(request);
        return codelistMapper.toPersonBankingData(response);
    }

    /// Get codelist data specifically for person education (GET /persons/{personId}/education)
    public PersonEducationData getPersonEducationData(PersonEducationProjection personEducation, String language) {
        var request = codelistMapper.toPersonEducationDataRequest(personEducation, language);
        var response = codelistServiceStub.getPersonEducationData(request);
        return codelistMapper.toPersonEducationData(response);
    }

}
