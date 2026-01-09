package com.stag.academics.shared.grpc.mapper;

import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldRequest;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/// **Study Plan Mapper**
///
/// MapStruct mapper for converting between gRPC study plan service messages and internal data models.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StudyPlanMapper {

    /// StudyPlanMapper Instance
    StudyPlanMapper INSTANCE = Mappers.getMapper(StudyPlanMapper.class);

    /// Maps study identifiers and language to gRPC request.
    ///
    /// @param studyProgramId the study program identifier
    /// @param studyPlanId the study plan identifier
    /// @param language the language code
    /// @return gRPC request for study program and field data
    GetStudyProgramAndFieldRequest toStudyProgramAndFieldDataRequest(Long studyProgramId, Long studyPlanId, String language);

    /// Maps gRPC response to internal study program data model.
    ///
    /// @param response the gRPC response
    /// @return study program and field lookup data
    StudyProgramAndFieldLookupData toStudyProgramAndFieldData(GetStudyProgramAndFieldResponse response);

}
