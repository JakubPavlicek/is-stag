package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PersonProfileInternal {
    private Integer personId;
    private List<String> personalNumbers;
    private String firstName;
    private String lastName;
    private String birthSurname;
    private ContactInternal contact;
    private TitlesInternal titles;
    private String birthNumber;
    private LocalDate birthDate;
    private BirthPlaceInternal birthPlace;
    private CitizenshipInternal citizenship;
    private String passportNumber;
    private String gender;
    private String maritalStatus;
}
