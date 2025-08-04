package com.stag.platform.codelist.service;

import com.stag.platform.codelist.repository.HighSchoolFieldOfStudyRepository;
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
