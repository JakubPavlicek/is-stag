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
import org.springframework.stereotype.Service;

/// **Codelist gRPC Client**
///
/// gRPC client for codelist-service communication. Fetches localized codelist
/// meanings for person data enrichment with circuit breaker and retry patterns
/// for resilience. Optimizes calls by skipping requests with no meaningful data.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@Service
public class CodelistClient {

    /// Codelist Service stub
    private final CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    /// Constructor for CodelistClient
    ///
    /// @param codelistServiceStub the gRPC blocking stub for codelist service
    public CodelistClient(CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub) {
        this.codelistServiceStub = codelistServiceStub;
    }

    /// Fetches codelist meanings for simple profile enrichment.
    ///
    /// @param simpleProfile the simple profile projection
    /// @param language the language code for localization
    /// @return enriched codelist meanings data
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public CodelistMeaningsLookupData getSimpleProfileData(SimpleProfileView simpleProfile, String language) {
        var request = CodelistMapper.INSTANCE.toCodelistValuesRequest(simpleProfile, language);
        var response = codelistServiceStub.getCodelistValues(request);

        return CodelistMapper.INSTANCE.toCodelistMeaningsData(response);
    }

    /// Fetches codelist data for full person profile enrichment.
    /// Skips call if no meaningful data to fetch for optimization.
    ///
    /// @param personProfile the profile projection
    /// @param language the language code for localization
    /// @return enriched profile lookup data or null if skipped
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public ProfileLookupData getPersonProfileData(ProfileView personProfile, String language) {
        var request = CodelistMapper.INSTANCE.toPersonProfileDataRequest(personProfile, language);

        // Skip call if no meaningful data to fetch
        if (shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person profile data - no meaningful data to fetch");
            return null;
        }

        var response = codelistServiceStub.getPersonProfileData(request);
        return CodelistMapper.INSTANCE.toPersonProfileData(response);
    }

    /// Validates and resolves codelist values for profile update operations.
    /// Returns resolved low values and IDs for database persistence.
    ///
    /// @param maritalStatus the marital status meaning to validate
    /// @param birthCountryName the birth country name to resolve
    /// @param titles the titles to validate
    /// @return validated profile update data or empty object if skipped
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public ProfileUpdateLookupData getPersonProfileUpdateData(String maritalStatus, String birthCountryName, Profile.Titles titles) {
        var request = CodelistMapper.INSTANCE.toPersonProfileUpdateDataRequest(maritalStatus, birthCountryName, titles);

        // Skip call if no meaningful data to fetch
        if (request == null || shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person profile update data - no meaningful data to fetch");
            return new ProfileUpdateLookupData(null, null, null, null);
        }

        var response = codelistServiceStub.getPersonProfileUpdateData(request);
        return CodelistMapper.INSTANCE.toProfileUpdateLookupData(response);
    }

    /// Fetches codelist data for person addresses with localized location names.
    /// Skips call if no meaningful data to fetch for optimization.
    ///
    /// @param addressView the address projection
    /// @param language the language code for localization
    /// @return enriched address lookup data or null if skipped
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public AddressLookupData getPersonAddressData(AddressView addressView, String language) {
        var request = CodelistMapper.INSTANCE.toPersonAddressDataRequest(addressView, language);

        // Skip call if no meaningful data to fetch
        if (shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person address data - no meaningful data to fetch");
            return null;
        }

        var response = codelistServiceStub.getPersonAddressData(request);
        return CodelistMapper.INSTANCE.toPersonAddressData(response, addressView);
    }

    /// Fetches codelist data for person banking with localized bank names.
    /// Skips call if no meaningful data to fetch for optimization.
    ///
    /// @param bankView the banking projection
    /// @param language the language code for localization
    /// @return enriched banking lookup data or null if skipped
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public BankingLookupData getPersonBankingData(BankView bankView, String language) {
        var request = CodelistMapper.INSTANCE.toPersonBankingDataRequest(bankView, language);

        // Skip call if no meaningful data to fetch
        if (shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person banking data - no meaningful data to fetch");
            return null;
        }

        var response = codelistServiceStub.getPersonBankingData(request);
        return CodelistMapper.INSTANCE.toPersonBankingData(response);
    }

    /// Fetches codelist data for person education with school and field of study details.
    /// Skips call if no meaningful data to fetch for optimization.
    ///
    /// @param personEducation the education projection
    /// @param language the language code for localization
    /// @return enriched education lookup data or null if skipped
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public EducationLookupData getPersonEducationData(EducationView personEducation, String language) {
        var request = CodelistMapper.INSTANCE.toPersonEducationDataRequest(personEducation, language);

        // Skip call if no meaningful data to fetch
        if (shouldSkipRequest(request)) {
            log.debug("Skipping codelist-service call for person education data - no meaningful data to fetch");
            return null;
        }

        var response = codelistServiceStub.getPersonEducationData(request);
        return CodelistMapper.INSTANCE.toPersonEducationData(response);
    }

    /// Checks if a profile data request should be skipped based on empty fields.
    private boolean shouldSkipRequest(GetPersonProfileDataRequest request) {
        return !request.hasBirthCountryId()
            && !request.hasCitizenshipCountryId()
            && request.getCodelistKeysCount() == 0;
    }

    /// Checks if a profile update request should be skipped based on empty fields.
    private boolean shouldSkipRequest(GetPersonProfileUpdateDataRequest request) {
        return !request.hasMaritalStatus()
            && !request.hasTitlePrefix()
            && !request.hasTitleSuffix()
            && !request.hasBirthCountryName();
    }

    /// Checks if the address data request should be skipped based on empty fields.
    private boolean shouldSkipRequest(GetPersonAddressDataRequest request) {
        return !request.hasPermanentMunicipalityPartId()
            && !request.hasPermanentCountryId()
            && !request.hasTemporaryMunicipalityPartId()
            && !request.hasTemporaryCountryId();
    }

    /// Checks if banking data request should be skipped based on empty fields.
    private boolean shouldSkipRequest(GetPersonBankingDataRequest request) {
        return !request.hasEuroAccountCountryId()
            && request.getCodelistKeysCount() == 0;
    }

    /// Checks if education data request should be skipped based on empty fields.
    private boolean shouldSkipRequest(GetPersonEducationDataRequest request) {
        return !request.hasHighSchoolId()
            && !request.hasHighSchoolFieldOfStudyNumber()
            && !request.hasHighSchoolCountryId();
    }

}
