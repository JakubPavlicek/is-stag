package com.stag.identity.person.model;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PersonEducation(
    HighSchool highSchool,
    ForeignHighSchool foreignHighSchool
) {

    @Builder
    public record HighSchool(
        String name,
        String fieldOfStudy,
        LocalDate graduationDate,
        HighSchoolAddress address
    ) {

    }

    @Builder
    public record ForeignHighSchool(
        String name,
        String location,
        String fieldOfStudy
    ) {

    }

    @Builder
    public record HighSchoolAddress(
        String street,
        String zipCode,
        String municipality,
        String district,
        String country
    ) {

    }

}
