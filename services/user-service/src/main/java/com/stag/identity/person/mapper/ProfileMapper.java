package com.stag.identity.person.mapper;

import com.stag.identity.person.model.Profile;
import com.stag.identity.person.model.SimpleProfile;
import com.stag.identity.person.repository.projection.ProfileView;
import com.stag.identity.person.repository.projection.SimpleProfileView;
import com.stag.identity.person.service.data.CodelistMeaningsLookupData;
import com.stag.identity.person.service.data.ProfileLookupData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = { CodelistValueResolver.class })
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    @Mapping(target = "personId", source = "profile.id")
    @Mapping(target = "studentIds", source = "studentIds")
    @Mapping(target = "birthSurname", source = "profile.birthName")
    @Mapping(target = "gender", source = "profile.gender", qualifiedByName = "lookupGender")
    @Mapping(target = "maritalStatus", source = "profile.maritalStatus", qualifiedByName = "lookupMaritalStatus")
    @Mapping(target = "contact", source = "profile")
    @Mapping(target = "titles", source = "profile")
    @Mapping(target = "birthPlace", source = "profile")
    @Mapping(target = "citizenship", source = "profile")
    Profile toPersonProfile(
        ProfileView profile,
        List<String> studentIds,
        @Context ProfileLookupData data
    );

    Profile.Contact toContact(ProfileView profile);

    @Mapping(target = "prefix", source = "titlePrefix", qualifiedByName = "lookupTitlePrefix")
    @Mapping(target = "suffix", source = "titleSuffix", qualifiedByName = "lookupTitleSuffix")
    Profile.Titles toTitles(
        ProfileView profile,
        @Context ProfileLookupData data
    );

    @Mapping(target = "city", source = "birthPlace")
    @Mapping(target = "country", source = "profile", qualifiedByName = "birthCountryName")
    Profile.BirthPlace toBirthPlace(
        ProfileView profile,
        @Context ProfileLookupData data
    );

    @Mapping(target = "country", source = "profile", qualifiedByName = "citizenshipCountryName")
    @Mapping(target = "qualifier", source = "citizenshipQualification", qualifiedByName = "lookupCitizenshipQualifier")
    Profile.Citizenship toCitizenship(
        ProfileView profile,
        @Context ProfileLookupData data
    );

    @Named("birthCountryName")
    default String birthCountryName(
        ProfileView profile,
        @Context ProfileLookupData data
    ) {
        return data.birthCountryName();
    }

    @Named("citizenshipCountryName")
    default String citizenshipCountryName(
        ProfileView profile,
        @Context ProfileLookupData data
    ) {
        return data.citizenshipCountryName();
    }

    @Mapping(target = "titles", source = "simpleProfile")
    @Mapping(target = "gender", source = "simpleProfile.gender", qualifiedByName = "lookupCodelistGender")
    SimpleProfile toSimplePersonProfile(
        SimpleProfileView simpleProfile,
        @Context CodelistMeaningsLookupData data
    );

    @Mapping(target = "prefix", source = "titlePrefix", qualifiedByName = "lookupCodelistTitlePrefix")
    @Mapping(target = "suffix", source = "titleSuffix", qualifiedByName = "lookupCodelistTitleSuffix")
    Profile.Titles toTitles(
        SimpleProfileView projection,
        @Context CodelistMeaningsLookupData data
    );

}
