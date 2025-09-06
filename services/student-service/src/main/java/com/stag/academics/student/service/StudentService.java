package com.stag.academics.student.service;

import com.stag.academics.student.exception.StudentNotFoundException;
import com.stag.academics.student.mapper.ProfileMapper;
import com.stag.academics.student.model.StudentProfile;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentAsyncService studentAsyncService;

    @Transactional(readOnly = true)
    public List<String> findAllStudentIds(Integer personId) {
        return studentRepository.findAllStudentIds(personId);
    }

    @Transactional(readOnly = true)
    public Integer findPersonId(String studentId) {
        return studentRepository.findPersonId(studentId)
                                .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    // TODO: Other students can also fetch the student profile, but they should not see StudyProgram and FieldOfStudy of the student
    //  we can make this work by annotating the get methods / gRPC calls with @PreAuthorize and then providing null if not satisfied
    //  see https://www.youtube.com/watch?v=-x8-s3QnhMQ&list=PLa1A960Nosoe5yT4Uj_LdrtgtZH8c7vfR&index=109&ab_channel=SpringI%2FO at 27:35

    @Cacheable(value = "student-profile", key = "#studentId + ':' + #language")
    @PreAuthorize("""
        hasAnyRole('AD', 'DE', 'PR', 'SR', 'SP', 'VY', 'VK')
        || #studentId == principal.claims['studentId']
    """)
    public StudentProfile getStudentProfile(String studentId, String language) {
        log.info("Fetching student profile for studentId: {} with language: {}", studentId, language);

        ProfileView profileView = studentRepository.findStudentProfileById(studentId)
                                                   .orElseThrow(() -> new StudentNotFoundException(studentId));

        log.debug("Student profile found, fetching additional data for studentId: {}", studentId);

        CompletableFuture<SimpleProfileLookupData> personSimpleProfileDataFuture =
            studentAsyncService.getPersonSimpleProfileData(profileView.personId(), language);

        CompletableFuture<StudyProgramAndFieldLookupData> studyProgramAndFieldFuture =
            studentAsyncService.getStudyProgramAndField(profileView.studyProgramId(), profileView.studyPlanId(), language);

        CompletableFuture.allOf(personSimpleProfileDataFuture, studyProgramAndFieldFuture).join();

        log.debug("Additional data fetched, mapping to StudentProfile for studentId: {}", studentId);
        StudentProfile profile = ProfileMapper.INSTANCE.toStudentProfile(
            profileView, personSimpleProfileDataFuture.join(), studyProgramAndFieldFuture.join()
        );

        log.info("Successfully fetched student profile for studentId: {}", studentId);
        return profile;
    }

}
