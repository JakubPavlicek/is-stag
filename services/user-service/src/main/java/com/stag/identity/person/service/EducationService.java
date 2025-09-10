package com.stag.identity.person.service;

import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.mapper.EducationMapper;
import com.stag.identity.person.model.Education;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.EducationView;
import com.stag.identity.person.service.data.EducationLookupData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class EducationService {

    private final PersonRepository personRepository;
    private final CodelistLookupService codelistLookupService;

    @Cacheable(value = "person-education", key = "{#personId, #language}")
    @PreAuthorize("""
        hasAnyRole('AD', 'DE', 'PR', 'SR', 'SP', 'VY', 'VK')
        || @authorizationService.isStudentAndOwner(hasRole('ST'), principal.claims['studentId'], #personId)
    """)
    public Education getPersonEducation(Integer personId, String language) {
        log.info("Fetching person education information for personId: {} with language: {}", personId, language);

        EducationView educationView =
            personRepository.findEducationByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        log.debug("Person education information found, fetching additional data for personId: {}", personId);

        CompletableFuture<EducationLookupData> educationDataFuture =
            codelistLookupService.getPersonEducationData(educationView, language);

        EducationLookupData educationData = educationDataFuture.join();

        log.debug("Additional data fetched, mapping to PersonEducation for personId: {}", personId);
        Education education = EducationMapper.INSTANCE.toPersonEducation(
            educationView, educationData
        );

        log.info("Successfully fetched person education information for personId: {}", personId);
        return education;
    }

}
