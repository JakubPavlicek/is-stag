package com.stag.identity.user.service;

import com.stag.identity.user.dto.PersonProfileInternal;
import com.stag.identity.user.entity.Osoba;
import com.stag.identity.user.mapper.PersonMapper;
import com.stag.identity.user.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    private Osoba getPersonById(Integer personId) {
        return personRepository.findById(personId)
                               .orElseThrow(() -> new IllegalArgumentException("Person with ID: " + personId + " not found"));
    }

    public PersonProfileInternal getPerson(Integer personId) {
        Osoba person = getPersonById(personId);

        // TODO: Call gRPC service to get personal numbers (STUDENT table)
        List<String> personalNumbers = null;

        return personMapper.toPersonProfileInternal(person, personalNumbers);
    }

}