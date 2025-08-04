package com.stag.identity.user.service;

import com.stag.identity.user.exception.PersonNotFoundException;
import com.stag.identity.user.mapper.PersonMapper;
import com.stag.identity.user.model.PersonAddresses;
import com.stag.identity.user.model.PersonBanking;
import com.stag.identity.user.model.PersonEducation;
import com.stag.identity.user.model.PersonProfile;
import com.stag.identity.user.repository.PersonRepository;
import com.stag.identity.user.repository.projection.PersonAddressProjection;
import com.stag.identity.user.repository.projection.PersonBankProjection;
import com.stag.identity.user.repository.projection.PersonEducationProjection;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonBankingData;
import com.stag.identity.user.service.data.PersonEducationData;
import com.stag.identity.user.service.data.PersonProfileData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonAsyncService personAsyncService;
    private final PersonMapper personMapper;

    public PersonProfile getPersonProfile(Integer personId, String language) {
        PersonProfileProjection personProfile =
            personRepository.findById(personId, PersonProfileProjection.class)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<List<String>> personalNumbersFuture =
            personAsyncService.getStudentPersonalNumbers(personId);

        CompletableFuture<PersonProfileData> profileDataFuture =
            personAsyncService.getPersonProfileData(personProfile, language);

        CompletableFuture.allOf(personalNumbersFuture, profileDataFuture).join();

        return personMapper.toPersonProfile(personProfile, personalNumbersFuture.join(), profileDataFuture.join());
    }

    public PersonAddresses getPersonAddresses(Integer personId, String language) {
        PersonAddressProjection personAddressProjection =
            personRepository.findAddressesByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonAddressData> addressDataFuture =
            personAsyncService.getPersonAddressData(personAddressProjection, language);

        return personMapper.toPersonAddresses(personAddressProjection, addressDataFuture.join());
    }

    public PersonBanking getPersonBanking(Integer personId, String language) {
        PersonBankProjection personBankProjection =
            personRepository.findBankingByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonBankingData> bankingDataFuture =
            personAsyncService.getPersonBankingData(personBankProjection, language);

        return personMapper.toPersonBanking(personBankProjection, bankingDataFuture.join());
    }

    public PersonEducation getPersonEducation(Integer personId, String language) {
        PersonEducationProjection personEducationProjection =
            personRepository.findEducationByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonEducationData> educationDataFuture =
            personAsyncService.getPersonEducationData(personEducationProjection, language);

        return personMapper.toPersonEducation(personEducationProjection, educationDataFuture.join());
    }

}
