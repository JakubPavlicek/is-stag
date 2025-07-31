package com.stag.identity.user.model;

import com.stag.identity.user.model.PersonAddresses.HighSchoolAddress;
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

}
