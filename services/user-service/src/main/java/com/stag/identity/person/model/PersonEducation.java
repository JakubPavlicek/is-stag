package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
public record PersonEducation(
    HighSchool highSchool,
    ForeignHighSchool foreignHighSchool
) implements Serializable {

    @Builder
    public record HighSchool(
        String name,
        String fieldOfStudy,
        LocalDate graduationDate,
        HighSchoolAddress address
    ) implements Serializable {

    }

    @Builder
    public record ForeignHighSchool(
        String name,
        String location,
        String fieldOfStudy
    ) implements Serializable {

    }

    @Builder
    public record HighSchoolAddress(
        String street,
        String zipCode,
        String municipality,
        String district,
        String country
    ) implements Serializable {

    }

}
