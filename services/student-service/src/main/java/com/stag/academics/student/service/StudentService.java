package com.stag.academics.student.service;

import com.stag.academics.shared.grpc.client.StudyPlanClient;
import com.stag.academics.shared.grpc.client.UserClient;
import com.stag.academics.student.exception.StudentNotFoundException;
import com.stag.academics.student.exception.StudentProfileFetchException;
import com.stag.academics.student.mapper.ProfileMapper;
import com.stag.academics.student.model.Profile;
import com.stag.academics.student.repository.StudentRepository;
import com.stag.academics.student.repository.projection.ProfileView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;

import static java.util.concurrent.StructuredTaskScope.Joiner.allSuccessfulOrThrow;

/// **Student Service**
///
/// Core service for managing student data and profiles.
/// Orchestrates data retrieval from repository and external services, with caching and security.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class StudentService {

    /// Student Repository
    private final StudentRepository studentRepository;

    /// gRPC User Client
    private final UserClient userClient;
    /// gRPC Study Plan Client
    private final StudyPlanClient studyPlanClient;

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
    /// @throws StudentNotFoundException if a student not found
    @Transactional(readOnly = true)
    public Integer findPersonId(String studentId) {
        return studentRepository.findPersonId(studentId)
                                .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    /// Retrieves complete student profile with enriched data from external services.
    /// Fetches profile data from User and Study Plan services, then combines the results.
    /// Cached by student ID and language.
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
        ProfileView profileView = transactionTemplate.execute(_ ->
            studentRepository.findStudentProfileById(studentId)
                             .orElseThrow(() -> new StudentNotFoundException(studentId))
        );

        try (var scope = StructuredTaskScope.open(allSuccessfulOrThrow())) {
            var simpleProfileTask = scope.fork(
                () -> userClient.getPersonSimpleProfileData(profileView.personId(), language)
            );

            var studyProgramAndFieldTask = scope.fork(
                () -> studyPlanClient.getStudyProgramAndField(profileView.studyProgramId(), profileView.studyPlanId(), language)
            );

            scope.join();

            Profile profile = ProfileMapper.INSTANCE.toStudentProfile(
                profileView, simpleProfileTask.get(), studyProgramAndFieldTask.get()
            );

            log.info("Successfully fetched student profile for studentId: {}", studentId);
            return profile;
        } catch (StructuredTaskScope.FailedException e) {
            // Re-throw known exceptions untouched
            if (e.getCause() instanceof RuntimeException re) {
                throw re;
            }

            // Wrap unknown checked exceptions
            throw new StudentProfileFetchException(studentId, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new StudentProfileFetchException(studentId, e);
        }
    }

}
