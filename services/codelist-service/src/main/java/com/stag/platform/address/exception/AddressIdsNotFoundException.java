package com.stag.platform.address.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class AddressIdsNotFoundException extends RuntimeException {

    private final List<String> addressNames;

    public AddressIdsNotFoundException(List<String> addressNames) {
        super("Unable to find address ids for names: [" + String.join(", ", addressNames) + "]");
        this.addressNames = addressNames;
    }

}
