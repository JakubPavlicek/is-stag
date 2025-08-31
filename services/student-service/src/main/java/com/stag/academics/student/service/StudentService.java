package com.stag.academics.student.service;

import com.stag.academics.student.exception.StudentNotFoundException;
import com.stag.academics.student.model.StudentProfile;
import com.stag.academics.student.repository.StudentProfileProjection;
import com.stag.academics.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<String> findAllStudentIds(Integer personId) {
        return studentRepository.findAllStudentIds(personId);
    }

    @Transactional(readOnly = true)
    public Integer findPersonId(String studentId) {
        return studentRepository.findPersonId(studentId)
                                .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    @Cacheable(value = "student-profile", key = "#studentId + ':' + #language")
    public StudentProfile getStudentProfile(String studentId, String language) {
        log.info("Fetching student profile for studentId: {} with language: {}", studentId, language);

        StudentProfileProjection studentProfileProjection =
            studentRepository.findById(studentId, StudentProfileProjection.class)
                             .orElseThrow(() -> new StudentNotFoundException(studentId));

        log.debug("Student profile found, fetching additional data for studentId: {}", studentId);

        // TODO: 1 StudyProgram can have multiple FieldOfStudies -> see Obsidian notes (probably use StudyPlan)
        //  see view VOBORY_STUDENTA DDL -> we will use this logic

        return null;
    }

}
