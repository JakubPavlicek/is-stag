package com.stag.platform.codelist.repository;

import com.stag.platform.codelist.entity.HighSchoolFieldOfStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HighSchoolFieldOfStudyRepository extends JpaRepository<HighSchoolFieldOfStudy, String> {

    @Query("SELECT h.name FROM HighSchoolFieldOfStudy h WHERE h.id = :id")
    String findNameById(String id);

}