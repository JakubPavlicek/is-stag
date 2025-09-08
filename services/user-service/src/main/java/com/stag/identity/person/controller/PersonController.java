package com.stag.identity.person.controller;

import com.stag.identity.api.PersonsApi;
import com.stag.identity.api.dto.AddressesResponse;
import com.stag.identity.api.dto.BankAccountsResponse;
import com.stag.identity.api.dto.EducationResponse;
import com.stag.identity.api.dto.PersonResponse;
import com.stag.identity.person.mapper.PersonApiMapper;
import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.model.Banking;
import com.stag.identity.person.model.Education;
import com.stag.identity.person.model.Profile;
import com.stag.identity.person.service.AddressService;
import com.stag.identity.person.service.BankingService;
import com.stag.identity.person.service.EducationService;
import com.stag.identity.person.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class PersonController implements PersonsApi {

    private final ProfileService profileService;
    private final AddressService addressService;
    private final BankingService bankingService;
    private final EducationService educationService;

    @Override
    public ResponseEntity<PersonResponse> getPersonProfile(Integer personId, String language) {
        log.info("Person profile requested for personId: {} with language: {}", personId, language);

        Profile profile = profileService.getPersonProfile(personId, language);
        PersonResponse personResponse = PersonApiMapper.INSTANCE.toPersonResponse(profile);

        return ResponseEntity.ok(personResponse);
    }

    @Override
    public ResponseEntity<AddressesResponse> getPersonAddresses(Integer personId, String language) {
        log.info("Person addresses requested for personId: {} with language: {}", personId, language);

        Addresses addresses = addressService.getPersonAddresses(personId, language);
        AddressesResponse addressesResponse = PersonApiMapper.INSTANCE.toAddressesResponse(addresses);

        return ResponseEntity.ok(addressesResponse);
    }

    @Override
    public ResponseEntity<BankAccountsResponse> getPersonBanking(Integer personId, String language) {
        log.info("Person banking requested for personId: {} with language: {}", personId, language);

        Banking banking = bankingService.getPersonBanking(personId, language);
        BankAccountsResponse accountsResponse = PersonApiMapper.INSTANCE.toBankAccountsResponse(banking);

        return ResponseEntity.ok(accountsResponse);
    }

    @Override
    public ResponseEntity<EducationResponse> getPersonEducation(Integer personId, String language) {
        log.info("Person education requested for personId: {} with language: {}", personId, language);

        Education education = educationService.getPersonEducation(personId, language);
        EducationResponse educationResponse = PersonApiMapper.INSTANCE.toEducationResponse(education);

        return ResponseEntity.ok(educationResponse);
    }

}
