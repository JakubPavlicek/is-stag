package com.stag.academics.student.service;

import com.stag.academics.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<String> findAllStudentIds(Integer personId) {
        return studentRepository.findAllStudentIds(personId);
    }

    // TODO: create own exception

    @Transactional(readOnly = true)
    public Integer findPersonId(String studentId) {
        return studentRepository.findPersonId(studentId)
                                .orElseThrow(() -> new RuntimeException("Student not found for ID: " + studentId));
    }

}
