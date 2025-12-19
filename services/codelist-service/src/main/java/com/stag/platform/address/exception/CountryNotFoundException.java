package com.stag.platform.address.exception;

import lombok.Getter;

@Getter
public class CountryNotFoundException extends RuntimeException {

    private final String countryName;

    public CountryNotFoundException(String countryName) {
        super("Country not found: " + countryName);
        this.countryName = countryName;
    }

}
