package com.stag.identity.person.exception;

import lombok.Getter;

@Getter
public class InvalidAccountNumberException extends RuntimeException {

    private final String accountNumber;

    public InvalidAccountNumberException(String accountNumber) {
        super("Invalid account number: " + accountNumber);
        this.accountNumber = accountNumber;
    }

}
