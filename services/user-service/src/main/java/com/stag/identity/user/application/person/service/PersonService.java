package com.stag.identity.user.application.person.service;

import com.stag.identity.user.application.person.dto.PersonAddressData;
import com.stag.identity.user.application.person.dto.PersonAddressesResult;
import com.stag.identity.user.application.person.dto.PersonBankingData;
import com.stag.identity.user.application.person.dto.PersonBankingResult;
import com.stag.identity.user.application.person.dto.PersonEducationData;
import com.stag.identity.user.application.person.dto.PersonEducationResult;
import com.stag.identity.user.application.person.dto.PersonProfileData;
import com.stag.identity.user.application.person.dto.PersonProfileResult;
import com.stag.identity.user.application.person.mapper.PersonAppMapper;
import com.stag.identity.user.application.person.port.in.GetPersonAddressesUseCase;
import com.stag.identity.user.application.person.port.in.GetPersonBankingUseCase;
import com.stag.identity.user.application.person.port.in.GetPersonEducationUseCase;
import com.stag.identity.user.application.person.port.in.GetPersonProfileUseCase;
import com.stag.identity.user.application.person.port.out.PersonRepositoryPort;
import com.stag.identity.user.domain.person.exception.PersonNotFoundException;
import com.stag.identity.user.domain.person.model.PersonAddress;
import com.stag.identity.user.domain.person.model.PersonBank;
import com.stag.identity.user.domain.person.model.PersonEducation;
import com.stag.identity.user.domain.person.model.PersonId;
import com.stag.identity.user.domain.person.model.PersonProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService implements
    GetPersonProfileUseCase,
    GetPersonAddressesUseCase,
    GetPersonBankingUseCase,
    GetPersonEducationUseCase {

    private final PersonRepositoryPort personRepository;
    private final PersonAsyncService personAsyncService;
    private final PersonAppMapper personMapper;

    @Override
    public PersonProfileResult getPersonProfile(PersonId personId, String language) {
        PersonProfile personProfile =
            personRepository.findPersonProfileById(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<List<String>> personalNumbersFuture =
            personAsyncService.getStudentPersonalNumbers(personId);

        CompletableFuture<PersonProfileData> profileDataFuture =
            personAsyncService.getPersonProfileData(personProfile);

        CompletableFuture.allOf(personalNumbersFuture, profileDataFuture).join();

        return personMapper.toPersonProfile(personProfile, personalNumbersFuture.join(), profileDataFuture.join());
    }

    @Override
    public PersonAddressesResult getPersonAddresses(PersonId personId, String language) {
        PersonAddress personAddress =
            personRepository.findAddressesByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonAddressData> addressDataFuture =
            personAsyncService.getPersonAddressData(personAddress);

        return personMapper.toPersonAddresses(personAddress, addressDataFuture.join());
    }

    @Override
    public PersonBankingResult getPersonBanking(PersonId personId, String language) {
        PersonBank personBank =
            personRepository.findBankingByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonBankingData> bankingDataFuture =
            personAsyncService.getPersonBankingData(personBank);

        return personMapper.toPersonBanking(personBank, bankingDataFuture.join());
    }

    @Override
    public PersonEducationResult getPersonEducation(PersonId personId, String language) {
        PersonEducation personEducation =
            personRepository.findEducationByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonEducationData> educationDataFuture =
            personAsyncService.getPersonEducationData(personEducation);

        return personMapper.toPersonEducation(personEducation, educationDataFuture.join());
    }

}
