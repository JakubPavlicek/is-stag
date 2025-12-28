package com.stag.academics.studyprogram.mapper;

import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// **Study Program Mapper**
///
/// MapStruct mapper for transforming study program views by enriching
/// raw data with localized codelist meanings for form and type fields.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(uses = { CodelistValueResolver.class })
public interface StudyProgramMapper {

    /// StudyProgramMapper Instance
    StudyProgramMapper INSTANCE = Mappers.getMapper(StudyProgramMapper.class);

    /// Converts raw study program view to an enriched view with codelist meanings.
    ///
    /// @param rawStudyProgramView the raw study program data
    /// @param data the codelist meanings lookup data
    /// @return enriched study program view
    @Mapping(target = "form", source = "rawStudyProgramView.form", qualifiedByName = "lookupForm")
    @Mapping(target = "type", source = "rawStudyProgramView.type", qualifiedByName = "lookupType")
    StudyProgramView toStudyProgramView(
        StudyProgramView rawStudyProgramView,
        @Context CodelistMeaningsLookupData data
    );

}
