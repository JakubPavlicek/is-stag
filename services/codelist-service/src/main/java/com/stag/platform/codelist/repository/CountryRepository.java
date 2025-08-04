package com.stag.platform.codelist.repository;

import com.stag.platform.codelist.entity.Country;
import com.stag.platform.codelist.repository.projection.CountryNameProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {

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
