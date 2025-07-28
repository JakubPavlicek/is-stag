package com.stag.identity.user.exception;

import lombok.Getter;

@Getter
public class PersonNotFoundException extends RuntimeException {

    private final Integer personId;

    public PersonNotFoundException(Integer personId) {
        super("Person with ID: " + personId + " not found.");
        this.personId = personId;
    }

}
