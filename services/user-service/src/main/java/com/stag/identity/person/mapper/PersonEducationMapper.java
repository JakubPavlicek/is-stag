package com.stag.identity.person.mapper;

import com.stag.identity.person.model.PersonEducation;
import com.stag.identity.person.repository.projection.PersonEducationProjection;
import com.stag.identity.person.service.data.PersonEducationData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    @Mapping(target = "name", source = "personEducation", qualifiedByName = "highSchoolName")
    @Mapping(target = "fieldOfStudy", source = "personEducation", qualifiedByName = "highSchoolFieldOfStudy")
    @Mapping(target = "address", source = "personEducation", qualifiedByName = "toHighSchoolAddressFromContext")
    PersonEducation.HighSchool toHighSchool(
        PersonEducationProjection personEducation,
        @Context PersonEducationData data
    );

    @Named("toForeignHighSchool")
    @Mapping(target = "name", source = "highSchoolForeign")
    @Mapping(target = "location", source = "highSchoolForeignPlace")
    @Mapping(target = "fieldOfStudy", source = "personEducation", qualifiedByName = "highSchoolFieldOfStudy")
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

    @Named("highSchoolName")
    default String highSchoolName(
        PersonEducationProjection personEducation,
        @Context PersonEducationData data
    ) {
        return data.highSchoolName();
    }

    @Named("highSchoolFieldOfStudy")
    default String highSchoolFieldOfStudy(
        PersonEducationProjection personEducation,
        @Context PersonEducationData data
    ) {
        return data.highSchoolFieldOfStudy();
    }

    @Named("toHighSchoolAddressFromContext")
    default PersonEducation.HighSchoolAddress toHighSchoolAddressFromContext(
        PersonEducationProjection personEducation,
        @Context PersonEducationData data
    ) {
        return toHighSchoolAddress(data);
    }

}
