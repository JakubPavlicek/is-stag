package com.stag.identity.person.mapper;

import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.service.data.AddressLookupData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/// **Address Mapper**
///
/// MapStruct mapper for transforming address projections to domain models.
/// Enriches Czech addresses with localized country, district, and municipality names.
/// Handles both domestic (permanent/temporary) and foreign addresses.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(uses = { CodelistValueResolver.class })
public interface AddressMapper {

    /// AddressMapper Instance
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    /// Maps address projection to complete addresses model with all address types.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data with localized names
    /// @return addresses model with Czech and foreign addresses
    @Mapping(target = "permanentAddress", source = "personAddress", qualifiedByName = "toPermanentAddress")
    @Mapping(target = "temporaryAddress", source = "personAddress", qualifiedByName = "toTemporaryAddress")
    @Mapping(target = "foreignPermanentAddress", source = "personAddress", qualifiedByName = "toForeignPermanentAddress")
    @Mapping(target = "foreignTemporaryAddress", source = "personAddress", qualifiedByName = "toForeignTemporaryAddress")
    Addresses toPersonAddresses(
        AddressView personAddress,
        @Context AddressLookupData data
    );

    /// Maps permanent Czech address with localized location names.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data
    /// @return permanent address model
    @Named("toPermanentAddress")
    @Mapping(target = "street", source = "permanentStreet")
    @Mapping(target = "streetNumber", source = "permanentStreetNumber")
    @Mapping(target = "zipCode", source = "permanentZipCode")
    @Mapping(target = "municipality", source = "personAddress", qualifiedByName = "permanentMunicipalityName")
    @Mapping(target = "municipalityPart", source = "personAddress", qualifiedByName = "permanentMunicipalityPartName")
    @Mapping(target = "district", source = "personAddress", qualifiedByName = "permanentDistrictName")
    @Mapping(target = "country", source = "personAddress", qualifiedByName = "permanentCountryName")
    Addresses.Address toPermanentAddress(
        AddressView personAddress,
        @Context AddressLookupData data
    );

    /// Maps temporary Czech address with localized location names.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data
    /// @return temporary address model
    @Named("toTemporaryAddress")
    @Mapping(target = "street", source = "temporaryStreet")
    @Mapping(target = "streetNumber", source = "temporaryStreetNumber")
    @Mapping(target = "zipCode", source = "temporaryZipCode")
    @Mapping(target = "municipality", source = "personAddress", qualifiedByName = "temporaryMunicipalityName")
    @Mapping(target = "municipalityPart", source = "personAddress", qualifiedByName = "temporaryMunicipalityPartName")
    @Mapping(target = "district", source = "personAddress", qualifiedByName = "temporaryDistrictName")
    @Mapping(target = "country", source = "personAddress", qualifiedByName = "temporaryCountryName")
    Addresses.Address toTemporaryAddress(
        AddressView personAddress,
        @Context AddressLookupData data
    );

    /// Maps foreign permanent address with manually entered location data.
    ///
    /// @param personAddress the address projection
    /// @return foreign permanent address model
    @Named("toForeignPermanentAddress")
    @Mapping(target = "zipCode", source = "foreignPermanentZipCode")
    @Mapping(target = "municipality", source = "foreignPermanentMunicipality")
    @Mapping(target = "district", source = "foreignPermanentDistrict")
    @Mapping(target = "postOffice", source = "foreignPermanentPostOffice")
    Addresses.ForeignAddress toForeignPermanentAddress(AddressView personAddress);

    /// Maps foreign temporary address with manually entered location data.
    ///
    /// @param personAddress the address projection
    /// @return foreign temporary address model
    @Named("toForeignTemporaryAddress")
    @Mapping(target = "zipCode", source = "foreignTemporaryZipCode")
    @Mapping(target = "municipality", source = "foreignTemporaryMunicipality")
    @Mapping(target = "district", source = "foreignTemporaryDistrict")
    @Mapping(target = "postOffice", source = "foreignTemporaryPostOffice")
    Addresses.ForeignAddress toForeignTemporaryAddress(AddressView personAddress);

    /// Resolves permanent municipality name from lookup data.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data containing localized municipality names
    /// @return the permanent municipality name
    @Named("permanentMunicipalityName")
    default String permanentMunicipalityName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.permanentMunicipality();
    }

    /// Resolves permanent municipality part name from lookup data.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data containing localized municipality part names
    /// @return the permanent municipality part name
    @Named("permanentMunicipalityPartName")
    default String permanentMunicipalityPartName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.permanentMunicipalityPart();
    }

    /// Resolves permanent district name from lookup data.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data containing localized district names
    /// @return the permanent district name
    @Named("permanentDistrictName")
    default String permanentDistrictName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.permanentDistrict();
    }

    /// Resolves permanent country name from lookup data.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data containing localized country names
    /// @return the permanent country name
    @Named("permanentCountryName")
    default String permanentCountryName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.permanentCountry();
    }

    /// Resolves temporary municipality name from lookup data.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data containing localized municipality names
    /// @return the temporary municipality name
    @Named("temporaryMunicipalityName")
    default String temporaryMunicipalityName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.temporaryMunicipality();
    }

    /// Resolves temporary municipality part name from lookup data.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data containing localized municipality part names
    /// @return the temporary municipality part name
    @Named("temporaryMunicipalityPartName")
    default String temporaryMunicipalityPartName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.temporaryMunicipalityPart();
    }

    /// Resolves temporary district name from lookup data.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data containing localized district names
    /// @return the temporary district name
    @Named("temporaryDistrictName")
    default String temporaryDistrictName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.temporaryDistrict();
    }

    /// Resolves temporary country name from lookup data.
    ///
    /// @param personAddress the address projection
    /// @param data the address lookup data containing localized country names
    /// @return the temporary country name
    @Named("temporaryCountryName")
    default String temporaryCountryName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.temporaryCountry();
    }

}