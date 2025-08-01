package com.stag.identity.user.application.person.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PersonProfileResult(
    Integer personId,
    List<String> personalNumbers,
    String firstName,
    String lastName,
    String birthSurname,
    Contact contact,
    Titles titles,
    String birthNumber,
    LocalDate birthDate,
    BirthPlace birthPlace,
    Citizenship citizenship,
    String passportNumber,
    String gender,
    String maritalStatus
) {

    public record Contact(
        String email,
        String phone,
        String mobile
    ) {

    }

    public record Titles(
        String prefix,
        String suffix
    ) {

    }

    public record BirthPlace(
        String city,
        String country
    ) {

    }

    public record Citizenship(
        String country,
        String qualifier
    ) {

    }

}
