package com.stag.identity.person.service.dto;

import com.stag.identity.person.model.Profile;

/// **Person Update Command**
///
/// Command object for person profile update operations. Contains all editable
/// profile fields including personal info, contact details, and banking information.
/// Validated by codelist service before persisting to a database.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record PersonUpdateCommand(
    String birthSurname,
    String maritalStatus,
    Profile.Contact contact,
    Profile.Titles titles,
    Profile.BirthPlace birthPlace,
    BankAccount bankAccount
) {

    /// Bank account update data with Czech account format and holder information.
    public record BankAccount(
        String prefix,
        String suffix,
        String bankCode,
        String holderName,
        String holderAddress
    ) {

    }

}
