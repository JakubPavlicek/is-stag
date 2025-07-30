package com.stag.identity.user.mapper;

import com.stag.identity.user.dto.AddressesDTO;
import com.stag.identity.user.dto.PersonProfileDTO;
import com.stag.identity.user.model.CodelistDomain;
import com.stag.identity.user.model.CodelistEntryId;
import com.stag.identity.user.model.PersonAddresses;
import com.stag.identity.user.model.PersonAddresses.PersonAddress;
import com.stag.identity.user.model.PersonProfile;
import com.stag.identity.user.model.PersonProfile.BirthPlace;
import com.stag.identity.user.model.PersonProfile.Citizenship;
import com.stag.identity.user.model.PersonProfile.Contact;
import com.stag.identity.user.model.PersonProfile.Titles;
import com.stag.identity.user.repository.projection.PersonAddressProjection;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonProfileData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.stag.identity.user.model.PersonAddresses.PersonForeignAddress;

@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class PersonMapper {

    // --- API Mapping Methods ---
    public abstract PersonProfileDTO toPersonProfileDTO(PersonProfile personProfile);
    public abstract AddressesDTO toAddressesDTO(PersonAddresses personAddresses);

    // --- Main Mapping Method ---
    public PersonProfile toPersonProfile(
        PersonProfileProjection projection,
        List<String> personalNumbers,
        PersonProfileData codelistData
    ) {
        Map<CodelistEntryId, String> meanings = codelistData.getCodelistMeanings();

        return PersonProfile.builder()
                            .personId(projection.id())
                            .firstName(projection.firstName())
                            .lastName(projection.lastName())
                            .birthSurname(projection.birthName())
                            .birthNumber(projection.birthNumber())
                            .birthDate(projection.birthDate())
                            .passportNumber(projection.passportNumber())
                            .personalNumbers(personalNumbers)
                            .gender(lookupCodelistValue(CodelistDomain.POHLAVI, projection.gender(), meanings))
                            .maritalStatus(lookupCodelistValue(CodelistDomain.STAV, projection.maritalStatus(), meanings))
                            .contact(toContact(projection))
                            .titles(toTitles(projection, meanings))
                            .birthPlace(toBirthPlace(projection, codelistData))
                            .citizenship(toCitizenship(projection, codelistData, meanings))
                            .build();
    }

    // --- Nested Object Builders ---
    abstract Contact toContact(PersonProfileProjection projection);

    @Mapping(source = "projection.birthPlace", target = "city")
    @Mapping(source = "codelistData.birthCountryName", target = "country")
    abstract BirthPlace toBirthPlace(PersonProfileProjection projection, PersonProfileData codelistData);

    private Titles toTitles(PersonProfileProjection projection, Map<CodelistEntryId, String> meanings) {
        return new Titles(
            lookupCodelistValue(CodelistDomain.TITUL_PRED, projection.titlePrefix(), meanings),
            lookupCodelistValue(CodelistDomain.TITUL_ZA, projection.titleSuffix(), meanings)
        );
    }

    private Citizenship toCitizenship(
        PersonProfileProjection projection,
        PersonProfileData codelistData,
        Map<CodelistEntryId, String> meanings
    ) {
        return new Citizenship(
            codelistData.getCitizenshipCountryName(),
            lookupCodelistValue(CodelistDomain.KVANT_OBCAN, projection.citizenshipQualification(), meanings)
        );
    }

    private String lookupCodelistValue(CodelistDomain domain, String lowValue, Map<CodelistEntryId, String> meanings) {
        return Optional.ofNullable(lowValue)
                       .map(value -> meanings.get(new CodelistEntryId(domain.name(), value)))
                       .orElse(null);
    }

    // --- Address Mapping Methods ---
    public PersonAddresses toPersonAddresses(PersonAddressProjection personAddress, PersonAddressData personAddressData) {
        return PersonAddresses.builder()
                              .permanentAddress(buildPersonAddress(
                                  personAddress.permanentStreet(),
                                  personAddress.permanentStreetNumber(),
                                  personAddress.permanentZipCode(),
                                  personAddressData.getPermanentMunicipality(),
                                  personAddressData.getPermanentMunicipalityPart(),
                                  personAddressData.getPermanentDistrict(),
                                  personAddressData.getPermanentCountry()
                              ))
                              .temporaryAddress(buildPersonAddress(
                                  personAddress.temporaryStreet(),
                                  personAddress.temporaryStreetNumber(),
                                  personAddress.temporaryZipCode(),
                                  personAddressData.getTemporaryMunicipality(),
                                  personAddressData.getTemporaryMunicipalityPart(),
                                  personAddressData.getTemporaryDistrict(),
                                  personAddressData.getTemporaryCountry()
                              ))
                              .foreignPermanentAddress(buildPersonForeignAddress(
                                  personAddress.foreignPermanentZipCode(),
                                  personAddress.foreignPermanentMunicipality(),
                                  personAddress.foreignPermanentDistrict(),
                                  personAddress.foreignPermanentPostOffice()
                              ))
                              .foreignTemporaryAddress(buildPersonForeignAddress(
                                  personAddress.foreignTemporaryZipCode(),
                                  personAddress.foreignTemporaryMunicipality(),
                                  personAddress.foreignTemporaryDistrict(),
                                  personAddress.foreignTemporaryPostOffice()
                              ))
                              .build();
    }

    private PersonAddress buildPersonAddress(
        String street,
        String streetNumber,
        String zipCode,
        String municipality,
        String municipalityPart,
        String district,
        String country
    ) {
        return PersonAddress.builder()
                            .street(street)
                            .streetNumber(streetNumber)
                            .zipCode(zipCode)
                            .municipality(municipality)
                            .municipalityPart(municipalityPart)
                            .district(district)
                            .country(country)
                            .build();
    }

    private PersonForeignAddress buildPersonForeignAddress(
        String zipCode,
        String municipality,
        String district,
        String postOffice
    ) {
        return PersonForeignAddress.builder()
                                   .zipCode(zipCode)
                                   .municipality(municipality)
                                   .district(district)
                                   .postOffice(postOffice)
                                   .build();
    }

}
