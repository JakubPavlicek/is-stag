package com.stag.academics.student.controller;

import com.stag.academics.api.StudentsApi;
import com.stag.academics.api.dto.StudentResponse;
import com.stag.academics.student.mapper.StudentApiMapper;
import com.stag.academics.student.model.Profile;
import com.stag.academics.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// **Student Controller**
///
/// REST API controller for student operations. Implements OpenAPI-generated
/// interface for managing students.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class StudentController implements StudentsApi {

    /// Student Service
    private final StudentService studentService;

    /// Retrieves a student profile by ID with localized data.
    ///
    /// @param studentId the student identifier
    /// @param language the language code for localization
    /// @return response entity containing student profile
    @Override
    public ResponseEntity<StudentResponse> getStudentProfile(String studentId, String language) {
        log.info("Student profile requested for studentId: {} with language: {}", studentId, language);

        Profile profile = studentService.getStudentProfile(studentId, language);
        StudentResponse studentResponse = StudentApiMapper.INSTANCE.toStudentResponse(profile);

        return ResponseEntity.ok(studentResponse);
    }

}
