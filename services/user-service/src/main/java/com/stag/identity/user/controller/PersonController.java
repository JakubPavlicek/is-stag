package com.stag.identity.user.controller;

import com.stag.identity.user.PersonsApi;
import com.stag.identity.user.dto.AddressesDTO;
import com.stag.identity.user.dto.BankAccountsDTO;
import com.stag.identity.user.dto.EducationDetailsDTO;
import com.stag.identity.user.dto.PersonProfileDTO;
import com.stag.identity.user.mapper.PersonMapper;
import com.stag.identity.user.model.PersonProfile;
import com.stag.identity.user.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PersonController implements PersonsApi {

    private final PersonService personService;
    private final PersonMapper personMapper;

    @Override
    public ResponseEntity<PersonProfileDTO> getPerson(Integer personId) {
        PersonProfile personProfile = personService.getPersonProfile(personId);
        PersonProfileDTO personProfileDTO = personMapper.toPersonProfileDTO(personProfile);

        return ResponseEntity.ok(personProfileDTO);
    }

    @Override
    public ResponseEntity<AddressesDTO> getPersonAddresses(Integer personId) {
        return PersonsApi.super.getPersonAddresses(personId);
    }

    @Override
    public ResponseEntity<BankAccountsDTO> getPersonBanking(Integer personId) {
        return PersonsApi.super.getPersonBanking(personId);
    }

    @Override
    public ResponseEntity<EducationDetailsDTO> getPersonEducation(Integer personId) {
        return PersonsApi.super.getPersonEducation(personId);
    }

}
