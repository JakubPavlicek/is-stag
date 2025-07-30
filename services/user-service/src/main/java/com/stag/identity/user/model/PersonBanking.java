package com.stag.identity.user.model;

public record PersonBanking(
    BankAccount account,
    EuroBankAccount euroAccount
) {

    public record BankAccount(
        String owner,
        String address,
        String prefix,
        String suffix,
        String bank,
        String iban,
        String currency
    ) {

    }

    public record EuroBankAccount(
        String owner,
        String address,
        String prefix,
        String suffix,
        String bank,
        String iban,
        String currency,
        String country,
        String swift
    ) {

    }

}

