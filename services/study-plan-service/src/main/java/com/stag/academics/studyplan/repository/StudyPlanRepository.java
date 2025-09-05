package com.stag.academics.studyplan.repository;

import com.stag.academics.studyplan.entity.StudyPlan;
import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

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