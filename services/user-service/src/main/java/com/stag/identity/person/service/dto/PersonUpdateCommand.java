package com.stag.identity.person.service.dto;

import com.stag.identity.person.model.Profile;

/// **Person Update Command**
///
/// Command object for person profile update operations.
/// Contains all editable profile fields including personal info, contact details, and banking information.
/// Validated by codelist service before persisting to a database.
///
/// @param birthSurname The birth surname of the person.
/// @param maritalStatus The marital status of the person.
/// @param contact The contact information of the person.
/// @param titles The titles of the person.
/// @param birthPlace The birthplace of the person.
/// @param bankAccount The bank account of the person.
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
    ///
    /// @param prefix The bank account prefix.
    /// @param suffix The bank account suffix.
    /// @param bankCode The bank code.
    /// @param holderName The name of the account holder.
    /// @param holderAddress The address of the account holder.
    public record BankAccount(
        String prefix,
        String suffix,
        String bankCode,
        String holderName,
        String holderAddress
    ) {

    }

}
