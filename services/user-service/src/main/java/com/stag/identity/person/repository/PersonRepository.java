package com.stag.identity.person.repository;

import com.stag.identity.person.entity.Person;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.repository.projection.EducationView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/// **Person Repository**
///
/// Data access layer for person entities. Provides methods to retrieve
/// person data with dynamic projection support and specialized queries
/// for addresses, banking, and education information.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public interface PersonRepository extends JpaRepository<Person, Integer> {

    /// Finds person by ID with dynamic projection.
    ///
    /// @param id the person identifier
    /// @param clazz the projection class type
    /// @param <T> the projection type
    /// @return optional person data projected to specified type
    <T> Optional<T> findById(Integer id, Class<T> clazz);

    /// Finds comprehensive address data for a person including domicile,
    /// temporary, and foreign addresses.
    ///
    /// @param personId the person identifier
    /// @return optional address view with all address types
    @Query(
        """
        SELECT new com.stag.identity.person.repository.projection.AddressView(
           p.street,
           p.streetNumber,
           p.domicileZipCode,
           CAST(p.domicileMunicipalityPartId AS Long),
           CAST(p.domicileCountryId AS Integer),

           p.temporaryStreet,
           p.temporaryStreetNumber,
           p.temporaryZipCode,
           CAST(p.temporaryMunicipalityPartId AS Long),
           CAST(p.temporaryCountryId AS Integer),

           p.zipCodeForeign,
           p.municipalityForeign,
           p.districtForeign,
           p.postOfficeForeign,

           p.temporaryZipCodeForeign,
           p.temporaryMunicipalityForeign,
           p.temporaryDistrictForeign,
           p.temporaryPostOfficeForeign
        )
        FROM
           Person p
        WHERE
           p.id = :personId
        """
    )
    Optional<AddressView> findAddressesByPersonId(Integer personId);

    /// Finds banking information for a person including standard
    /// and Euro accounts.
    ///
    /// @param personId the person identifier
    /// @return optional bank view with account details
    @Query(
        """
        SELECT new com.stag.identity.person.repository.projection.BankView(
            p.accountHolder,
            p.accountAddress,
            p.accountPrefix,
            p.accountSuffix,
            p.bankCode,
            p.accountIban,
            p.accountCurrency,

            p.euroAccountHolder,
            p.euroAccountAddress,
            p.euroAccountPrefix,
            p.euroAccountSuffix,
            p.euroBankCode,
            p.euroAccountIban,
            p.euroAccountCurrency,
            CAST(p.euroAccountCountryId AS Integer),
            p.euroAccountSwiftCode
        )
        FROM
            Person p
        WHERE
            p.id = :personId
        """
    )
    Optional<BankView> findBankingByPersonId(Integer personId);

    /// Finds education information for a person including high school
    /// details and field of study.
    ///
    /// @param personId the person identifier
    /// @return optional education view with school information
    @Query(
        """
        SELECT new com.stag.identity.person.repository.projection.EducationView(
            p.highSchoolId,
            p.highSchoolFieldOfStudyNumber,
            CAST(p.highSchoolCountryId AS Integer),
            p.graduationDate,

            p.highSchoolForeign,
            p.highSchoolForeignPlace,
            p.highSchoolForeignFieldOfStudy
        )
        FROM
            Person p
        WHERE
            p.id = :personId
        """
    )
    Optional<EducationView> findEducationByPersonId(Integer personId);

}
