package com.stag.identity.person.controller;

import com.stag.identity.PersonsApi;
import com.stag.identity.dto.AddressesDTO;
import com.stag.identity.dto.BankAccountsDTO;
import com.stag.identity.dto.EducationDetailsDTO;
import com.stag.identity.dto.PersonProfileDTO;
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
    public ResponseEntity<PersonProfileDTO> getPersonProfile(Integer personId, String language) {
        log.info("Person profile requested for personId: {} with language: {}", personId, language);

        Profile profile = profileService.getPersonProfile(personId, language);
        PersonProfileDTO personProfileDTO = PersonApiMapper.INSTANCE.toPersonProfileDTO(profile);

        return ResponseEntity.ok(personProfileDTO);
    }

    @Override
    public ResponseEntity<AddressesDTO> getPersonAddresses(Integer personId, String language) {
        log.info("Person addresses requested for personId: {} with language: {}", personId, language);

        Addresses addresses = addressService.getPersonAddresses(personId, language);
        AddressesDTO addressesDTO = PersonApiMapper.INSTANCE.toAddressesDTO(addresses);

        return ResponseEntity.ok(addressesDTO);
    }

    @Override
    public ResponseEntity<BankAccountsDTO> getPersonBanking(Integer personId, String language) {
        log.info("Person banking requested for personId: {} with language: {}", personId, language);

        Banking banking = bankingService.getPersonBanking(personId, language);
        BankAccountsDTO bankAccountsDTO = PersonApiMapper.INSTANCE.toBankAccountsDTO(banking);

        return ResponseEntity.ok(bankAccountsDTO);
    }

    @Override
    public ResponseEntity<EducationDetailsDTO> getPersonEducation(Integer personId, String language) {
        log.info("Person education requested for personId: {} with language: {}", personId, language);

        Education education = educationService.getPersonEducation(personId, language);
        EducationDetailsDTO educationDetailsDTO = PersonApiMapper.INSTANCE.toEducationDetailsDTO(education);

        return ResponseEntity.ok(educationDetailsDTO);
    }

}
