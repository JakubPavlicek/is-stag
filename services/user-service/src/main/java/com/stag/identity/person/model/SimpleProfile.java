package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record SimpleProfile(
    String firstName,
    String lastName,
    Profile.Titles titles,
    String gender
) implements Serializable {

}
