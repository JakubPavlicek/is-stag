package com.stag.identity.person.service;

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
import com.stag.identity.shared.grpc.client.CodelistClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/// **Codelist Lookup Service**
///
/// Asynchronous service for enriching person data with localized codelist meanings.
/// Fetches country names, bank names, education fields, and other codelist values
/// from the codelist-service via gRPC. Most operations are async for performance.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class CodelistLookupService {

    /// Codelist Client
    private final CodelistClient codelistClient;

    /// Fetches localized codelist data for a full person profile.
    /// Retrieves meanings for nationality, citizenship, marital status, titles, etc.
    ///
    /// @param personProfile the profile projection with codelist value codes
    /// @param language the language code for localization
    /// @return future containing enriched profile data
    @Async
    public CompletableFuture<ProfileLookupData> getPersonProfileData(ProfileView personProfile, String language) {
        log.info("Fetching person profile data");
        ProfileLookupData profileLookupData = codelistClient.getPersonProfileData(personProfile, language);
        log.debug("Completed fetching person profile data");

        return CompletableFuture.completedFuture(profileLookupData);
    }

    /// Validates and resolves codelist values for profile update operations.
    /// Synchronous validation of marital status, birth country, and titles before update.
    ///
    /// @param maritalStatus the marital status meaning to validate
    /// @param birthCountryName the birth country name to resolve
    /// @param titles the title prefix/suffix to validate
    /// @return validated lookup data with resolved IDs and low values
    public ProfileUpdateLookupData getPersonProfileUpdateData(String maritalStatus, String birthCountryName, Profile.Titles titles) {
        log.info("Fetching person profile update data");
        ProfileUpdateLookupData profileUpdateLookupData = codelistClient.getPersonProfileUpdateData(maritalStatus, birthCountryName, titles);
        log.debug("Completed fetching person profile update data");

        return profileUpdateLookupData;
    }

    /// Fetches localized codelist data for person addresses.
    /// Retrieves country and state names for permanent, temporary, and foreign addresses.
    ///
    /// @param personAddress the address projection with country/state IDs
    /// @param language the language code for localization
    /// @return future containing enriched address data
    @Async
    public CompletableFuture<AddressLookupData> getPersonAddressData(AddressView personAddress, String language) {
        log.info("Fetching person address data");
        AddressLookupData addressLookupData = codelistClient.getPersonAddressData(personAddress, language);
        log.debug("Completed fetching person address data");

        return CompletableFuture.completedFuture(addressLookupData);
    }

    /// Fetches localized codelist data for person banking information.
    /// Retrieves bank names and account type meanings.
    ///
    /// @param personBank the banking projection with bank codes
    /// @param language the language code for localization
    /// @return future containing enriched banking data
    @Async
    public CompletableFuture<BankingLookupData> getPersonBankingData(BankView personBank, String language) {
        log.info("Fetching person banking data");
        BankingLookupData bankingLookupData = codelistClient.getPersonBankingData(personBank, language);
        log.debug("Completed fetching person banking data");

        return CompletableFuture.completedFuture(bankingLookupData);
    }

    /// Fetches localized codelist data for person education information.
    /// Retrieves field of study names and education level meanings.
    ///
    /// @param personEducation the education projection with field codes
    /// @param language the language code for localization
    /// @return future containing enriched education data
    @Async
    public CompletableFuture<EducationLookupData> getPersonEducationData(EducationView personEducation, String language) {
        log.info("Fetching person education data");
        EducationLookupData educationLookupData = codelistClient.getPersonEducationData(personEducation, language);
        log.debug("Completed fetching person education data");

        return CompletableFuture.completedFuture(educationLookupData);
    }

    /// Fetches localized codelist data for a simple person profile.
    /// Lighter version retrieving only essential codelist meanings for basic profile.
    ///
    /// @param simpleProfile the simple profile projection
    /// @param language the language code for localization
    /// @return enriched simple profile data
    public CodelistMeaningsLookupData getSimpleProfileData(SimpleProfileView simpleProfile, String language) {
        log.info("Fetching person simple profile data");
        CodelistMeaningsLookupData codelistMeaningsLookupData = codelistClient.getSimpleProfileData(simpleProfile, language);
        log.debug("Completed fetching person simple profile data");

        return codelistMeaningsLookupData;
    }

}
