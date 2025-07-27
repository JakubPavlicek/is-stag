package com.stag.identity.user.service;

import com.stag.identity.user.mapper.PersonMapper;
import com.stag.identity.user.model.PersonProfile;
import com.stag.identity.user.repository.PersonRepository;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
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

        CompletableFuture<List<String>> personalNumbersFuture = personAsyncService.getStudentPersonalNumbersAsync(personId);
        CompletableFuture<PersonProfileData> profileDataFuture = personAsyncService.getPersonProfileDataAsync(personProfile);

        CompletableFuture.allOf(personalNumbersFuture, profileDataFuture).join();

        return personMapper.toPersonProfile(personProfile, personalNumbersFuture.join(), profileDataFuture.join());
    }

    public Object getPersonAddresses(Integer personId) {
        AddressProjection address = getPersonById(personId);

        // Get only the codelist data needed for addresses
        PersonAddressCodelistData addressCodelistData = codelistServiceClient.getPersonAddressCodelistData(person);

        // TODO: Implement address mapping
        return null;
    }
//
//    /**
//     * Get person banking data with codelist lookups
//     */
//    public Object getPersonBanking(Integer personId) {
//        Person person = getPersonById(personId);
//
//        // Get only the codelist data needed for banking
//        PersonBankingCodelistData bankingCodelistData = codelistServiceClient.getPersonBankingCodelistData(person);
//
//        // TODO: Implement banking mapping
//        return null;
//    }
//
//    /**
//     * Get person education data with codelist lookups
//     */
//    public Object getPersonEducation(Integer personId) {
//        Person person = getPersonById(personId);
//
//        // Get only the codelist data needed for education
//        PersonEducationCodelistData educationCodelistData = codelistServiceClient.getPersonEducationCodelistData(person);
//
//        // TODO: Implement education mapping
//        return null;
//    }

    private PersonProfileProjection getPersonProfileById(Integer personId) {
        return personRepository.findById(personId, PersonProfileProjection.class)
                               .orElseThrow(() -> new IllegalArgumentException("Person with ID: " + personId + " not found"));
    }

}
