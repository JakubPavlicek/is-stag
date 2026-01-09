package com.stag.academics.studyprogram.repository;

import com.stag.academics.studyprogram.entity.StudyProgram;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/// **Study Program Repository**
///
/// Data access layer for study program entities.
/// Provides methods to retrieve localized study program views with basic information.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public interface StudyProgramRepository extends JpaRepository<StudyProgram, Long> {

    /// Finds a study program view by ID with localized name.
    ///
    /// @param studyProgramId the study program identifier
    /// @param language the language code (e.g., 'en', 'cz')
    /// @return optional study program view with localized data
    @Query(
        """
        SELECT new com.stag.academics.studyprogram.repository.projection.StudyProgramView(
            sp.id,
            CASE
                WHEN :language = 'en' THEN COALESCE(sp.nameEn, sp.nameCz)
                ELSE sp.nameCz
            END AS name,
            sp.faculty,
            sp.code,
            sp.form,
            sp.type
        )
        FROM
            StudyProgram sp
        WHERE
            sp.id = :studyProgramId
        """
    )
    Optional<StudyProgramView> findStudyProgramViewById(Long studyProgramId, String language);

}