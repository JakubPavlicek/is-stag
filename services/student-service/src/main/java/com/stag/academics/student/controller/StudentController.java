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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class StudentController implements StudentsApi {

    private final StudentService studentService;

    @Override
    public ResponseEntity<StudentResponse> getStudentProfile(String studentId, String language) {
        log.info("Student profile requested for studentId: {} with language: {}", studentId, language);

        Profile profile = studentService.getStudentProfile(studentId, language);
        StudentResponse studentResponse = StudentApiMapper.INSTANCE.toStudentResponse(profile);

        return ResponseEntity.ok(studentResponse);
    }

}
