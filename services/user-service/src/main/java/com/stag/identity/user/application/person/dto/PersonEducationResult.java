package com.stag.identity.user.application.person.dto;

import com.stag.identity.user.application.person.dto.PersonAddressesResult.HighSchoolAddress;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PersonEducationResult(
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
