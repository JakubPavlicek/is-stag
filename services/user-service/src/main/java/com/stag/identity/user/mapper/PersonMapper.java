package com.stag.identity.user.mapper;

import com.stag.identity.user.dto.AddressesDTO;
import com.stag.identity.user.model.Address;
import com.stag.identity.user.model.Addresses;
import com.stag.identity.user.model.BirthPlace;
import com.stag.identity.user.model.Citizenship;
import com.stag.identity.user.model.CodelistEntryId;
import com.stag.identity.user.model.Contact;
import com.stag.identity.user.dto.PersonProfileDTO;
import com.stag.identity.user.model.ForeignAddress;
import com.stag.identity.user.repository.projection.ForeignAddressProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonForeignAddressData;
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

    public Addresses mapToAddresses(PersonAddressData addressData, PersonForeignAddressData foreignAddressData) {
        Address permanentAddress = getPermanentAddress(addressData);
        Address temporaryAddress = getTemporaryAddress(addressData);
        ForeignAddress foreignPermanentAddress = getForeignAddress(foreignAddressData.getForeignPermanentAddress());
        ForeignAddress foreignTemporaryAddress = getForeignAddress(foreignAddressData.getForeignTemporaryAddress());

        return Addresses.builder()
                        .permanentResidence(permanentAddress)
                        .temporaryResidence(temporaryAddress)
                        .foreignPermanentResidence(foreignPermanentAddress)
                        .foreignTemporaryResidence(foreignTemporaryAddress)
                        .build();
    }

    private Address getPermanentAddress(PersonAddressData addressData) {
        return Address.builder()
                      .street(addressData.getPermanentStreet())
                      .streetNumber(addressData.getPermanentStreetNumber())
                      .zipCode(addressData.getPermanentZipCode())
                      .municipality(addressData.getPermanentMunicipality())
                      .municipalityPart(addressData.getPermanentMunicipalityPart())
                      .district(addressData.getPermanentDistrict())
                      .country(addressData.getPermanentCountry())
                      .build();
    }

    private Address getTemporaryAddress(PersonAddressData addressData) {
        return Address.builder()
                      .street(addressData.getTemporaryStreet())
                      .streetNumber(addressData.getTemporaryStreetNumber())
                      .zipCode(addressData.getTemporaryZipCode())
                      .municipality(addressData.getTemporaryMunicipality())
                      .municipalityPart(addressData.getTemporaryMunicipalityPart())
                      .district(addressData.getTemporaryDistrict())
                      .country(addressData.getTemporaryCountry())
                      .build();
    }

    private ForeignAddress getForeignAddress(ForeignAddressProjection foreignAddress) {
        return ForeignAddress.builder()
                             .zipCode(foreignAddress.zipCode())
                             .municipality(foreignAddress.municipality())
                             .district(foreignAddress.district())
                             .postOffice(foreignAddress.postOffice())
                             .build();
    }

    @Mapping(source = "permanentResidence", target = "permanentResidence")
    @Mapping(source = "temporaryResidence", target = "temporaryResidence")
    @Mapping(source = "foreignPermanentResidence", target = "foreignPermanentResidence")
    @Mapping(source = "foreignTemporaryResidence", target = "foreignTemporaryResidence")
    public abstract AddressesDTO toAddressesDTO(Addresses personAddresses);

}
