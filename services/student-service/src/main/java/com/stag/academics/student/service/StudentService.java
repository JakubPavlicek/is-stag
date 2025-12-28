package com.stag.academics.student.service;

import com.stag.academics.student.exception.StudentNotFoundException;
import com.stag.academics.student.mapper.ProfileMapper;
import com.stag.academics.student.model.Profile;
import com.stag.academics.student.repository.StudentRepository;
import com.stag.academics.student.repository.projection.ProfileView;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/// **Student Service**
///
/// Core service for managing student data and profiles. Orchestrates data
/// retrieval from repository and external services, with caching and security.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class StudentService {

    /// Student Repository
    private final StudentRepository studentRepository;
    /// Student Async Service for asynchronous external data fetching.
    private final StudentAsyncService studentAsyncService;

    /// Transaction Template for transaction management.
    private final TransactionTemplate transactionTemplate;

    /// Retrieves all student IDs associated with a person.
    ///
    /// @param personId the person identifier
    /// @return list of student IDs
    @Transactional(readOnly = true)
    public List<String> findAllStudentIds(Integer personId) {
        return studentRepository.findAllStudentIds(personId);
    }

    /// Finds the person ID for a given student.
    ///
    /// @param studentId the student identifier
    /// @return the person ID
    /// @throws StudentNotFoundException if student not found
    @Transactional(readOnly = true)
    public Integer findPersonId(String studentId) {
        return studentRepository.findPersonId(studentId)
                                .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    /// Retrieves complete student profile with enriched data from external services.
    ///
    /// Fetches profile data asynchronously from Person and Study Plan services,
    /// then combines the results. Cached by student ID and language.
    /// Access is restricted to authorized roles or the students themselves.
    ///
    /// @param studentId the student identifier
    /// @param language the language code for localized data
    /// @return complete student profile
    /// @throws StudentNotFoundException if a student not found
    @Cacheable(value = "student-profile", key = "{#studentId, #language}")
    @PreAuthorize("""
        hasAnyRole('AD', 'DE', 'PR', 'SR', 'SP', 'VY', 'VK')
        || #studentId.equalsIgnoreCase(principal.claims['studentId'])
    """)
    public Profile getStudentProfile(String studentId, String language) {
        log.info("Fetching student profile for studentId: {} with language: {}", studentId, language);

        // Fetch base profile within a transaction
        ProfileView profileView = transactionTemplate.execute(status ->
            studentRepository.findStudentProfileById(studentId)
                             .orElseThrow(() -> new StudentNotFoundException(studentId))
        );

        log.debug("Student profile found, fetching additional data for studentId: {}", studentId);

        // Fetch external data asynchronously in parallel
        CompletableFuture<SimpleProfileLookupData> personSimpleProfileDataFuture =
            studentAsyncService.getPersonSimpleProfileData(profileView.personId(), language);

        CompletableFuture<StudyProgramAndFieldLookupData> studyProgramAndFieldFuture =
            studentAsyncService.getStudyProgramAndField(profileView.studyProgramId(), profileView.studyPlanId(), language);

        // Wait for both async operations to complete
        CompletableFuture.allOf(personSimpleProfileDataFuture, studyProgramAndFieldFuture).join();

        log.debug("Additional data fetched, mapping to StudentProfile for studentId: {}", studentId);
        Profile profile = ProfileMapper.INSTANCE.toStudentProfile(
            profileView, personSimpleProfileDataFuture.join(), studyProgramAndFieldFuture.join()
        );

        log.info("Successfully fetched student profile for studentId: {}", studentId);
        return profile;
    }

}
