package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.CodelistMapper;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/// **Codelist Client**
///
/// gRPC client for communicating with the Codelist Service. Retrieves localized
/// codelist meanings for study program data with circuit breaker and retry support.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@Service
public class CodelistClient {

    /// Codelist Service stub
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    /// Constructor for CodelistClient creation.
    ///
    /// @param codelistServiceStub Codelist Service stub
    public CodelistClient(CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub) {
        this.codelistServiceStub = codelistServiceStub;
    }

    /// Fetches codelist meanings for study program data from codelist service.
    ///
    /// @param studyProgram the study program view
    /// @param language the language code for localization
    /// @return codelist meanings lookup data
    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public CodelistMeaningsLookupData getStudyProgramData(StudyProgramView studyProgram, String language) {
        log.info("Fetching codelist meanings for study program: {}", studyProgram.id());

        var request = CodelistMapper.INSTANCE.toCodelistValuesRequest(studyProgram, language);
        var response = codelistServiceStub.getCodelistValues(request);

        log.debug("Completed fetching codelist meanings for study program: {}", studyProgram.id());

        return CodelistMapper.INSTANCE.toCodelistMeaningsData(response);
    }

}
