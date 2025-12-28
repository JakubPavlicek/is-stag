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

/// **Profile Mapper**
///
/// MapStruct mapper for transforming profile projections to domain models.
/// Enriches profiles with localized codelist values and student IDs using
/// CodelistValueResolver for codelist lookups.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(uses = { CodelistValueResolver.class })
public interface ProfileMapper {

    /// ProfileMapper Instance
    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    /// Maps profile projection to a full profile model with enriched data.
    /// Resolves localized gender, marital status, titles, birthplace, and citizenship.
    ///
    /// @param profile the profile projection from a database
    /// @param studentIds the list of associated student IDs
    /// @param data the enriched codelist lookup data
    /// @return complete profile with localized codelist values
    @Mapping(target = "personId", source = "profile.id")
    @Mapping(target = "studentIds", source = "studentIds")
    @Mapping(target = "birthSurname", source = "profile.birthSurname")
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

    /// Maps contact information from the profile projection.
    Profile.Contact toContact(ProfileView profile);

    /// Maps titles with localized prefix and suffix values.
    @Mapping(target = "prefix", source = "titlePrefix", qualifiedByName = "lookupTitlePrefix")
    @Mapping(target = "suffix", source = "titleSuffix", qualifiedByName = "lookupTitleSuffix")
    Profile.Titles toTitles(
        ProfileView profile,
        @Context ProfileLookupData data
    );

    /// Maps birthplace with resolved country name.
    @Mapping(target = "city", source = "birthPlace")
    @Mapping(target = "country", source = "profile", qualifiedByName = "birthCountryName")
    Profile.BirthPlace toBirthPlace(
        ProfileView profile,
        @Context ProfileLookupData data
    );

    /// Maps citizenship with resolved country name and qualifier.
    @Mapping(target = "country", source = "profile", qualifiedByName = "citizenshipCountryName")
    @Mapping(target = "qualifier", source = "citizenshipQualification", qualifiedByName = "lookupCitizenshipQualifier")
    Profile.Citizenship toCitizenship(
        ProfileView profile,
        @Context ProfileLookupData data
    );

    /// Resolves birth country name from lookup data.
    @Named("birthCountryName")
    default String birthCountryName(
        ProfileView profile,
        @Context ProfileLookupData data
    ) {
        return data.birthCountryName();
    }

    /// Resolves citizenship country name from lookup data.
    @Named("citizenshipCountryName")
    default String citizenshipCountryName(
        ProfileView profile,
        @Context ProfileLookupData data
    ) {
        return data.citizenshipCountryName();
    }

    /// Maps a simple profile projection to a simple profile model with localized gender.
    ///
    /// @param simpleProfile the simple profile projection from a database
    /// @param data the enriched codelist lookup data
    /// @return simple profile with localized codelist values
    @Mapping(target = "titles", source = "simpleProfile")
    @Mapping(target = "gender", source = "simpleProfile.gender", qualifiedByName = "lookupCodelistGender")
    SimpleProfile toSimplePersonProfile(
        SimpleProfileView simpleProfile,
        @Context CodelistMeaningsLookupData data
    );

    /// Maps titles from a simple profile with localized prefix and suffix.
    @Mapping(target = "prefix", source = "titlePrefix", qualifiedByName = "lookupCodelistTitlePrefix")
    @Mapping(target = "suffix", source = "titleSuffix", qualifiedByName = "lookupCodelistTitleSuffix")
    Profile.Titles toTitles(
        SimpleProfileView projection,
        @Context CodelistMeaningsLookupData data
    );

}
