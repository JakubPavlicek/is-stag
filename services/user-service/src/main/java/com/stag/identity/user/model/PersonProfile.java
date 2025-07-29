package com.stag.identity.user.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PersonProfile {

    private Integer personId;
    private List<String> personalNumbers;
    private String firstName;
    private String lastName;
    private String birthSurname;
    private Contact contact;
    private Titles titles;
    private String birthNumber;
    private LocalDate birthDate;
    private BirthPlace birthPlace;
    private Citizenship citizenship;
    private String passportNumber;
    private String gender;
    private String maritalStatus;

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
