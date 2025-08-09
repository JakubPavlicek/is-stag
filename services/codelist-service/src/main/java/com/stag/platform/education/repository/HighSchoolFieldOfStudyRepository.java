package com.stag.platform.education.repository;

import com.stag.platform.education.entity.HighSchoolFieldOfStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HighSchoolFieldOfStudyRepository extends JpaRepository<HighSchoolFieldOfStudy, String> {

    @Query("SELECT h.name FROM HighSchoolFieldOfStudy h WHERE h.id = :id")
    Optional<String> findNameById(String id);

}