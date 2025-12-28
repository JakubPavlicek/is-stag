package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.StudyPlanMapper;
import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/// **Study Plan Client**
///
/// gRPC client for communicating with the Study Plan service to fetch study program
/// and field of study data. Includes circuit breaker, retry logic, and caching.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@Service
public class StudyPlanClient {

    /// Study plan service stub
    private StudyPlanServiceGrpc.StudyPlanServiceBlockingStub studyPlanServiceStub;

    /// Constructs the study plan client with a study plan service stub.
    ///
    /// @param studyPlanServiceStub the gRPC blocking stub for study plan service
    public StudyPlanClient(StudyPlanServiceGrpc.StudyPlanServiceBlockingStub studyPlanServiceStub) {
        this.studyPlanServiceStub = studyPlanServiceStub;
    }

    /// Fetches study program and field of study data via gRPC.
    ///
    /// Includes circuit breaker for fault tolerance and caching for performance.
    ///
    /// @param studyProgramId the study program identifier
    /// @param studyPlanId the study plan identifier
    /// @param language the language code for localized data
    /// @return study program and field lookup data
    @Cacheable(value = "study-program-and-field", key = "{#studyProgramId, #studyPlanId, #language}")
    @CircuitBreaker(name = "study-plan-service")
    @Retry(name = "study-plan-service")
    public StudyProgramAndFieldLookupData getStudyProgramAndField(Long studyProgramId, Long studyPlanId, String language) {
        log.info("Fetching study program: {}, field of study with plan: {}", studyProgramId, studyPlanId);

        // Build gRPC request
        var request = StudyPlanMapper.INSTANCE.toStudyProgramAndFieldDataRequest(studyProgramId, studyPlanId, language);
        var response = studyPlanServiceStub.getStudyProgramAndField(request);

        log.debug("Completed fetching study program and field of study");

        return StudyPlanMapper.INSTANCE.toStudyProgramAndFieldData(response);
    }

}