package com.stag.identity.user.application.person.dto;

import lombok.Builder;

@Builder
public record PersonBankingResult(
    BankAccount account,
    EuroBankAccount euroAccount
) {

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
    ) {

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
        String swift
    ) {

    }

}

