package com.stag.keycloak.authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IsStagUserDetails(
    @JsonProperty("userName") String userName,
    @JsonProperty("role") String role,
    @JsonProperty("email") String email,
    @JsonProperty("osCislo") Optional<String> personalNumber,
    @JsonProperty("ucitIdno") Optional<Long> teacherId
) {

}
