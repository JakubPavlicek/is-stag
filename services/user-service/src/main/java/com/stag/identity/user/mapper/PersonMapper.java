package com.stag.identity.user.mapper;

import com.stag.identity.user.dto.AddressesDTO;
import com.stag.identity.user.dto.PersonProfileDTO;
import com.stag.identity.user.model.Addresses;
import com.stag.identity.user.model.Addresses.Address;
import com.stag.identity.user.model.Addresses.ForeignAddress;
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
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public PersonAddresses toPersonAddresses(Addresses addresses, PersonAddressData personAddressData) {
        return PersonAddresses.builder()
                              .permanentAddress(buildPersonAddress(
                                  addresses.permanentAddress(),
                                  personAddressData.getPermanentMunicipality(),
                                  personAddressData.getPermanentMunicipalityPart(),
                                  personAddressData.getPermanentDistrict(),
                                  personAddressData.getPermanentCountry()
                              ))
                              .temporaryAddress(buildPersonAddress(
                                  addresses.temporaryAddress(),
                                  personAddressData.getTemporaryMunicipality(),
                                  personAddressData.getTemporaryMunicipalityPart(),
                                  personAddressData.getTemporaryDistrict(),
                                  personAddressData.getTemporaryCountry()
                              ))
                              .foreignPermanentAddress(addresses.foreignPermanentAddress())
                              .foreignTemporaryAddress(addresses.foreignTemporaryAddress())
                              .build();
    }

    private PersonAddress buildPersonAddress(
        Address address,
        String municipality,
        String municipalityPart,
        String district,
        String country
    ) {
        return PersonAddress.builder()
                            .street(address.street())
                            .streetNumber(address.streetNumber())
                            .zipCode(address.zipCode())
                            .municipality(municipality)
                            .municipalityPart(municipalityPart)
                            .district(district)
                            .country(country)
                            .build();
    }

    // --- Address Projection Mapping ---
    @Mapping(source = "projection", target = "permanentAddress", qualifiedByName = "mapPermanentAddress")
    @Mapping(source = "projection", target = "temporaryAddress", qualifiedByName = "mapTemporaryAddress")
    @Mapping(source = "projection", target = "foreignPermanentAddress", qualifiedByName = "mapForeignPermanentAddress")
    @Mapping(source = "projection", target = "foreignTemporaryAddress", qualifiedByName = "mapForeignTemporaryAddress")
    public abstract Addresses toAddresses(PersonAddressProjection projection);

    @Named("mapPermanentAddress")
    @Mapping(source = "permanentStreet", target = "street")
    @Mapping(source = "permanentStreetNumber", target = "streetNumber")
    @Mapping(source = "permanentZipCode", target = "zipCode")
    @Mapping(source = "permanentMunicipalityId", target = "municipalityId")
    @Mapping(source = "permanentMunicipalityPartId", target = "municipalityPartId")
    @Mapping(source = "permanentDistrictId", target = "districtId")
    @Mapping(source = "permanentCountryId", target = "countryId")
    abstract Address mapPermanentAddress(PersonAddressProjection projection);

    @Named("mapTemporaryAddress")
    @Mapping(source = "temporaryStreet", target = "street")
    @Mapping(source = "temporaryStreetNumber", target = "streetNumber")
    @Mapping(source = "temporaryZipCode", target = "zipCode")
    @Mapping(source = "temporaryMunicipalityId", target = "municipalityId")
    @Mapping(source = "temporaryMunicipalityPartId", target = "municipalityPartId")
    @Mapping(source = "temporaryDistrictId", target = "districtId")
    @Mapping(source = "temporaryCountryId", target = "countryId")
    abstract Address mapTemporaryAddress(PersonAddressProjection projection);

    @Named("mapForeignPermanentAddress")
    @Mapping(source = "foreignPermanentZipCode", target = "zipCode")
    @Mapping(source = "foreignPermanentMunicipality", target = "municipality")
    @Mapping(source = "foreignPermanentDistrict", target = "district")
    @Mapping(source = "foreignPermanentPostOffice", target = "postOffice")
    abstract ForeignAddress mapForeignPermanentAddress(PersonAddressProjection projection);

    @Named("mapForeignTemporaryAddress")
    @Mapping(source = "foreignTemporaryZipCode", target = "zipCode")
    @Mapping(source = "foreignTemporaryMunicipality", target = "municipality")
    @Mapping(source = "foreignTemporaryDistrict", target = "district")
    @Mapping(source = "foreignTemporaryPostOffice", target = "postOffice")
    abstract ForeignAddress mapForeignTemporaryAddress(PersonAddressProjection projection);

}
