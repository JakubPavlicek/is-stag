package com.stag.academics.studyplan.repository;

import com.stag.academics.studyplan.entity.StudyPlan;
import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/// **Study Plan Repository**
///
/// Data access layer for study plan entities.
/// Provides methods to retrieve field of study information associated with study plans.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

    /// Finds the field of study for a given study plan with a localized name.
    ///
    /// @param studyPlanId the study plan identifier
    /// @param language the language code (e.g., 'en', 'cz')
    /// @return optional field of study view with localized data
    @Query(
        """
        SELECT new com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView(
            fos.id,
            CASE
                WHEN :language = 'en' THEN COALESCE(fos.nameEn, fos.nameCz)
                ELSE fos.nameCz
            END AS name,
            fos.faculty,
            fos.department,
            fos.fieldNumber
        )
        FROM
            StudyPlan sp
        INNER JOIN sp.fieldOfStudy fos
        WHERE
            sp.id = :studyPlanId
        """
    )
    Optional<FieldOfStudyView> findFieldOfStudy(Long studyPlanId, String language);

}