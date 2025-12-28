package com.stag.academics.student.service;

import com.stag.academics.shared.grpc.client.StudyPlanClient;
import com.stag.academics.shared.grpc.client.UserClient;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/// **Student Async Service**
///
/// Handles asynchronous operations for fetching external student-related data
/// from Person and Study Plan services via gRPC clients.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class StudentAsyncService {

    private final UserClient userClient;
    private final StudyPlanClient studyPlanClient;

    /// Asynchronously fetches person profile data for a student.
    ///
    /// @param personId the person identifier
    /// @param language the language code for localized data
    /// @return completable future containing simple profile data
    @Async
    public CompletableFuture<SimpleProfileLookupData> getPersonSimpleProfileData(
        Integer personId,
        String language
    ) {
        log.info("Fetching student simple profile data");
        SimpleProfileLookupData simpleProfileLookupData =
            userClient.getPersonSimpleProfileData(personId, language);
        log.debug("Completed fetching student simple profile data");

        return CompletableFuture.completedFuture(simpleProfileLookupData);
    }

    /// Asynchronously fetches study program and field of study data.
    ///
    /// @param studyProgramId the study program identifier
    /// @param studyPlanId the study plan identifier
    /// @param language the language code for localized data
    /// @return a completable future containing study program and field data
    @Async
    public CompletableFuture<StudyProgramAndFieldLookupData> getStudyProgramAndField(
        Long studyProgramId,
        Long studyPlanId,
        String language
    ) {
        log.info("Fetching student study program and field of study data");
        StudyProgramAndFieldLookupData studyProgramAndFieldLookupData =
            studyPlanClient.getStudyProgramAndField(studyProgramId, studyPlanId, language);
        log.debug("Completed fetching study program and field of study data");

        return CompletableFuture.completedFuture(studyProgramAndFieldLookupData);
    }

}
