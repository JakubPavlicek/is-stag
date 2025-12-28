package com.stag.platform.address.repository;

import com.stag.platform.address.entity.Country;
import com.stag.platform.address.repository.projection.CountryNameProjection;
import com.stag.platform.address.repository.projection.CountryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/// **Country Repository**
///
/// Data access layer for country entities with custom query methods.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public interface CountryRepository extends JpaRepository<Country, Integer> {

    /// Retrieves all valid countries with localized names.
    ///
    /// @param language the language code for name localization (en or cs)
    /// @return set of country views with localized data
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

    /// Finds country names by their IDs with language-specific localization.
    ///
    /// @param ids collection of country IDs to fetch
    /// @param language the language code for name localization (en or cs)
    /// @return list of country name projections
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

    /// Finds a country ID by its Czech or English name.
    ///
    /// @param countryName the country name to search for
    /// @return optional containing the country ID if found
    @Query("SELECT c.id FROM Country c WHERE c.name = :countryName OR c.englishName = :countryName")
    Optional<Integer> findCountryIdByName(String countryName);

}
