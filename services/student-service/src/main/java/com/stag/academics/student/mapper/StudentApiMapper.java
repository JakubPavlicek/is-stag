package com.stag.academics.student.mapper;

import com.stag.academics.api.dto.StudentResponse;
import com.stag.academics.student.model.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentApiMapper {

    StudentApiMapper INSTANCE = Mappers.getMapper(StudentApiMapper.class);

    StudentResponse toStudentResponse(Profile profile);

}
