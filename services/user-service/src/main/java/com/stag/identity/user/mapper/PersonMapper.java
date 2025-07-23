package com.stag.identity.user.mapper;

import com.stag.identity.user.model.BirthPlace;
import com.stag.identity.user.model.Citizenship;
import com.stag.identity.user.model.CodelistEntryId;
import com.stag.identity.user.model.Contact;
import com.stag.identity.user.dto.PersonProfileDTO;
import com.stag.identity.user.service.data.PersonProfileData;
import com.stag.identity.user.model.PersonProfile;
import com.stag.identity.user.model.Titles;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class PersonMapper {

    // --- API Mapping Methods ---

    @Mapping(source = "personId", target = "personId")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "birthSurname", target = "birthSurname")
    @Mapping(source = "birthNumber", target = "birthNumber")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "passportNumber", target = "passportNumber")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "maritalStatus", target = "maritalStatus")
    @Mapping(source = "personalNumbers", target = "personalNumbers")
    @Mapping(source = "contact", target = "contact")
    @Mapping(source = "titles", target = "titles")
    @Mapping(source = "birthPlace", target = "birthPlace")
    @Mapping(source = "citizenship", target = "citizenship")
    public abstract PersonProfileDTO toPersonProfileDTO(PersonProfile personProfile);

    // --- Main Mapping Method ---

    @Mapping(source = "personProfile.id", target = "personId")
    @Mapping(source = "personProfile.firstName", target = "firstName")
    @Mapping(source = "personProfile.lastName", target = "lastName")
    @Mapping(source = "personProfile.birthName", target = "birthSurname")
    @Mapping(source = "personProfile.birthNumber", target = "birthNumber")
    @Mapping(source = "personProfile.birthDate", target = "birthDate")
    @Mapping(source = "personProfile.passportNumber", target = "passportNumber")
    @Mapping(source = "personProfile", target = "contact")
    @Mapping(source = "personalNumbers", target = "personalNumbers")
    @Mapping(target = "gender", ignore = true) // Handled in @AfterMapping
    @Mapping(target = "maritalStatus", ignore = true) // Handled in @AfterMapping
    @Mapping(target = "titles", ignore = true) // Handled in @AfterMapping
    @Mapping(target = "birthPlace", ignore = true) // Handled in @AfterMapping
    @Mapping(target = "citizenship", ignore = true) // Handled in @AfterMapping
    public abstract PersonProfile toPersonProfile(
        PersonProfileProjection personProfile,
        List<String> personalNumbers,
        @Context PersonProfileData codelistData
    );

    // --- Delegate Mappers for Nested Objects ---

    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "mobile", target = "mobile")
    public abstract Contact toContact(PersonProfileProjection personProfile);

    @AfterMapping
    void afterMapping(
        PersonProfileProjection personProfileProjection,
        @MappingTarget PersonProfile personProfile,
        @Context PersonProfileData codelistData
    ) {
        Map<CodelistEntryId, String> meanings = codelistData.getCodelistMeanings();

        personProfile.setGender(lookupCodelistValue("POHLAVI", personProfileProjection.gender(), meanings));
        personProfile.setMaritalStatus(lookupCodelistValue("STAV", personProfileProjection.maritalStatus(), meanings));

        personProfile.setTitles(
            new Titles(
                lookupCodelistValue("TITUL_PRED", personProfileProjection.titlePrefix(), meanings),
                lookupCodelistValue("TITUL_ZA", personProfileProjection.titleSuffix(), meanings)
            ));

        personProfile.setBirthPlace(
            new BirthPlace(
                personProfileProjection.birthPlace(),
                codelistData.getBirthCountryName()
            ));

        personProfile.setCitizenship(
            new Citizenship(
                codelistData.getCitizenshipCountryName(),
                lookupCodelistValue("KVANT_OBCAN", personProfileProjection.citizenshipQualification(), meanings)
            ));
    }

    private String lookupCodelistValue(String domain, String lowValue, Map<CodelistEntryId, String> meanings) {
        if (lowValue == null) {
            return null;
        }
        CodelistEntryId key = new CodelistEntryId(domain, lowValue);
        return meanings.get(key);
    }

}
