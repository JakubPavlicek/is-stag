package com.stag.identity.person.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorizationService {

    private final StudentLookupService studentLookupService;

    public boolean isStudentAndOwner(boolean isStudent, String studentId, Integer personId) {
        if (!isStudent) {
            return false;
        }

        Integer studentPersonId = studentLookupService.getStudentPersonId(studentId);
        return studentPersonId.equals(personId);
    }

}
