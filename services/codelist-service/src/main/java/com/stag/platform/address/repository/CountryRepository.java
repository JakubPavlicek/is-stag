package com.stag.platform.address.repository;

import com.stag.platform.address.entity.Country;
import com.stag.platform.address.repository.projection.CountryNameProjection;
import com.stag.platform.address.repository.projection.CountryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    @Query(
        """
        SELECT new com.stag.platform.address.repository.projection.CountryView(
            c.id,
            CASE
                WHEN :language = 'en' THEN c.englishName
                ELSE c.name
            END AS name,
            CASE
                WHEN :language = 'en' THEN c.commonNameEn
                ELSE c.commonNameCz
            END AS commonName,
            c.abbreviation
        )
        FROM
            Country c
        WHERE
            c.validTo IS NULL
        ORDER BY
            c.abbreviation
        """
    )
    Set<CountryView> findAllValidCountries(String language);

    @Query(
        """
        SELECT
            c.id,
            CASE
                WHEN :language = 'en' THEN c.englishName
                ELSE c.name
            END AS name
        FROM
            Country c
        WHERE
            c.id IN :ids
        """
    )
    List<CountryNameProjection> findNamesByIds(Collection<Integer> ids, String language);

}
