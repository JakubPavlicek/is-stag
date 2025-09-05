package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.CodelistMapper;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CodelistClient {

    @GrpcClient("codelist-service")
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    public CodelistMeaningsLookupData getStudyProgramData(StudyProgramView studyProgram, String language) {
        var request = CodelistMapper.INSTANCE.toCodelistValuesRequest(studyProgram, language);
        var response = codelistServiceStub.getCodelistValues(request);

        return CodelistMapper.INSTANCE.toCodelistMeaningsData(response);
    }

}
