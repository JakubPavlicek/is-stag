package com.stag.identity.user.domain.person.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/// Represents the Person aggregate root.
/// It encapsulates all information related to a person, ensuring consistency.
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Person {

    private final PersonId personId;
    private List<String> personalNumbers; // Student numbers

    // --- Composed Value Objects ---
    private PersonName name;
    private Contact contact;
    private Address permanentAddress;
    private Address temporaryAddress;
    private BankAccount bankAccount;
    private BankAccount euroBankAccount;
    private HighSchool highSchool;

    // --- Core Person Attributes ---
    private String birthNumber;
    private LocalDate birthDate;
    private String birthPlace;
    private String birthCountry;
    private String citizenship;
    private String citizenshipQualification;
    private String passportNumber;
    private String gender;
    private String maritalStatus;

    // --- Business Methods ---

    public void changeContactInfo(Contact newContact) {
        // Add validation logic here if needed
        this.contact = newContact;
    }

    public void updatePermanentAddress(Address newAddress) {
        // Add validation logic here if needed
        this.permanentAddress = newAddress;
    }

    // Add other business methods as needed...
}