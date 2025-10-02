package com.stag.academics.studyprogram.service;

import com.stag.academics.shared.grpc.client.CodelistClient;
import com.stag.academics.studyprogram.exception.StudyProgramNotFoundException;
import com.stag.academics.studyprogram.mapper.StudyProgramMapper;
import com.stag.academics.studyprogram.repository.StudyProgramRepository;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyProgramService {

    private final StudyProgramRepository studyProgramRepository;

    private final CodelistClient codelistClient;

    private final TransactionTemplate transactionTemplate;

    public StudyProgramView findStudyProgram(Long studyProgramId, String language) {
        StudyProgramView rawStudyProgramView = transactionTemplate.execute(status ->
            studyProgramRepository.findStudyProgramViewById(studyProgramId, language)
                                  .orElseThrow(() -> new StudyProgramNotFoundException(studyProgramId))
        );

        // Fetch codelist meanings from the codelist-service
        CodelistMeaningsLookupData studyProgramData =
            codelistClient.getStudyProgramData(rawStudyProgramView, language);

        // Enrich the raw StudyProgramView with the codelist meanings
        return StudyProgramMapper.INSTANCE.toStudyProgramView(
            rawStudyProgramView, studyProgramData
        );
    }

}
