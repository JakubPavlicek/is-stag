package com.stag.platform.education.service;

import com.stag.platform.education.repository.HighSchoolFieldOfStudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HighSchoolFieldOfStudyService {

    private final HighSchoolFieldOfStudyRepository fieldOfStudyRepository;

    public String findFieldOfStudyName(String fieldOfStudyNumber) {
        return fieldOfStudyRepository.findNameById(fieldOfStudyNumber);
    }

}
