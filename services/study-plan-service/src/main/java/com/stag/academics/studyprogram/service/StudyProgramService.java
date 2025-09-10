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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyProgramService {

    private final StudyProgramRepository studyProgramRepository;

    private final CodelistClient codelistClient;

    public StudyProgramView findStudyProgramViewById(Long studyProgramId, String language) {
        return studyProgramRepository.findStudyProgramViewById(studyProgramId, language)
                                     .orElseThrow(() -> new StudyProgramNotFoundException(studyProgramId));
    }

    @Transactional(readOnly = true)
    public StudyProgramView findStudyProgram(Long studyProgramId, String language) {
        StudyProgramView rawStudyProgramView = findStudyProgramViewById(studyProgramId, language);

        // Fetch codelist meanings from the codelist-service
        CodelistMeaningsLookupData studyProgramData =
            codelistClient.getStudyProgramData(rawStudyProgramView, language);

        // Enrich the raw StudyProgramView with the codelist meanings
        return StudyProgramMapper.INSTANCE.toStudyProgramView(
            rawStudyProgramView, studyProgramData
        );
    }

}
