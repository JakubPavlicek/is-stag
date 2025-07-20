package com.stag.academics.student.service;

import com.stag.academics.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<String> findAllPersonalNumbers(Integer personId) {
        return studentRepository.findAllPersonalNumbers(personId);
    }

}
