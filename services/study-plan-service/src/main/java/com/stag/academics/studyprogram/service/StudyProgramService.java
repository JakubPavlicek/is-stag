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

/// **Study Program Service**
///
/// Business logic layer for study program operations.
/// Retrieves study program views enriched with codelist meanings from the codelist service.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class StudyProgramService {

    /// Study Program Repository
    private final StudyProgramRepository studyProgramRepository;

    /// Codelist Client
    private final CodelistClient codelistClient;

    /// Transaction Template for transaction management
    private final TransactionTemplate transactionTemplate;

    /// Finds a study program by ID with localized codelist meanings.
    ///
    /// @param studyProgramId the study program identifier
    /// @param language the language code for localization
    /// @return enriched study program view with codelist meanings
    /// @throws StudyProgramNotFoundException if study program not found
    public StudyProgramView findStudyProgram(Long studyProgramId, String language) {
        log.info("Fetching study program with ID: {} and language: {}", studyProgramId, language);

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
