package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;

/// **Banking Model**
///
/// Person banking information supporting Czech and Euro bank accounts.
/// Includes localized bank names and IBAN for international transfers.
///
/// @param account the Czech bank account
/// @param euroAccount the Euro bank account
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record Banking(
    BankAccount account,
    EuroBankAccount euroAccount
) implements Serializable {

    /// Czech bank account with prefix/suffix format and localized bank name.
    ///
    /// @param owner the owner
    /// @param address the address
    /// @param prefix the prefix
    /// @param suffix the suffix
    /// @param bankCode the bank code
    /// @param bankName the bank name
    /// @param iban the IBAN
    /// @param currency the currency
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
    ///
    /// @param owner the owner
    /// @param address the address
    /// @param prefix the prefix
    /// @param suffix the suffix
    /// @param bankCode the bank code
    /// @param bankName the bank name
    /// @param iban the IBAN
    /// @param currency the currency
    /// @param country the country
    /// @param swiftCode the SWIFT code
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

