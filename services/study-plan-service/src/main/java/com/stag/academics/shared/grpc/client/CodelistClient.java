package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.CodelistMapper;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CodelistClient {

    @GrpcClient("codelist-service")
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistStub() {
        return codelistServiceStub.withDeadlineAfter(1, TimeUnit.SECONDS);
    }

    @CircuitBreaker(name = "codelist-service")
    @Retry(name = "codelist-service")
    public CodelistMeaningsLookupData getStudyProgramData(StudyProgramView studyProgram, String language) {
        var request = CodelistMapper.INSTANCE.toCodelistValuesRequest(studyProgram, language);
        var response = codelistStub().getCodelistValues(request);

        return CodelistMapper.INSTANCE.toCodelistMeaningsData(response);
    }

}
