package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;

/// **Banking Model**
///
/// Person banking information supporting Czech and Euro bank accounts.
/// Includes localized bank names and IBAN for international transfers.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record Banking(
    BankAccount account,
    EuroBankAccount euroAccount
) implements Serializable {

    /// Czech bank account with prefix/suffix format and localized bank name.
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

    /// Euro bank account with SWIFT code for international transfers.
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

