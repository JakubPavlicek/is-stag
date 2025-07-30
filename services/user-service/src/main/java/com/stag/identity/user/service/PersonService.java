package com.stag.identity.user.service;

import com.stag.identity.user.exception.PersonNotFoundException;
import com.stag.identity.user.mapper.PersonMapper;
import com.stag.identity.user.model.PersonAddresses;
import com.stag.identity.user.model.PersonBanking;
import com.stag.identity.user.model.PersonProfile;
import com.stag.identity.user.repository.PersonRepository;
import com.stag.identity.user.repository.projection.PersonAddressProjection;
import com.stag.identity.user.repository.projection.PersonBankProjection;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonBankingData;
import com.stag.identity.user.service.data.PersonProfileData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PersonAsyncService personAsyncService;

    @Transactional(readOnly = true)
    public PersonProfile getPersonProfile(Integer personId) {
        PersonProfileProjection personProfile =
            personRepository.findById(personId, PersonProfileProjection.class)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<List<String>> personalNumbersFuture =
            personAsyncService.getStudentPersonalNumbers(personId);

        CompletableFuture<PersonProfileData> profileDataFuture =
            personAsyncService.getPersonProfileData(personProfile);

        CompletableFuture.allOf(personalNumbersFuture, profileDataFuture).join();

        return personMapper.toPersonProfile(personProfile, personalNumbersFuture.join(), profileDataFuture.join());
    }

    @Transactional(readOnly = true)
    public PersonAddresses getPersonAddresses(Integer personId) {
        PersonAddressProjection personAddressProjection =
            personRepository.findAddressesByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonAddressData> addressDataFuture =
            personAsyncService.getPersonAddressData(personAddressProjection);

        return personMapper.toPersonAddresses(personAddressProjection, addressDataFuture.join());
    }

    @Transactional(readOnly = true)
    public PersonBanking getPersonBanking(Integer personId) {
        PersonBankProjection personBankProjection =
            personRepository.findBankingByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonBankingData> personBankingDataFuture =
            personAsyncService.getPersonBankingData(personBankProjection);

        return personMapper.toPersonBanking(personBankProjection, personBankingDataFuture.join());
    }

}
