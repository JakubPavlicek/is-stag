package com.stag.identity.person.controller;

import com.stag.identity.api.PersonsApi;
import com.stag.identity.api.dto.AddressesResponse;
import com.stag.identity.api.dto.BankAccountsResponse;
import com.stag.identity.api.dto.EducationResponse;
import com.stag.identity.api.dto.PersonResponse;
import com.stag.identity.api.dto.UpdatePersonRequest;
import com.stag.identity.person.mapper.PersonApiMapper;
import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.model.Banking;
import com.stag.identity.person.model.Education;
import com.stag.identity.person.model.Profile;
import com.stag.identity.person.service.AddressService;
import com.stag.identity.person.service.BankingService;
import com.stag.identity.person.service.EducationService;
import com.stag.identity.person.service.ProfileService;
import com.stag.identity.person.service.dto.PersonUpdateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// **Person REST Controller**
///
/// REST API endpoints for person profile operations.
/// Provides operations for profiles, addresses, banking, and education information with localization support.
/// Implements OpenAPI-generated PersonsApi interface.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class PersonController implements PersonsApi {

    /// Profile Service
    private final ProfileService profileService;
    /// Address Service
    private final AddressService addressService;
    /// Banking Service
    private final BankingService bankingService;
    /// Education Service
    private final EducationService educationService;

    /// Retrieves a complete person profile with localized codelist data.
    ///
    /// @param personId the person identifier
    /// @param language the language code for localization (cs/en)
    /// @return person profile response with HTTP 200
    @Override
    public ResponseEntity<PersonResponse> getPersonProfile(Integer personId, String language) {
        log.info("Person profile requested for personId: {} with language: {}", personId, language);

        Profile profile = profileService.getPersonProfile(personId, language);
        PersonResponse personResponse = PersonApiMapper.INSTANCE.toPersonResponse(profile);

        return ResponseEntity.ok(personResponse);
    }

    /// Updates person profile information with validation.
    ///
    /// @param personId the person identifier
    /// @param updatePersonRequest the profile update request
    /// @return no content response with HTTP 204
    @Override
    public ResponseEntity<Void> updatePersonProfile(Integer personId, UpdatePersonRequest updatePersonRequest) {
        log.info("Person profile update requested for personId: {}", personId);

        PersonUpdateCommand command = PersonApiMapper.INSTANCE.toPersonUpdateCommand(updatePersonRequest);
        profileService.updatePersonProfile(personId, command);

        return ResponseEntity.noContent().build();
    }

    /// Retrieves person addresses with localized geographic data.
    ///
    /// @param personId the person identifier
    /// @param language the language code for localization (cs/en)
    /// @return addresses response with HTTP 200
    @Override
    public ResponseEntity<AddressesResponse> getPersonAddresses(Integer personId, String language) {
        log.info("Person addresses requested for personId: {} with language: {}", personId, language);

        Addresses addresses = addressService.getPersonAddresses(personId, language);
        AddressesResponse addressesResponse = PersonApiMapper.INSTANCE.toAddressesResponse(addresses);

        return ResponseEntity.ok(addressesResponse);
    }

    /// Retrieves person banking information with localized bank names.
    ///
    /// @param personId the person identifier
    /// @param language the language code for localization (cs/en)
    /// @return banking response with HTTP 200
    @Override
    public ResponseEntity<BankAccountsResponse> getPersonBanking(Integer personId, String language) {
        log.info("Person banking requested for personId: {} with language: {}", personId, language);

        Banking banking = bankingService.getPersonBanking(personId, language);
        BankAccountsResponse accountsResponse = PersonApiMapper.INSTANCE.toBankAccountsResponse(banking);

        return ResponseEntity.ok(accountsResponse);
    }

    /// Retrieves person education information with a localized field of study.
    ///
    /// @param personId the person identifier
    /// @param language the language code for localization (cs/en)
    /// @return education response with HTTP 200
    @Override
    public ResponseEntity<EducationResponse> getPersonEducation(Integer personId, String language) {
        log.info("Person education requested for personId: {} with language: {}", personId, language);

        Education education = educationService.getPersonEducation(personId, language);
        EducationResponse educationResponse = PersonApiMapper.INSTANCE.toEducationResponse(education);

        return ResponseEntity.ok(educationResponse);
    }

}
