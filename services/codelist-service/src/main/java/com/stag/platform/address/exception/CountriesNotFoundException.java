package com.stag.platform.address.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CountriesNotFoundException extends RuntimeException {
    public CountriesNotFoundException(String message) {
        super(message);
    }
}
