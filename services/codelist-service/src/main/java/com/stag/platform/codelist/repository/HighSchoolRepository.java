package com.stag.platform.codelist.repository;

import com.stag.platform.codelist.entity.HighSchool;
import com.stag.platform.codelist.repository.projection.HighSchoolAddressProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HighSchoolRepository extends JpaRepository<HighSchool, String> {

    @Query(
        """
        SELECT new com.stag.platform.codelist.repository.projection.HighSchoolAddressProjection(
            hs.name,
            hs.street,
            hs.zipCode,
            m.name,
            d.name
        )
        FROM
            HighSchool hs
        LEFT JOIN hs.municipality m
        LEFT JOIN m.district d
        WHERE
            hs.id = :id
        """
    )
    Optional<HighSchoolAddressProjection> findHighSchoolAddressById(String id);

}