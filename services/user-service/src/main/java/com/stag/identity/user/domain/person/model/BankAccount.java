package com.stag.identity.user.domain.person.model;

import lombok.Builder;
import lombok.Getter;

/**
 * A Value Object for a person's bank account.
 */
@Getter
@Builder
public class BankAccount {
    private final String owner;
    private final String address;
    private final String prefix;
    private final String suffix;
    private final String bankCode;
    private final String bankName;
    private final String iban;
    private final String swift;
    private final String currency;
    private final String country;
}
