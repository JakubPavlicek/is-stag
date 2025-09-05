package com.stag.academics.studyprogram.repository;

import com.stag.academics.studyprogram.entity.StudyProgram;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyProgramRepository extends JpaRepository<StudyProgram, Long> {

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