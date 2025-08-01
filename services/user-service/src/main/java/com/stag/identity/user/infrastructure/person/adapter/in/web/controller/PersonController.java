package com.stag.identity.user.infrastructure.person.adapter.in.web.controller;

import com.stag.identity.user.application.person.dto.PersonAddressesResult;
import com.stag.identity.user.application.person.dto.PersonBankingResult;
import com.stag.identity.user.application.person.dto.PersonEducationResult;
import com.stag.identity.user.application.person.dto.PersonProfileResult;
import com.stag.identity.user.application.person.port.in.GetPersonAddressesUseCase;
import com.stag.identity.user.application.person.port.in.GetPersonBankingUseCase;
import com.stag.identity.user.application.person.port.in.GetPersonEducationUseCase;
import com.stag.identity.user.application.person.port.in.GetPersonProfileUseCase;
import com.stag.identity.user.domain.person.model.PersonId;
import com.stag.identity.user.infrastructure.PersonsApi;
import com.stag.identity.user.infrastructure.dto.AddressesDTO;
import com.stag.identity.user.infrastructure.dto.BankAccountsDTO;
import com.stag.identity.user.infrastructure.dto.EducationDetailsDTO;
import com.stag.identity.user.infrastructure.dto.PersonProfileDTO;
import com.stag.identity.user.infrastructure.person.adapter.in.web.mapper.PersonWebMapper;
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

    private final GetPersonProfileUseCase getPersonProfileUseCase;
    private final GetPersonAddressesUseCase getPersonAddressesUseCase;
    private final GetPersonBankingUseCase getPersonBankingUseCase;
    private final GetPersonEducationUseCase getPersonEducationUseCase;
//    private final PersonService personService;
    private final PersonWebMapper personMapper;

    // TODO: handle the language parameter properly, defaulting to a specific language if not provided

    @Override
    public ResponseEntity<PersonProfileDTO> getPersonProfile(Integer personId, String language) {
        PersonId domainPersonId = new PersonId(personId);

        PersonProfileResult personProfileResult = getPersonProfileUseCase.getPersonProfile(domainPersonId, language);
        PersonProfileDTO personProfileDTO = personMapper.toPersonProfileDTO(personProfileResult);

        return ResponseEntity.ok(personProfileDTO);
    }

    @Override
    public ResponseEntity<AddressesDTO> getPersonAddresses(Integer personId, String language) {
        PersonId domainPersonId = new PersonId(personId);

        PersonAddressesResult personAddressesResult = getPersonAddressesUseCase.getPersonAddresses(domainPersonId, language);
        AddressesDTO addressesDTO = personMapper.toAddressesDTO(personAddressesResult);

        return ResponseEntity.ok(addressesDTO);
    }

    @Override
    public ResponseEntity<BankAccountsDTO> getPersonBanking(Integer personId, String language) {
        PersonId domainPersonId = new PersonId(personId);

        PersonBankingResult personBankingResult = getPersonBankingUseCase.getPersonBanking(domainPersonId, language);
        BankAccountsDTO bankAccountsDTO = personMapper.toBankAccountsDTO(personBankingResult);

        return ResponseEntity.ok(bankAccountsDTO);
    }

    @Override
    public ResponseEntity<EducationDetailsDTO> getPersonEducation(Integer personId, String language) {
        PersonId domainPersonId = new PersonId(personId);

        PersonEducationResult personEducation = getPersonEducationUseCase.getPersonEducation(domainPersonId, language);
        EducationDetailsDTO educationDetailsDTO = personMapper.toEducationDetailsDTO(personEducation);

        return ResponseEntity.ok(educationDetailsDTO);
    }

}
