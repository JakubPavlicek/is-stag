package com.stag.identity.person.repository.projection;

/// **Bank View Projection**
///
/// View of a person's banking information including standard Czech account and Euro account details.
///
/// @param accountOwner the account owner name
/// @param accountAddress the account address
/// @param accountPrefix the account prefix
/// @param accountSuffix the account suffix
/// @param accountBank the account bank code
/// @param accountIban the account IBAN
/// @param accountCurrency the account currency code
/// @param euroAccountOwner the euro account owner name
/// @param euroAccountAddress the euro account address
/// @param euroAccountPrefix the euro account prefix
/// @param euroAccountSuffix the euro account suffix
/// @param euroAccountBank the euro account bank code
/// @param euroAccountIban the euro account IBAN
/// @param euroAccountCurrency the euro account currency code
/// @param euroAccountCountryId the euro account country ID
/// @param euroAccountSwiftCode the euro account SWIFT code
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record BankView(
    String accountOwner,
    String accountAddress,
    String accountPrefix,
    String accountSuffix,
    String accountBank,
    String accountIban,
    String accountCurrency,

    String euroAccountOwner,
    String euroAccountAddress,
    String euroAccountPrefix,
    String euroAccountSuffix,
    String euroAccountBank,
    String euroAccountIban,
    String euroAccountCurrency,
    Integer euroAccountCountryId,
    String euroAccountSwiftCode
) {

}
