package com.stag.identity.person.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccessPolicyService {

    private final StudentLookupService studentLookupService;

    public boolean canAccessPerson(JwtAuthenticationToken token, Integer personId) {
        Collection<GrantedAuthority> roles = token.getAuthorities();

        List<String> roleNames = roles.stream()
                                      .map(GrantedAuthority::getAuthority)
                                      .toList();

        // Students can only access their own data
        if (roleNames.contains("ROLE_ST")) {
            return canStudentAccessPerson(token, personId);
        }

        // TODO: Add Teacher support ?

        return false;
    }

    private boolean canStudentAccessPerson(JwtAuthenticationToken token, Integer personId) {
        String personalNumber = token.getTokenAttributes()
                                     .get("osCislo")
                                     .toString();

        log.info("Checking if student {} can access person {}", personalNumber, personId);

        Integer studentPersonId = studentLookupService.getStudentPersonId(personalNumber);
        boolean canAccess = studentPersonId.equals(personId);

        log.debug("Student {} can access person {}: {}", personalNumber, personId, canAccess);

        return canAccess;
    }

}
