package com.stag.identity.person.exception;

import lombok.Getter;

@Getter
public class InvalidDataBoxException extends RuntimeException {

    private final String dataBox;

    public InvalidDataBoxException(String dataBox) {
        super("Invalid data box: " + dataBox);
        this.dataBox = dataBox;
    }

}
