package com.stag.identity.user.service;

import com.stag.identity.user.mapper.PersonMapper;
import com.stag.identity.user.model.Addresses;
import com.stag.identity.user.model.PersonProfile;
import com.stag.identity.user.repository.PersonRepository;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonForeignAddressData;
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
        PersonProfileProjection personProfile = getPersonProfileById(personId);

        CompletableFuture<List<String>> personalNumbersFuture =
            personAsyncService.getStudentPersonalNumbers(personId);

        CompletableFuture<PersonProfileData> profileDataFuture =
            personAsyncService.getPersonProfileData(personProfile);

        CompletableFuture.allOf(personalNumbersFuture, profileDataFuture).join();

        return personMapper.toPersonProfile(personProfile, personalNumbersFuture.join(), profileDataFuture.join());
    }

    @Transactional(readOnly = true)
    public Addresses getPersonAddresses(Integer personId) {
        if (!personRepository.existsById(personId)) {
            throw new IllegalArgumentException("Person with ID: " + personId + " not found");
        }

        CompletableFuture<PersonAddressData> addressDataFuture =
            personAsyncService.getPersonAddressData(personId);

        CompletableFuture<PersonForeignAddressData> foreignAddressesFuture =
            personAsyncService.getForeignAddressesByPersonId(personId);

        CompletableFuture.allOf(addressDataFuture, foreignAddressesFuture).join();

        return personMapper.mapToAddresses(addressDataFuture.join(), foreignAddressesFuture.join());
    }

    private PersonProfileProjection getPersonProfileById(Integer personId) {
        return personRepository.findById(personId, PersonProfileProjection.class)
                               .orElseThrow(() -> new IllegalArgumentException("Person with ID: " + personId + " not found"));
    }

}
