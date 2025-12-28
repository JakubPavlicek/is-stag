package com.stag.academics.shared.grpc.service;

import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import com.stag.academics.studyplan.service.StudyPlanService;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.StudyProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/// **Study Plan Async gRPC Service**
///
/// Async service layer for concurrent data fetching in gRPC operations.
/// Enables parallel execution of study program and field of study queries.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class StudyPlanAsyncGrpcService {

    /// Study Program Service
    private final StudyProgramService studyProgramService;
    /// Study Plan Service
    private final StudyPlanService studyPlanService;

    /// Fetches study program asynchronously.
    ///
    /// @param studyProgramId the study program identifier
    /// @param language the language code for localization
    /// @return future containing study program view
    @Async
    public CompletableFuture<StudyProgramView> fetchStudyProgram(Long studyProgramId, String language) {
        StudyProgramView studyProgram = studyProgramService.findStudyProgram(studyProgramId, language);
        return CompletableFuture.completedFuture(studyProgram);
    }

    /// Fetches field of study asynchronously.
    ///
    /// @param studyPlanId the study plan identifier
    /// @param language the language code for localization
    /// @return future containing field of study view
    @Async
    public CompletableFuture<FieldOfStudyView> fetchFieldOfStudy(Long studyPlanId, String language) {
        FieldOfStudyView fieldOfStudy = studyPlanService.findFieldOfStudy(studyPlanId, language);
        return CompletableFuture.completedFuture(fieldOfStudy);
    }

}
