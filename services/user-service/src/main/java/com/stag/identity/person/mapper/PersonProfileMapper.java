package com.stag.identity.person.mapper;

import com.stag.identity.person.model.PersonProfile;
import com.stag.identity.person.repository.projection.PersonProfileProjection;
import com.stag.identity.person.service.data.PersonProfileData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = { Qualifiers.class })
public interface PersonProfileMapper {

    PersonProfileMapper INSTANCE = Mappers.getMapper(PersonProfileMapper.class);

    @Mapping(target = "personId", source = "profile.id")
    @Mapping(target = "personalNumbers", source = "personalNumbers")
    @Mapping(target = "birthSurname", source = "profile.birthName")
    @Mapping(target = "gender", source = "profile.gender", qualifiedByName = "lookupGender")
    @Mapping(target = "maritalStatus", source = "profile.maritalStatus", qualifiedByName = "lookupMaritalStatus")
    @Mapping(target = "contact", source = "profile")
    @Mapping(target = "titles", source = "profile")
    @Mapping(target = "birthPlace", source = "profile")
    @Mapping(target = "citizenship", source = "profile")
    PersonProfile toPersonProfile(
        PersonProfileProjection profile,
        List<String> personalNumbers,
        @Context PersonProfileData data
    );

    PersonProfile.Contact toContact(PersonProfileProjection profile);

    @Mapping(target = "prefix", source = "titlePrefix", qualifiedByName = "lookupTitlePrefix")
    @Mapping(target = "suffix", source = "titleSuffix", qualifiedByName = "lookupTitleSuffix")
    PersonProfile.Titles toTitles(PersonProfileProjection projection, @Context PersonProfileData data);

    @Mapping(target = "city", source = "birthPlace")
    @Mapping(target = "country", expression = "java(data.birthCountryName())")
    PersonProfile.BirthPlace toBirthPlace(PersonProfileProjection projection, @Context PersonProfileData data);

    @Mapping(target = "country", expression = "java(data.citizenshipCountryName())")
    @Mapping(target = "qualifier", source = "citizenshipQualification", qualifiedByName = "lookupCitizenshipQualifier")
    PersonProfile.Citizenship toCitizenship(PersonProfileProjection projection, @Context PersonProfileData data);
}