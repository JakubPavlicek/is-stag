package com.stag.academics.shared.grpc.mapper;

import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/// **Study Plan Mapper**
///
/// MapStruct mapper for building gRPC responses combining study program
/// and field of study views. Used by gRPC service layer.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StudyPlanMapper {

    /// StudyPlanMapper Instance
    StudyPlanMapper INSTANCE = Mappers.getMapper(StudyPlanMapper.class);

    /// Builds a gRPC response containing study program and field of study data.
    ///
    /// @param studyProgram the study program view
    /// @param fieldOfStudy the field of study view
    /// @return gRPC response with combined data
    GetStudyProgramAndFieldResponse buildStudyProgramAndFieldResponse(
        StudyProgramView studyProgram,
        FieldOfStudyView fieldOfStudy
    );

}
