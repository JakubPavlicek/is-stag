package com.stag.identity.user.domain.person.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * A Value Object for a person's high school education details.
 */
@Getter
@Builder
public class HighSchool {
    private final String name;
    private final String location; // For foreign schools
    private final String fieldOfStudy;
    private final LocalDate graduationDate;
    private final Address address;
}
