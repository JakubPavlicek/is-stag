package com.stag.academics.shared.grpc.mapper;

import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StudyPlanMapper {

    StudyPlanMapper INSTANCE = Mappers.getMapper(StudyPlanMapper.class);

    GetStudyProgramAndFieldResponse buildStudyProgramAndFieldResponse(
        StudyProgramView studyProgram,
        FieldOfStudyView fieldOfStudy
    );

}
