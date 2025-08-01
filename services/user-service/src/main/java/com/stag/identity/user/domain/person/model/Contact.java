package com.stag.identity.user.domain.person.model;

import lombok.Builder;
import lombok.Getter;

/**
 * A Value Object for a person's contact information.
 */
@Getter
@Builder
public class Contact {
    private final String email;
    private final String phone;
    private final String mobile;
    private final String dataBoxAddress;
}
