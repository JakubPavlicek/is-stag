package com.stag.academics.student.mapper;

import com.stag.academics.student.model.Profile;
import com.stag.academics.student.repository.projection.ProfileView;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// **Profile Mapper**
///
/// MapStruct mapper for assembling complete student profile from multiple data sources.
/// Combines database projection with external service data.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper
public interface ProfileMapper {

    /// ProfileMapper Instance
    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    /// Maps profile view and external data to complete the student profile.
    ///
    /// Combines data from database, person service, and study plan service
    /// into a unified profile model with nested title structure.
    ///
    /// @param profile the database profile projection
    /// @param profileData person profile data from external service
    /// @param studyProgramAndFieldData study program data from external service
    /// @return complete student profile
    @Mapping(target = "titles.prefix", source = "profileData.titlePrefix")
    @Mapping(target = "titles.suffix", source = "profileData.titleSuffix")
    Profile toStudentProfile(
        ProfileView profile,
        SimpleProfileLookupData profileData,
        StudyProgramAndFieldLookupData studyProgramAndFieldData
    );

}
