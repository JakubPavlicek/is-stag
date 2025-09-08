package com.stag.academics.student.mapper;

import com.stag.academics.student.model.Profile;
import com.stag.academics.student.repository.projection.ProfileView;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    @Mapping(target = "titles.prefix", source = "profileData.titlePrefix")
    @Mapping(target = "titles.suffix", source = "profileData.titleSuffix")
    Profile toStudentProfile(
        ProfileView profile,
        SimpleProfileLookupData profileData,
        StudyProgramAndFieldLookupData studyProgramAndFieldData
    );

}
