package com.stag.academics.studyprogram.mapper;

import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { CodelistValueResolver.class })
public interface StudyProgramMapper {

    StudyProgramMapper INSTANCE = Mappers.getMapper(StudyProgramMapper.class);

    @Mapping(target = "form", source = "rawStudyProgramView.form", qualifiedByName = "lookupForm")
    @Mapping(target = "type", source = "rawStudyProgramView.type", qualifiedByName = "lookupType")
    StudyProgramView toStudyProgramView(
        StudyProgramView rawStudyProgramView,
        @Context CodelistMeaningsLookupData data
    );

}
