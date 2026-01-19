package com.stag.keycloak.authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a user in the IS/STAG system.
 * <p>
 * This record is used for deserializing user information received from the IS/STAG authentication service.
 * It contains the user's basic personal information and a list of detailed user attributes.
 * </p>
 *
 * @param firstName The first name of the user (mapped from "jmeno").
 * @param lastName The last name of the user (mapped from "prijmeni").
 * @param stagUserInfo A list of {@link IsStagUserDetails} containing additional user details.
 * @author Jakub Pavlíček
 * @version 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IsStagUser(
    @JsonProperty("jmeno") String firstName,
    @JsonProperty("prijmeni") String lastName,
    @JsonProperty("stagUserInfo") List<IsStagUserDetails> stagUserInfo
) {

}
