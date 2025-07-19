package com.stag.identity.user.controller;

import com.stag.identity.user.PersonsApi;
import com.stag.identity.user.dto.Addresses;
import com.stag.identity.user.dto.BankAccounts;
import com.stag.identity.user.dto.EducationDetails;
import com.stag.identity.user.dto.PersonProfile;
import com.stag.identity.user.dto.PersonProfileInternal;
import com.stag.identity.user.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PersonController implements PersonsApi {

    private final PersonService personService;

    @Override
    public ResponseEntity<PersonProfile> getPerson(Integer personId) {
        PersonProfileInternal personProfileInternal = personService.getPerson(personId);
        return null;
    }

    @Override
    public ResponseEntity<Addresses> getPersonAddresses(Integer personId) {
        return PersonsApi.super.getPersonAddresses(personId);
    }

    @Override
    public ResponseEntity<BankAccounts> getPersonBanking(Integer personId) {
        return PersonsApi.super.getPersonBanking(personId);
    }

    @Override
    public ResponseEntity<EducationDetails> getPersonEducation(Integer personId) {
        return PersonsApi.super.getPersonEducation(personId);
    }

}
