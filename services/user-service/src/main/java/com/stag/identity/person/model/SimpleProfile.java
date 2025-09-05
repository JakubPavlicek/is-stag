package com.stag.identity.person.model;

import lombok.Builder;

@Builder
public record SimpleProfile(
    String firstName,
    String lastName,
    Profile.Titles titles,
    String gender
) {

}
