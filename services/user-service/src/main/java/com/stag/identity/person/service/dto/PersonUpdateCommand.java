package com.stag.identity.person.service.dto;

import com.stag.identity.person.model.Profile;

public record PersonUpdateCommand(
    String birthSurname,
    String maritalStatus,
    Profile.Contact contact,
    Profile.Titles titles,
    Profile.BirthPlace birthPlace,
    Address temporaryAddress,
    BankAccount bankAccount
) {

    public record Address(
        String country,
        String district,
        String municipality,
        String municipalityPart,
        String street,
        String streetNumber,
        String zipCode,
        String postOffice
    ) {

    }

    public record BankAccount(
        String prefix,
        String suffix,
        String bankCode,
        String holderName,
        String holderAddress
    ) {

    }

}
