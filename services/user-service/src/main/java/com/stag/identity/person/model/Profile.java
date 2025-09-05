package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Builder
public record Profile(
    Integer personId,
    List<String> studentIds,
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
) implements Serializable {

    public record Contact(
        String email,
        String phone,
        String mobile
    ) implements Serializable {

    }

    public record Titles(
        String prefix,
        String suffix
    ) implements Serializable {

    }

    public record BirthPlace(
        String city,
        String country
    ) implements Serializable {

    }

    public record Citizenship(
        String country,
        String qualifier
    ) implements Serializable {

    }

}
