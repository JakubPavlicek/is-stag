package com.stag.identity.person.service;

import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.mapper.PersonAddressMapper;
import com.stag.identity.person.mapper.PersonBankingMapper;
import com.stag.identity.person.mapper.PersonEducationMapper;
import com.stag.identity.person.mapper.PersonProfileMapper;
import com.stag.identity.person.model.PersonAddresses;
import com.stag.identity.person.model.PersonBanking;
import com.stag.identity.person.model.PersonEducation;
import com.stag.identity.person.model.PersonProfile;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.PersonAddressProjection;
import com.stag.identity.person.repository.projection.PersonBankProjection;
import com.stag.identity.person.repository.projection.PersonEducationProjection;
import com.stag.identity.person.repository.projection.PersonProfileProjection;
import com.stag.identity.person.service.data.PersonAddressData;
import com.stag.identity.person.service.data.PersonBankingData;
import com.stag.identity.person.service.data.PersonEducationData;
import com.stag.identity.person.service.data.PersonProfileData;
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

    public PersonProfile getPersonProfile(Integer personId, String language) {
        PersonProfileProjection personProfile =
            personRepository.findById(personId, PersonProfileProjection.class)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<List<String>> personalNumbersFuture =
            personAsyncService.getStudentPersonalNumbers(personId);

        CompletableFuture<PersonProfileData> profileDataFuture =
            personAsyncService.getPersonProfileData(personProfile, language);

        CompletableFuture.allOf(personalNumbersFuture, profileDataFuture).join();

        return PersonProfileMapper.INSTANCE.toPersonProfile(personProfile, personalNumbersFuture.join(), profileDataFuture.join());
    }

    public PersonAddresses getPersonAddresses(Integer personId, String language) {
        PersonAddressProjection personAddressProjection =
            personRepository.findAddressesByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonAddressData> addressDataFuture =
            personAsyncService.getPersonAddressData(personAddressProjection, language);

        return PersonAddressMapper.INSTANCE.toPersonAddresses(personAddressProjection, addressDataFuture.join());
    }

    public PersonBanking getPersonBanking(Integer personId, String language) {
        PersonBankProjection personBankProjection =
            personRepository.findBankingByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonBankingData> bankingDataFuture =
            personAsyncService.getPersonBankingData(personBankProjection, language);

        return PersonBankingMapper.INSTANCE.toPersonBanking(personBankProjection, bankingDataFuture.join());
    }

    public PersonEducation getPersonEducation(Integer personId, String language) {
        PersonEducationProjection personEducationProjection =
            personRepository.findEducationByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        CompletableFuture<PersonEducationData> educationDataFuture =
            personAsyncService.getPersonEducationData(personEducationProjection, language);

        return PersonEducationMapper.INSTANCE.toPersonEducation(personEducationProjection, educationDataFuture.join());
    }

}
