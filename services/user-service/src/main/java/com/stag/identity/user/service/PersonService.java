package com.stag.identity.user.service;

import com.stag.identity.user.dto.PersonProfileCodelistData;
import com.stag.identity.user.dto.PersonProfileInternal;
import com.stag.identity.user.entity.Person;
import com.stag.identity.user.grpc.CodelistServiceClient;
import com.stag.identity.user.grpc.StudentServiceClient;
import com.stag.identity.user.mapper.PersonMapper;
import com.stag.identity.user.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    private final StudentServiceClient studentServiceClient;
    private final CodelistServiceClient codelistServiceClient;

    public PersonProfileInternal getPerson(Integer personId) {
        Person person = getPersonById(personId);

        List<String> personalNumbers = studentServiceClient.getStudentPersonalNumbers(personId);
        PersonProfileCodelistData profileCodelistData = codelistServiceClient.getPersonProfileCodelistData(person);

        return personMapper.toPersonProfileInternal(person, personalNumbers, profileCodelistData);
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

    private Person getPersonById(Integer personId) {
        return personRepository.findById(personId)
                               .orElseThrow(() -> new IllegalArgumentException("Person with ID: " + personId + " not found"));
    }

}
