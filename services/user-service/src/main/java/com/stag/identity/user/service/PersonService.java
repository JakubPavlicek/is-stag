package com.stag.identity.user.service;

import com.stag.identity.user.dto.PersonProfile;
import com.stag.identity.user.entity.Osoba;
import com.stag.identity.user.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    private Osoba getPersonById(Integer personId) {
        return personRepository.findById(personId)
                        .orElseThrow(() -> new IllegalArgumentException("Person with ID: " + personId + " not found"));
    }

    public void getPerson(Integer personId) {
        Osoba person = getPersonById(personId);
    }

}
