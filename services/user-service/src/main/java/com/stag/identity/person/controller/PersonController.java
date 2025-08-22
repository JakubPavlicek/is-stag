package com.stag.identity.person.controller;

import com.stag.identity.PersonsApi;
import com.stag.identity.dto.AddressesDTO;
import com.stag.identity.dto.BankAccountsDTO;
import com.stag.identity.dto.EducationDetailsDTO;
import com.stag.identity.dto.PersonProfileDTO;
import com.stag.identity.person.mapper.PersonDtoMapper;
import com.stag.identity.person.model.PersonAddresses;
import com.stag.identity.person.model.PersonBanking;
import com.stag.identity.person.model.PersonEducation;
import com.stag.identity.person.model.PersonProfile;
import com.stag.identity.person.service.PersonService;
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

    private final PersonService personService;

    @Override
    public ResponseEntity<PersonProfileDTO> getPersonProfile(Integer personId, String language) {
        log.info("Person profile requested for personId: {} with language: {}", personId, language);

        PersonProfile personProfile = personService.getPersonProfile(personId, language);
        PersonProfileDTO personProfileDTO = PersonDtoMapper.INSTANCE.toPersonProfileDTO(personProfile);

        return ResponseEntity.ok(personProfileDTO);
    }

    @Override
    public ResponseEntity<AddressesDTO> getPersonAddresses(Integer personId, String language) {
        log.info("Person addresses requested for personId: {} with language: {}", personId, language);

        PersonAddresses personAddresses = personService.getPersonAddresses(personId, language);
        AddressesDTO addressesDTO = PersonDtoMapper.INSTANCE.toAddressesDTO(personAddresses);

        return ResponseEntity.ok(addressesDTO);
    }

    @Override
    public ResponseEntity<BankAccountsDTO> getPersonBanking(Integer personId, String language) {
        log.info("Person banking requested for personId: {} with language: {}", personId, language);

        PersonBanking personBanking = personService.getPersonBanking(personId, language);
        BankAccountsDTO bankAccountsDTO = PersonDtoMapper.INSTANCE.toBankAccountsDTO(personBanking);

        return ResponseEntity.ok(bankAccountsDTO);
    }

    @Override
    public ResponseEntity<EducationDetailsDTO> getPersonEducation(Integer personId, String language) {
        log.info("Person education requested for personId: {} with language: {}", personId, language);

        PersonEducation personEducation = personService.getPersonEducation(personId, language);
        EducationDetailsDTO educationDetailsDTO = PersonDtoMapper.INSTANCE.toEducationDetailsDTO(personEducation);

        return ResponseEntity.ok(educationDetailsDTO);
    }

}
