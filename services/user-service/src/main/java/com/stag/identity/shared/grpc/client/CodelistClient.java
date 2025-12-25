package com.stag.identity.shared.grpc.client;

import com.stag.identity.person.model.Profile;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.repository.projection.EducationView;
import com.stag.identity.person.repository.projection.ProfileView;
import com.stag.identity.person.repository.projection.SimpleProfileView;
import com.stag.identity.person.service.data.AddressLookupData;
import com.stag.identity.person.service.data.BankingLookupData;
import com.stag.identity.person.service.data.CodelistMeaningsLookupData;
import com.stag.identity.person.service.data.EducationLookupData;
import com.stag.identity.person.service.data.ProfileLookupData;
import com.stag.identity.person.service.data.ProfileUpdateLookupData;
import com.stag.identity.shared.grpc.mapper.CodelistMapper;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.GetPersonAddressDataRequest;
import com.stag.platform.codelist.v1.GetPersonBankingDataRequest;
import com.stag.platform.codelist.v1.GetPersonEducationDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileUpdateDataRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CodelistClient {

    @GrpcClient("codelist-service")
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistStub() {
        return codelistServiceStub.withDeadlineAfter(1, TimeUnit.SECONDS);
    }

    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public CodelistMeaningsLookupData getSimpleProfileData(SimpleProfileView simpleProfile, String language) {
        var request = CodelistMapper.INSTANCE.toCodelistValuesRequest(simpleProfile, language);
        var response = codelistStub().getCodelistValues(request);

        return CodelistMapper.INSTANCE.toCodelistMeaningsData(response);
    }

    /// Get codelist data specifically for person profile (GET /persons/{personId})
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public ProfileLookupData getPersonProfileData(ProfileView personProfile, String language) {
        var request = CodelistMapper.INSTANCE.toPersonProfileDataRequest(personProfile, language);

        // Skip call if no meaningful data to fetch
        if (shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person profile data - no meaningful data to fetch");
            return null;
        }

        var response = codelistStub().getPersonProfileData(request);
        return CodelistMapper.INSTANCE.toPersonProfileData(response);
    }

    /// Get codelist data specifically for person profile update (PATCH /persons/{personId})
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public ProfileUpdateLookupData getPersonProfileUpdateData(String maritalStatus, String birthCountryName, Profile.Titles titles) {
        var request = CodelistMapper.INSTANCE.toPersonProfileUpdateDataRequest(maritalStatus, birthCountryName, titles);

        // Skip call if no meaningful data to fetch (can be null if )
        if (request == null || shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person profile update data - no meaningful data to fetch");
            return null;
        }

        var response = codelistStub().getPersonProfileUpdateData(request);
        return CodelistMapper.INSTANCE.toProfileUpdateLookupData(response);
    }

    /// Get codelist data specifically for person addresses (GET /persons/{personId}/addresses)
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public AddressLookupData getPersonAddressData(AddressView addressView, String language) {
        var request = CodelistMapper.INSTANCE.toPersonAddressDataRequest(addressView, language);

        // Skip call if no meaningful data to fetch
        if (shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person address data - no meaningful data to fetch");
            return null;
        }

        var response = codelistStub().getPersonAddressData(request);
        return CodelistMapper.INSTANCE.toPersonAddressData(response, addressView);
    }

    /// Get codelist data specifically for person banking (GET /persons/{personId}/banking)
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public BankingLookupData getPersonBankingData(BankView bankView, String language) {
        var request = CodelistMapper.INSTANCE.toPersonBankingDataRequest(bankView, language);

        // Skip call if no meaningful data to fetch
        if (shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person banking data - no meaningful data to fetch");
            return null;
        }

        var response = codelistStub().getPersonBankingData(request);
        return CodelistMapper.INSTANCE.toPersonBankingData(response);
    }

    /// Get codelist data specifically for person education (GET /persons/{personId}/education)
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public EducationLookupData getPersonEducationData(EducationView personEducation, String language) {
        var request = CodelistMapper.INSTANCE.toPersonEducationDataRequest(personEducation, language);

        // Skip call if no meaningful data to fetch
        if (shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person education data - no meaningful data to fetch");
            return null;
        }

        var response = codelistStub().getPersonEducationData(request);
        return CodelistMapper.INSTANCE.toPersonEducationData(response);
    }

    private boolean shouldSkipRequest(GetPersonProfileDataRequest request) {
        return !request.hasBirthCountryId()
            && !request.hasCitizenshipCountryId()
            && request.getCodelistKeysCount() == 0;
    }

    private boolean shouldSkipRequest(GetPersonProfileUpdateDataRequest request) {
        return !request.hasMaritalStatus()
            && !request.hasTitlePrefix()
            && !request.hasTitleSuffix()
            && !request.hasBirthCountryName();
    }

    private boolean shouldSkipRequest(GetPersonAddressDataRequest request) {
        return !request.hasPermanentMunicipalityPartId()
            && !request.hasPermanentCountryId()
            && !request.hasTemporaryMunicipalityPartId()
            && !request.hasTemporaryCountryId();
    }

    private boolean shouldSkipRequest(GetPersonBankingDataRequest request) {
        return !request.hasEuroAccountCountryId()
            && request.getCodelistKeysCount() == 0;
    }

    private boolean shouldSkipRequest(GetPersonEducationDataRequest request) {
        return !request.hasHighSchoolId()
            && !request.hasHighSchoolFieldOfStudyNumber()
            && !request.hasHighSchoolCountryId();
    }

}
