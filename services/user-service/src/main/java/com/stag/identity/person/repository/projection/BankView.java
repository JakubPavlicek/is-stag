package com.stag.identity.person.repository.projection;

/// **Bank View Projection**
///
/// View of a person's banking information including standard Czech account
/// and Euro account details.
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
