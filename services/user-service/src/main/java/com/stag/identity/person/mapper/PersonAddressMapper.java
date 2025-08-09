package com.stag.identity.person.mapper;

import com.stag.identity.person.model.PersonAddresses;
import com.stag.identity.person.repository.projection.PersonAddressProjection;
import com.stag.identity.person.service.data.PersonAddressData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { Qualifiers.class })
public interface PersonAddressMapper {

    PersonAddressMapper INSTANCE = Mappers.getMapper(PersonAddressMapper.class);

    @Mapping(target = "permanentAddress", source = "personAddress", qualifiedByName = "toPermanentAddress")
    @Mapping(target = "temporaryAddress", source = "personAddress", qualifiedByName = "toTemporaryAddress")
    @Mapping(target = "foreignPermanentAddress", source = "personAddress", qualifiedByName = "toForeignPermanentAddress")
    @Mapping(target = "foreignTemporaryAddress", source = "personAddress", qualifiedByName = "toForeignTemporaryAddress")
    PersonAddresses toPersonAddresses(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    );

    @Named("toPermanentAddress")
    @Mapping(target = "street", source = "permanentStreet")
    @Mapping(target = "streetNumber", source = "permanentStreetNumber")
    @Mapping(target = "zipCode", source = "permanentZipCode")
    @Mapping(target = "municipality", source = "personAddress", qualifiedByName = "permanentMunicipalityName")
    @Mapping(target = "municipalityPart", source = "personAddress", qualifiedByName = "permanentMunicipalityPartName")
    @Mapping(target = "district", source = "personAddress", qualifiedByName = "permanentDistrictName")
    @Mapping(target = "country", source = "personAddress", qualifiedByName = "permanentCountryName")
    PersonAddresses.Address toPermanentAddress(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    );

    @Named("toTemporaryAddress")
    @Mapping(target = "street", source = "temporaryStreet")
    @Mapping(target = "streetNumber", source = "temporaryStreetNumber")
    @Mapping(target = "zipCode", source = "temporaryZipCode")
    @Mapping(target = "municipality", source = "personAddress", qualifiedByName = "temporaryMunicipalityName")
    @Mapping(target = "municipalityPart", source = "personAddress", qualifiedByName = "temporaryMunicipalityPartName")
    @Mapping(target = "district", source = "personAddress", qualifiedByName = "temporaryDistrictName")
    @Mapping(target = "country", source = "personAddress", qualifiedByName = "temporaryCountryName")
    PersonAddresses.Address toTemporaryAddress(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    );

    @Named("toForeignPermanentAddress")
    @Mapping(target = "zipCode", source = "foreignPermanentZipCode")
    @Mapping(target = "municipality", source = "foreignPermanentMunicipality")
    @Mapping(target = "district", source = "foreignPermanentDistrict")
    @Mapping(target = "postOffice", source = "foreignPermanentPostOffice")
    PersonAddresses.ForeignAddress toForeignPermanentAddress(PersonAddressProjection personAddress);

    @Named("toForeignTemporaryAddress")
    @Mapping(target = "zipCode", source = "foreignTemporaryZipCode")
    @Mapping(target = "municipality", source = "foreignTemporaryMunicipality")
    @Mapping(target = "district", source = "foreignTemporaryDistrict")
    @Mapping(target = "postOffice", source = "foreignTemporaryPostOffice")
    PersonAddresses.ForeignAddress toForeignTemporaryAddress(PersonAddressProjection personAddress);

    @Named("permanentMunicipalityName")
    default String permanentMunicipalityName(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    ) {
        return data.permanentMunicipality();
    }

    @Named("permanentMunicipalityPartName")
    default String permanentMunicipalityPartName(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    ) {
        return data.permanentMunicipalityPart();
    }

    @Named("permanentDistrictName")
    default String permanentDistrictName(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    ) {
        return data.permanentDistrict();
    }

    @Named("permanentCountryName")
    default String permanentCountryName(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    ) {
        return data.permanentCountry();
    }

    @Named("temporaryMunicipalityName")
    default String temporaryMunicipalityName(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    ) {
        return data.temporaryMunicipality();
    }

    @Named("temporaryMunicipalityPartName")
    default String temporaryMunicipalityPartName(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    ) {
        return data.temporaryMunicipalityPart();
    }

    @Named("temporaryDistrictName")
    default String temporaryDistrictName(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    ) {
        return data.temporaryDistrict();
    }

    @Named("temporaryCountryName")
    default String temporaryCountryName(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    ) {
        return data.temporaryCountry();
    }
}