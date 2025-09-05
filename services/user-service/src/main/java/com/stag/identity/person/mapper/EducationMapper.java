package com.stag.identity.person.mapper;

import com.stag.identity.person.model.Education;
import com.stag.identity.person.repository.projection.EducationView;
import com.stag.identity.person.service.data.EducationLookupData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { CodelistValueResolver.class })
public interface EducationMapper {

    EducationMapper INSTANCE = Mappers.getMapper(EducationMapper.class);

    @Mapping(target = "highSchool", source = "personEducation", qualifiedByName = "toHighSchool")
    @Mapping(target = "foreignHighSchool", source = "personEducation", qualifiedByName = "toForeignHighSchool")
    Education toPersonEducation(
        EducationView personEducation,
        @Context EducationLookupData data
    );

    @Named("toHighSchool")
    @Mapping(target = "name", source = "personEducation", qualifiedByName = "highSchoolName")
    @Mapping(target = "fieldOfStudy", source = "personEducation", qualifiedByName = "highSchoolFieldOfStudy")
    @Mapping(target = "address", source = "personEducation", qualifiedByName = "toHighSchoolAddressFromContext")
    Education.HighSchool toHighSchool(
        EducationView personEducation,
        @Context EducationLookupData data
    );

    @Named("toForeignHighSchool")
    @Mapping(target = "name", source = "highSchoolForeign")
    @Mapping(target = "location", source = "highSchoolForeignPlace")
    @Mapping(target = "fieldOfStudy", source = "personEducation", qualifiedByName = "highSchoolFieldOfStudy")
    Education.ForeignHighSchool toForeignHighSchool(
        EducationView personEducation,
        @Context EducationLookupData data
    );

    @Named("toHighSchoolAddress")
    @Mapping(target = "street", source = "highSchoolStreet")
    @Mapping(target = "zipCode", source = "highSchoolZipCode")
    @Mapping(target = "municipality", source = "highSchoolMunicipalityName")
    @Mapping(target = "district", source = "highSchoolDistrictName")
    @Mapping(target = "country", source = "highSchoolCountryName")
    Education.HighSchoolAddress toHighSchoolAddress(EducationLookupData data);

    @Named("highSchoolName")
    default String highSchoolName(
        EducationView personEducation,
        @Context EducationLookupData data
    ) {
        return data.highSchoolName();
    }

    @Named("highSchoolFieldOfStudy")
    default String highSchoolFieldOfStudy(
        EducationView personEducation,
        @Context EducationLookupData data
    ) {
        return data.highSchoolFieldOfStudy();
    }

    @Named("toHighSchoolAddressFromContext")
    default Education.HighSchoolAddress toHighSchoolAddressFromContext(
        EducationView personEducation,
        @Context EducationLookupData data
    ) {
        return toHighSchoolAddress(data);
    }

}
