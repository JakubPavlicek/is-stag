package com.stag.identity.user.domain.person.exception;

import com.stag.identity.user.domain.person.model.PersonId;
import lombok.Getter;

@Getter
public class PersonNotFoundException extends RuntimeException {

    private final PersonId personId;

    public PersonNotFoundException(PersonId personId) {
        super("Person with ID: " + personId.id() + " not found.");
        this.personId = personId;
    }

}
