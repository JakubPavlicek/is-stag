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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonAsyncService personAsyncService;

    @Cacheable(value = "person-profile", key = "#personId + ':' + #language")
    @PreAuthorize("hasAnyRole('PR', 'AD', 'SP', 'SR') || @authorizationService.canAccessPerson(authentication, #personId)")
    public PersonProfile getPersonProfile(Integer personId, String language) {
        log.info("Fetching person profile for personId: {} with language: {}", personId, language);

        PersonProfileProjection personProfileProjection =
            personRepository.findById(personId, PersonProfileProjection.class)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        log.debug("Person profile found, fetching additional data for personId: {}", personId);

        CompletableFuture<List<String>> studentIdsFuture =
            personAsyncService.getStudentids(personId);

        CompletableFuture<PersonProfileData> profileDataFuture =
            personAsyncService.getPersonProfileData(personProfileProjection, language);

        CompletableFuture.allOf(studentIdsFuture, profileDataFuture).join();

        log.debug("Additional data fetched, mapping to PersonProfile for personId: {}", personId);
        PersonProfile personProfile = PersonProfileMapper.INSTANCE.toPersonProfile(
            personProfileProjection, studentIdsFuture.join(), profileDataFuture.join()
        );

        log.info("Successfully fetched person profile for personId: {}", personId);
        return personProfile;
    }

    @Cacheable(value = "person-addresses", key = "#personId + ':' + #language")
    @PreAuthorize("hasAnyRole('PR', 'AD', 'SP', 'SR') || @authorizationService.canAccessPerson(authentication, #personId)")
    public PersonAddresses getPersonAddresses(Integer personId, String language) {
        log.info("Fetching person addresses for personId: {} with language: {}", personId, language);

        PersonAddressProjection personAddressProjection =
            personRepository.findAddressesByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        log.debug("Person addresses found, fetching additional data for personId: {}", personId);

        CompletableFuture<PersonAddressData> addressDataFuture =
            personAsyncService.getPersonAddressData(personAddressProjection, language);

        PersonAddressData addressData = addressDataFuture.join();

        log.debug("Additional data fetched, mapping to PersonAddresses for personId: {}", personId);
        PersonAddresses personAddresses = PersonAddressMapper.INSTANCE.toPersonAddresses(
            personAddressProjection, addressData
        );

        log.info("Successfully fetched person addresses for personId: {}", personId);
        return personAddresses;
    }

    @Cacheable(value = "person-banking", key = "#personId + ':' + #language")
    @PreAuthorize("hasAnyRole('PR', 'AD', 'SP', 'SR') || @authorizationService.canAccessPerson(authentication, #personId)")
    public PersonBanking getPersonBanking(Integer personId, String language) {
        log.info("Fetching person banking information for personId: {} with language: {}", personId, language);

        PersonBankProjection personBankProjection =
            personRepository.findBankingByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        log.debug("Person banking information found, fetching additional data for personId: {}", personId);

        CompletableFuture<PersonBankingData> bankingDataFuture =
            personAsyncService.getPersonBankingData(personBankProjection, language);

        PersonBankingData bankingData = bankingDataFuture.join();

        log.debug("Additional data fetched, mapping to PersonBanking for personId: {}", personId);
        PersonBanking personBanking = PersonBankingMapper.INSTANCE.toPersonBanking(
            personBankProjection, bankingData
        );

        log.info("Successfully fetched person banking information for personId: {}", personId);
        return personBanking;
    }

    @Cacheable(value = "person-education", key = "#personId + ':' + #language")
    @PreAuthorize("hasAnyRole('PR', 'AD', 'SP', 'SR') || @authorizationService.canAccessPerson(authentication, #personId)")
    public PersonEducation getPersonEducation(Integer personId, String language) {
        log.info("Fetching person education information for personId: {} with language: {}", personId, language);

        PersonEducationProjection personEducationProjection =
            personRepository.findEducationByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        log.debug("Person education information found, fetching additional data for personId: {}", personId);

        CompletableFuture<PersonEducationData> educationDataFuture =
            personAsyncService.getPersonEducationData(personEducationProjection, language);

        PersonEducationData educationData = educationDataFuture.join();

        log.debug("Additional data fetched, mapping to PersonEducation for personId: {}", personId);
        PersonEducation personEducation = PersonEducationMapper.INSTANCE.toPersonEducation(
            personEducationProjection, educationData
        );

        log.info("Successfully fetched person education information for personId: {}", personId);
        return personEducation;
    }

}
