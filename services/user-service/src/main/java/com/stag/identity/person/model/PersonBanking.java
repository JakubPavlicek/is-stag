package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record PersonBanking(
    BankAccount account,
    EuroBankAccount euroAccount
) implements Serializable {

    @Builder
    public record BankAccount(
        String owner,
        String address,
        String prefix,
        String suffix,
        String bankCode,
        String bankName,
        String iban,
        String currency
    ) implements Serializable {

    }

    @Builder
    public record EuroBankAccount(
        String owner,
        String address,
        String prefix,
        String suffix,
        String bankCode,
        String bankName,
        String iban,
        String currency,
        String country,
        String swiftCode
    ) implements Serializable {

    }

}

