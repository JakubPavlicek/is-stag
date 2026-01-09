package com.stag.academics.student.mapper;

import com.stag.academics.api.dto.StudentResponse;
import com.stag.academics.student.model.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/// **Student API Mapper**
///
/// MapStruct mapper for converting an internal Profile model to REST API response DTOs.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper
public interface StudentApiMapper {

    /// StudentApiMapper Instance
    StudentApiMapper INSTANCE = Mappers.getMapper(StudentApiMapper.class);

    /// Maps profile model to student response DTO.
    ///
    /// @param profile the internal profile model
    /// @return student response DTO
    StudentResponse toStudentResponse(Profile profile);

}
