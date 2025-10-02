package com.stag.identity.person.repository;

import com.stag.identity.person.entity.Person;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.repository.projection.EducationView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    <T> Optional<T> findById(Integer id, Class<T> clazz);

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
