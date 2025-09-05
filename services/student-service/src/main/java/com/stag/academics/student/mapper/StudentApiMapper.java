package com.stag.academics.student.mapper;

import com.stag.academics.dto.StudentProfileDTO;
import com.stag.academics.student.model.StudentProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentApiMapper {

    StudentApiMapper INSTANCE = Mappers.getMapper(StudentApiMapper.class);

    StudentProfileDTO toStudentProfileDTO(StudentProfile studentProfile);

}
