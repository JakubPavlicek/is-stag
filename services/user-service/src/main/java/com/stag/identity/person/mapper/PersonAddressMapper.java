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
    @Mapping(target = "street", source = "personAddress.permanentStreet")
    @Mapping(target = "streetNumber", source = "personAddress.permanentStreetNumber")
    @Mapping(target = "zipCode", source = "personAddress.permanentZipCode")
    @Mapping(target = "municipality", expression = "java(data.permanentMunicipality())")
    @Mapping(target = "municipalityPart", expression = "java(data.permanentMunicipalityPart())")
    @Mapping(target = "district", expression = "java(data.permanentDistrict())")
    @Mapping(target = "country", expression = "java(data.permanentCountry())")
    PersonAddresses.Address toPermanentAddress(
        PersonAddressProjection personAddress,
        @Context PersonAddressData data
    );

    @Named("toTemporaryAddress")
    @Mapping(target = "street", source = "personAddress.temporaryStreet")
    @Mapping(target = "streetNumber", source = "personAddress.temporaryStreetNumber")
    @Mapping(target = "zipCode", source = "personAddress.temporaryZipCode")
    @Mapping(target = "municipality", expression = "java(data.temporaryMunicipality())")
    @Mapping(target = "municipalityPart", expression = "java(data.temporaryMunicipalityPart())")
    @Mapping(target = "district", expression = "java(data.temporaryDistrict())")
    @Mapping(target = "country", expression = "java(data.temporaryCountry())")
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
}