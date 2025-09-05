package com.stag.identity.person.mapper;

import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.service.data.AddressLookupData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { CodelistValueResolver.class })
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(target = "permanentAddress", source = "personAddress", qualifiedByName = "toPermanentAddress")
    @Mapping(target = "temporaryAddress", source = "personAddress", qualifiedByName = "toTemporaryAddress")
    @Mapping(target = "foreignPermanentAddress", source = "personAddress", qualifiedByName = "toForeignPermanentAddress")
    @Mapping(target = "foreignTemporaryAddress", source = "personAddress", qualifiedByName = "toForeignTemporaryAddress")
    Addresses toPersonAddresses(
        AddressView personAddress,
        @Context AddressLookupData data
    );

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

    @Named("toForeignPermanentAddress")
    @Mapping(target = "zipCode", source = "foreignPermanentZipCode")
    @Mapping(target = "municipality", source = "foreignPermanentMunicipality")
    @Mapping(target = "district", source = "foreignPermanentDistrict")
    @Mapping(target = "postOffice", source = "foreignPermanentPostOffice")
    Addresses.ForeignAddress toForeignPermanentAddress(AddressView personAddress);

    @Named("toForeignTemporaryAddress")
    @Mapping(target = "zipCode", source = "foreignTemporaryZipCode")
    @Mapping(target = "municipality", source = "foreignTemporaryMunicipality")
    @Mapping(target = "district", source = "foreignTemporaryDistrict")
    @Mapping(target = "postOffice", source = "foreignTemporaryPostOffice")
    Addresses.ForeignAddress toForeignTemporaryAddress(AddressView personAddress);

    @Named("permanentMunicipalityName")
    default String permanentMunicipalityName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.permanentMunicipality();
    }

    @Named("permanentMunicipalityPartName")
    default String permanentMunicipalityPartName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.permanentMunicipalityPart();
    }

    @Named("permanentDistrictName")
    default String permanentDistrictName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.permanentDistrict();
    }

    @Named("permanentCountryName")
    default String permanentCountryName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.permanentCountry();
    }

    @Named("temporaryMunicipalityName")
    default String temporaryMunicipalityName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.temporaryMunicipality();
    }

    @Named("temporaryMunicipalityPartName")
    default String temporaryMunicipalityPartName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.temporaryMunicipalityPart();
    }

    @Named("temporaryDistrictName")
    default String temporaryDistrictName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.temporaryDistrict();
    }

    @Named("temporaryCountryName")
    default String temporaryCountryName(
        AddressView personAddress,
        @Context AddressLookupData data
    ) {
        return data.temporaryCountry();
    }
}