package com.stag.identity.person.repository.projection;

public record SimpleProfileView(
    String firstName,
    String lastName,
    String titlePrefix,
    String titleSuffix,
    String gender
) {

}
