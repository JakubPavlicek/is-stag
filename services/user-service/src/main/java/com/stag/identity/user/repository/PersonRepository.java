package com.stag.identity.user.repository;

import com.stag.identity.user.entity.Person;
import com.stag.identity.user.repository.projection.PersonAddressProjection;
import com.stag.identity.user.repository.projection.PersonBankProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    <T> Optional<T> findById(Integer id, Class<T> clazz);

    @Query(
        """
        SELECT new com.stag.identity.user.repository.projection.PersonAddressProjection(
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
    Optional<PersonAddressProjection> findAddressesByPersonId(Integer personId);

    @Query(
        """
        SELECT new com.stag.identity.user.repository.projection.PersonBankProjection(
            p.accountOwner,
            p.accountAddress,
            p.accountPrefix,
            p.accountSuffix,
            p.accountBank,
            p.accountIban,
            p.accountCurrency,

            p.euroAccountOwner,
            p.euroAccountAddress,
            p.euroAccountPrefix,
            p.euroAccountSuffix,
            p.euroAccountBank,
            p.euroAccountIban,
            p.euroAccountCurrency,
            CAST(p.euroAccountCountryId AS Integer),
            p.euroAccountSwift
        )
        FROM
            Person p
        WHERE p.id = :personId
        """
    )
    Optional<PersonBankProjection> findBankingByPersonId(Integer personId);

}
