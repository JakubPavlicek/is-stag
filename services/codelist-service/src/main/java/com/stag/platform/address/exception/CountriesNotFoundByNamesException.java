package com.stag.platform.address.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class CountriesNotFoundByNamesException extends RuntimeException {

    private final List<String> missingNames;

    public CountriesNotFoundByNamesException(List<String> missingNames) {
        super("Unable to find countries for names: [" + String.join(", ", missingNames) + "]");
        this.missingNames = missingNames;
    }

}
