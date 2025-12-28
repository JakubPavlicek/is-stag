package com.stag.platform.education.repository;

import com.stag.platform.education.entity.HighSchoolFieldOfStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/// **High School Field Of Study Repository**
///
/// Data access layer for high school field of study entities.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public interface HighSchoolFieldOfStudyRepository extends JpaRepository<HighSchoolFieldOfStudy, String> {

    /// Finds the name of a field of study by its ID.
    ///
    /// @param id the field of study ID
    /// @return optional containing the field of study name if found
    @Query("SELECT h.name FROM HighSchoolFieldOfStudy h WHERE h.id = :id")
    Optional<String> findNameById(String id);

}