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

@Slf4j
@RequiredArgsConstructor
@Service
public class CodelistLookupService {

    private final CodelistClient codelistClient;

    @Async
    public CompletableFuture<ProfileLookupData> getPersonProfileData(ProfileView personProfile, String language) {
        log.info("Fetching person profile data");
        ProfileLookupData profileLookupData = codelistClient.getPersonProfileData(personProfile, language);
        log.debug("Completed fetching person profile data");

        return CompletableFuture.completedFuture(profileLookupData);
    }

    public ProfileUpdateLookupData getPersonProfileUpdateData(String maritalStatus, String birthCountryName, Profile.Titles titles) {
        log.info("Fetching person profile update data");
        ProfileUpdateLookupData profileUpdateLookupData = codelistClient.getPersonProfileUpdateData(maritalStatus, birthCountryName, titles);
        log.debug("Completed fetching person profile update data");

        return profileUpdateLookupData;
    }

    @Async
    public CompletableFuture<AddressLookupData> getPersonAddressData(AddressView personAddress, String language) {
        log.info("Fetching person address data");
        AddressLookupData addressLookupData = codelistClient.getPersonAddressData(personAddress, language);
        log.debug("Completed fetching person address data");

        return CompletableFuture.completedFuture(addressLookupData);
    }

    @Async
    public CompletableFuture<BankingLookupData> getPersonBankingData(BankView personBank, String language) {
        log.info("Fetching person banking data");
        BankingLookupData bankingLookupData = codelistClient.getPersonBankingData(personBank, language);
        log.debug("Completed fetching person banking data");

        return CompletableFuture.completedFuture(bankingLookupData);
    }

    @Async
    public CompletableFuture<EducationLookupData> getPersonEducationData(EducationView personEducation, String language) {
        log.info("Fetching person education data");
        EducationLookupData educationLookupData = codelistClient.getPersonEducationData(personEducation, language);
        log.debug("Completed fetching person education data");

        return CompletableFuture.completedFuture(educationLookupData);
    }

    public CodelistMeaningsLookupData getSimpleProfileData(SimpleProfileView simpleProfile, String language) {
        log.info("Fetching person simple profile data");
        CodelistMeaningsLookupData codelistMeaningsLookupData = codelistClient.getSimpleProfileData(simpleProfile, language);
        log.debug("Completed fetching person simple profile data");

        return codelistMeaningsLookupData;
    }

}
