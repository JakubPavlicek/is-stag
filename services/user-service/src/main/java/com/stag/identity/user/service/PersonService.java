package com.stag.identity.user.service;

import com.stag.identity.user.service.data.PersonProfileData;
import com.stag.identity.user.model.PersonProfile;
import com.stag.identity.user.grpc.CodelistServiceClient;
import com.stag.identity.user.grpc.StudentServiceClient;
import com.stag.identity.user.mapper.PersonMapper;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.repository.PersonRepository;
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

    private final StudentServiceClient studentServiceClient;
    private final CodelistServiceClient codelistServiceClient;

    @Transactional(readOnly = true)
    public PersonProfile getPersonProfile(Integer personId) {
        PersonProfileProjection personProfile = getPersonProfileById(personId);

        CompletableFuture<List<String>> personalNumbersFuture = CompletableFuture.supplyAsync(
            () -> studentServiceClient.getStudentPersonalNumbers(personId)
        );
        CompletableFuture<PersonProfileData> profileCodelistDataFuture = CompletableFuture.supplyAsync(
            () -> codelistServiceClient.getPersonProfileData(personProfile)
        );

        CompletableFuture.allOf(personalNumbersFuture, profileCodelistDataFuture).join();

        return personMapper.toPersonProfile(personProfile, personalNumbersFuture.join(), profileCodelistDataFuture.join());
    }

//    /**
//     * Get person address data with codelist lookups
//     */
//    public Object getPersonAddresses(Integer personId) {
//        Person person = getPersonById(personId);
//
//        // Get only the codelist data needed for addresses
//        PersonAddressCodelistData addressCodelistData = codelistServiceClient.getPersonAddressCodelistData(person);
//
//        // TODO: Implement address mapping
//        return null;
//    }
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
