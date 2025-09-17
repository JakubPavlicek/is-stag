package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.StudyPlanMapper;
import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StudyPlanClient {

    @GrpcClient("study-plan-service")
    private StudyPlanServiceGrpc.StudyPlanServiceBlockingStub studyPlanServiceStub;

    private StudyPlanServiceGrpc.StudyPlanServiceBlockingStub studyPlanStub() {
        return studyPlanServiceStub.withDeadlineAfter(500, TimeUnit.MILLISECONDS);
    }

    @Cacheable(value = "study-program-and-field", key = "{#studyProgramId, #studyPlanId, #language}")
    @CircuitBreaker(name = "study-plan-service")
    @Retry(name = "grpc-retry")
    public StudyProgramAndFieldLookupData getStudyProgramAndField(Long studyProgramId, Long studyPlanId, String language) {
        log.info("Fetching study program: {}, field of study with plan: {}", studyProgramId, studyPlanId);

        var request = StudyPlanMapper.INSTANCE.toStudyProgramAndFieldDataRequest(studyProgramId, studyPlanId, language);
        var response = studyPlanStub().getStudyProgramAndField(request);

        log.debug("Completed fetching study program and field of study");

        return StudyPlanMapper.INSTANCE.toStudyProgramAndFieldData(response);
    }

}