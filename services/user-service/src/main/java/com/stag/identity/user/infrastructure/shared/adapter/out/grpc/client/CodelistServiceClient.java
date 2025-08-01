package com.stag.identity.user.infrastructure.shared.adapter.out.grpc.client;

import com.stag.identity.user.application.person.dto.PersonAddressData;
import com.stag.identity.user.application.person.dto.PersonBankingData;
import com.stag.identity.user.application.person.dto.PersonEducationData;
import com.stag.identity.user.application.person.dto.PersonProfileData;
import com.stag.identity.user.application.person.port.out.CodelistServicePort;
import com.stag.identity.user.domain.person.model.PersonAddress;
import com.stag.identity.user.domain.person.model.PersonBank;
import com.stag.identity.user.domain.person.model.PersonEducation;
import com.stag.identity.user.domain.person.model.PersonProfile;
import com.stag.identity.user.infrastructure.shared.adapter.out.grpc.mapper.CodelistMapper;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodelistServiceClient implements CodelistServicePort {

    @GrpcClient("codelist-service")
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    private final CodelistMapper codelistMapper;

    /// Get codelist data specifically for person profile (GET /persons/{personId})
    @Override
    public PersonProfileData getPersonProfileData(PersonProfile personProfile) {
        var request = codelistMapper.toPersonProfileDataRequest(personProfile);
        var response = codelistServiceStub.getPersonProfileData(request);
        return codelistMapper.toPersonProfileData(response);
    }

    /// Get codelist data specifically for person addresses (GET /persons/{personId}/addresses)
    @Override
    public PersonAddressData getPersonAddressData(PersonAddress personAddress) {
        var request = codelistMapper.toPersonAddressDataRequest(personAddress);
        var response = codelistServiceStub.getPersonAddressData(request);
        return codelistMapper.toPersonAddressData(response, personAddress);
    }

    /// Get codelist data specifically for person banking (GET /persons/{personId}/banking)
    @Override
    public PersonBankingData getPersonBankingData(PersonBank personBank) {
        var request = codelistMapper.toPersonBankingDataRequest(personBank);
        var response = codelistServiceStub.getPersonBankingData(request);
        return codelistMapper.toPersonBankingData(response);
    }

    /// Get codelist data specifically for person education (GET /persons/{personId}/education)
    @Override
    public PersonEducationData getPersonEducationData(PersonEducation personEducation) {
        var request = codelistMapper.toPersonEducationDataRequest(personEducation);
        var response = codelistServiceStub.getPersonEducationData(request);
        return codelistMapper.toPersonEducationData(response);
    }

}
