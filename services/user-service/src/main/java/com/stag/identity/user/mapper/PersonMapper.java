package com.stag.identity.user.mapper;

import com.stag.identity.user.dto.BirthPlaceInternal;
import com.stag.identity.user.dto.CitizenshipInternal;
import com.stag.identity.user.dto.CodelistEntryId;
import com.stag.identity.user.dto.ContactInternal;
import com.stag.identity.user.dto.PersonProfile;
import com.stag.identity.user.dto.PersonProfileCodelistData;
import com.stag.identity.user.dto.PersonProfileInternal;
import com.stag.identity.user.dto.TitlesInternal;
import com.stag.identity.user.entity.Person;
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
    public abstract PersonProfile toPersonProfile(PersonProfileInternal internal);

    // --- Main Mapping Method ---

    @Mapping(source = "person.id", target = "personId")
    @Mapping(source = "person.name", target = "firstName")
    @Mapping(source = "person.surname", target = "lastName")
    @Mapping(source = "person.birthSurname", target = "birthSurname")
    @Mapping(source = "person.birthNumber", target = "birthNumber")
    @Mapping(source = "person.birthDate", target = "birthDate")
    @Mapping(source = "person.passportNumber", target = "passportNumber")
    @Mapping(source = "personalNumbers", target = "personalNumbers")
    @Mapping(target = "gender", ignore = true) // Handled in @AfterMapping
    @Mapping(target = "maritalStatus", ignore = true) // Handled in @AfterMapping
    @Mapping(target = "titles", ignore = true) // Handled in @AfterMapping
    @Mapping(source = "person", target = "contact")
    @Mapping(target = "birthPlace", ignore = true) // Handled in @AfterMapping
    @Mapping(target = "citizenship", ignore = true) // Handled in @AfterMapping
    public abstract PersonProfileInternal toPersonProfileInternal(
        Person person,
        List<String> personalNumbers,
        @Context PersonProfileCodelistData codelistData
    );

    // --- Delegate Mappers for Nested Objects ---

    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "mobile", target = "mobile")
    public abstract ContactInternal toContactInternal(Person person);

    @AfterMapping
    void afterMapping(
        Person person,
        @MappingTarget PersonProfileInternal personProfile,
        @Context PersonProfileCodelistData codelistData
    ) {
        Map<CodelistEntryId, String> meanings = codelistData.getCodelistMeanings();

        // Map titles - direct lookup
        TitlesInternal titles = TitlesInternal.builder()
                                              .prefix(lookupCodelistValue("TITUL_PRED", person.getTitlePrefix(), meanings))
                                              .suffix(lookupCodelistValue("TITUL_ZA", person.getTitleSuffix(), meanings))
                                              .build();
        personProfile.setTitles(titles);

        // Map other codelist values - direct lookup
        personProfile.setGender(lookupCodelistValue("POHLAVI", person.getGender(), meanings));
        personProfile.setMaritalStatus(lookupCodelistValue("STAV", person.getMaritalStatus(), meanings));

        // Map birth place with country name from gRPC
        BirthPlaceInternal birthPlace = BirthPlaceInternal.builder()
                                                          .city(person.getPlaceOfBirth())
                                                          .country(codelistData.getBirthCountryName())
                                                          .build();
        personProfile.setBirthPlace(birthPlace);

        // Map citizenship with country name from gRPC
        CitizenshipInternal citizenship = CitizenshipInternal.builder()
                                                             .country(codelistData.getCitizenshipCountryName())
                                                             .qualifier(lookupCodelistValue("KVANT_OBCAN", person.getCitizenshipQualification(), meanings))
                                                             .build();
        personProfile.setCitizenship(citizenship);                                                
    }

    /**
     * Simple helper method to lookup codelist values
     */
    private String lookupCodelistValue(String domain, String lowValue, Map<CodelistEntryId, String> meanings) {
        if (lowValue == null) {
            return null;
        }
        CodelistEntryId key = new CodelistEntryId(domain, lowValue);
        return meanings.get(key);
    }
}
