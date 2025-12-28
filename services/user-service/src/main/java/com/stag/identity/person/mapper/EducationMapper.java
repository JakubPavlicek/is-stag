package com.stag.identity.person.mapper;

import com.stag.identity.person.model.Education;
import com.stag.identity.person.repository.projection.EducationView;
import com.stag.identity.person.service.data.EducationLookupData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/// **Education Mapper**
///
/// MapStruct mapper for transforming education projections to domain models.
/// Enriches education history with localized field of study names and school locations.
/// Handles both Czech high schools with full addresses and foreign high schools.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(uses = { CodelistValueResolver.class })
public interface EducationMapper {

    /// EducationMapper Instance
    EducationMapper INSTANCE = Mappers.getMapper(EducationMapper.class);

    /// Maps education projection to complete education model.
    ///
    /// @param personEducation the education projection
    /// @param data the education lookup data
    /// @return education model with Czech or foreign high school
    @Mapping(target = "highSchool", source = "personEducation", qualifiedByName = "toHighSchool")
    @Mapping(target = "foreignHighSchool", source = "personEducation", qualifiedByName = "toForeignHighSchool")
    Education toPersonEducation(
        EducationView personEducation,
        @Context EducationLookupData data
    );

    /// Maps Czech high school with full address information.
    ///
    /// @param personEducation the education projection
    /// @param data the education lookup data
    /// @return Czech high school model
    @Named("toHighSchool")
    @Mapping(target = "name", source = "personEducation", qualifiedByName = "highSchoolName")
    @Mapping(target = "fieldOfStudy", source = "personEducation", qualifiedByName = "highSchoolFieldOfStudy")
    @Mapping(target = "address", source = "personEducation", qualifiedByName = "toHighSchoolAddressFromContext")
    Education.HighSchool toHighSchool(
        EducationView personEducation,
        @Context EducationLookupData data
    );

    /// Maps foreign high school with name and location.
    ///
    /// @param personEducation the education projection
    /// @param data the education lookup data
    /// @return foreign high school model
    @Named("toForeignHighSchool")
    @Mapping(target = "name", source = "highSchoolForeign")
    @Mapping(target = "location", source = "highSchoolForeignPlace")
    @Mapping(target = "fieldOfStudy", source = "personEducation", qualifiedByName = "highSchoolFieldOfStudy")
    Education.ForeignHighSchool toForeignHighSchool(
        EducationView personEducation,
        @Context EducationLookupData data
    );

    /// Maps high school address from lookup data.
    ///
    /// @param data the education lookup data
    /// @return high school address model
    @Named("toHighSchoolAddress")
    @Mapping(target = "street", source = "highSchoolStreet")
    @Mapping(target = "zipCode", source = "highSchoolZipCode")
    @Mapping(target = "municipality", source = "highSchoolMunicipalityName")
    @Mapping(target = "district", source = "highSchoolDistrictName")
    @Mapping(target = "country", source = "highSchoolCountryName")
    Education.HighSchoolAddress toHighSchoolAddress(EducationLookupData data);

    /// Resolves high school name from lookup data.
    @Named("highSchoolName")
    default String highSchoolName(
        EducationView personEducation,
        @Context EducationLookupData data
    ) {
        return data.highSchoolName();
    }

    /// Resolves high school field of study from lookup data.
    @Named("highSchoolFieldOfStudy")
    default String highSchoolFieldOfStudy(
        EducationView personEducation,
        @Context EducationLookupData data
    ) {
        return data.highSchoolFieldOfStudy();
    }

    /// Converts lookup data to high school address using named mapper method.
    @Named("toHighSchoolAddressFromContext")
    default Education.HighSchoolAddress toHighSchoolAddressFromContext(
        EducationView personEducation,
        @Context EducationLookupData data
    ) {
        return toHighSchoolAddress(data);
    }

}
