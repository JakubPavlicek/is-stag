package com.stag.identity.user.domain.person.model;

import lombok.Builder;
import lombok.Getter;

/**
 * A Value Object representing a person's full name, including titles and birth name.
 */
@Getter
@Builder
public class PersonName {
    private final String firstName;
    private final String lastName;
    private final String birthSurname;
    private final String titlePrefix;
    private final String titleSuffix;
}
