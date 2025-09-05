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

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyPlanAsyncGrpcService {

    private final StudyProgramService studyProgramService;
    private final StudyPlanService studyPlanService;

    @Async
    public CompletableFuture<StudyProgramView> fetchStudyProgram(Long studyProgramId, String language) {
        StudyProgramView studyProgram = studyProgramService.findStudyProgram(studyProgramId, language);
        return CompletableFuture.completedFuture(studyProgram);
    }

    @Async
    public CompletableFuture<FieldOfStudyView> fetchFieldOfStudy(Long studyPlanId, String language) {
        FieldOfStudyView fieldOfStudy = studyPlanService.findFieldOfStudy(studyPlanId, language);
        return CompletableFuture.completedFuture(fieldOfStudy);
    }

}
