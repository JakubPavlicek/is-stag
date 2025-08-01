package com.stag.identity.user.domain.person.model;

public record PersonBank(
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
    String euroAccountSwift
) {

}
