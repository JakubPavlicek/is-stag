package com.stag.keycloak.authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IsStagUserDetails(
    @JsonProperty("userName") String userName,
    @JsonProperty("email") String email,
    @JsonProperty("role") String role,
    @JsonProperty("roleNazev") String roleName,
    @JsonProperty("fakulta") String faculty,
    @JsonProperty("osCislo") Optional<String> personalNumber,
    @JsonProperty("ucitIdno") Optional<Long> teacherIdentifier
) {

}
