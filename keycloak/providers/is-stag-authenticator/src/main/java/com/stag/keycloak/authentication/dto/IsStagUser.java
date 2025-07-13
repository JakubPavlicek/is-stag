package com.stag.keycloak.authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IsStagUser(
    @JsonProperty("jmeno") String name,
    @JsonProperty("prijmeni") String lastname,
    @JsonProperty("stagUserInfo") List<IsStagUserDetails> stagUserInfo
) {

}
