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

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentAsyncService {

    private final UserClient userClient;
    private final StudyPlanClient studyPlanClient;

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
