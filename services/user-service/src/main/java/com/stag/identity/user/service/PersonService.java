package com.stag.identity.user.service;

import com.stag.identity.user.dto.PersonProfileInternal;
import com.stag.identity.user.entity.Person;
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

    private Person getPersonById(Integer personId) {
        return personRepository.findById(personId)
                               .orElseThrow(() -> new IllegalArgumentException("Person with ID: " + personId + " not found"));
    }

    public PersonProfileInternal getPerson(Integer personId) {
        Person person = getPersonById(personId);

        // TODO: Call gRPC service to get personal numbers (STUDENT table)
        List<String> personalNumbers = studentServiceClient.getStudentPersonalNumbers(personId);
        log.info("Got personal numbers size: {}", personalNumbers.size());

        return personMapper.toPersonProfileInternal(person, personalNumbers);
    }

}