package com.stag.academics.shared.grpc.mapper;

import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldRequest;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StudyPlanMapper {

    StudyPlanMapper INSTANCE = Mappers.getMapper(StudyPlanMapper.class);

    GetStudyProgramAndFieldRequest toStudyProgramAndFieldDataRequest(Long studyProgramId, Long studyPlanId, String language);

    StudyProgramAndFieldLookupData toStudyProgramAndFieldData(GetStudyProgramAndFieldResponse response);

}
