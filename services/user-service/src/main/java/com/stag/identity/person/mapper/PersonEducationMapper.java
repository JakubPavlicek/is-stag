package com.stag.identity.person.mapper;

import com.stag.identity.person.model.PersonEducation;
import com.stag.identity.person.repository.projection.PersonEducationProjection;
import com.stag.identity.person.service.data.PersonEducationData;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { Qualifiers.class })
public interface PersonEducationMapper {

    PersonEducationMapper INSTANCE = Mappers.getMapper(PersonEducationMapper.class);

    @Mapping(target = "highSchool", source = "personEducation", qualifiedByName = "toHighSchool")
    @Mapping(target = "foreignHighSchool", source = "personEducation", qualifiedByName = "toForeignHighSchool")
    PersonEducation toPersonEducation(
        PersonEducationProjection personEducation,
        @Context PersonEducationData data
    );

    @Named("toHighSchool")
    @Mapping(target = "name", expression = "java(data.highSchoolName())")
    @Mapping(target = "fieldOfStudy", expression = "java(data.highSchoolFieldOfStudy())")
    @Mapping(target = "graduationDate", source = "personEducation.graduationDate")
    @Mapping(target = "address", ignore = true)
    PersonEducation.HighSchool toHighSchool(
        PersonEducationProjection personEducation,
        @Context PersonEducationData data
    );

    @AfterMapping
    default void mapHighSchoolAddress(
        PersonEducationData data,
        @MappingTarget PersonEducation.HighSchool.HighSchoolBuilder target
    ) {
        target.address(toHighSchoolAddress(data));
    }

    @Named("toForeignHighSchool")
    @Mapping(target = "name", source = "personEducation.highSchoolForeign")
    @Mapping(target = "location", source = "personEducation.highSchoolForeignPlace")
    @Mapping(target = "fieldOfStudy", expression = "java(data.highSchoolFieldOfStudy())")
    PersonEducation.ForeignHighSchool toForeignHighSchool(
        PersonEducationProjection personEducation,
        @Context PersonEducationData data
    );

    @Named("toHighSchoolAddress")
    @Mapping(target = "street", source = "highSchoolStreet")
    @Mapping(target = "zipCode", source = "highSchoolZipCode")
    @Mapping(target = "municipality", source = "highSchoolMunicipalityName")
    @Mapping(target = "district", source = "highSchoolDistrictName")
    @Mapping(target = "country", source = "highSchoolCountryName")
    PersonEducation.HighSchoolAddress toHighSchoolAddress(PersonEducationData data);

}
