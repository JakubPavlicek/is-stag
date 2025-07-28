package com.stag.identity.user.repository;

import com.stag.identity.user.repository.projection.AddressProjection;
import com.stag.identity.user.entity.Person;
import com.stag.identity.user.repository.projection.ForeignAddressProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    <T> Optional<T> findById(Integer id, Class<T> clazz);

    @Query(
        """
        SELECT new com.stag.identity.user.repository.projection.AddressProjection(
           'PERMANENT',
           p.street,
           p.streetNumber,
           p.domicileZipCode,
           CAST(p.domicileMunicipalityId AS Long),
           CAST(p.domicileMunicipalityPartId AS Long),
           CAST(p.domicileDistrictId AS Integer),
           CAST(p.domicileCountryId AS Integer)
        )
        FROM
           Person p
        WHERE
           p.id = :personId
        UNION ALL
        SELECT new com.stag.identity.user.repository.projection.AddressProjection(
           'TEMPORARY',
           p.temporaryStreet,
           p.temporaryStreetNumber,
           p.temporaryZipCode,
           CAST(p.temporaryMunicipalityId AS Long),
           CAST(p.temporaryMunicipalityPartId AS Long),
           CAST(p.temporaryDistrictId AS Integer),
           CAST(p.temporaryCountryId AS Integer)
        )
        FROM
           Person p
        WHERE
           p.id = :personId
        """
    )
    List<AddressProjection> findAddressesByPersonId(Integer personId);

    @Query(
        """
        SELECT new com.stag.identity.user.repository.projection.ForeignAddressProjection(
            'FOREIGN_PERMANENT',
            p.zipCodeForeign,
            p.municipalityForeign,
            p.districtForeign,
            p.postOfficeForeign
        )
        FROM
            Person p
        WHERE
            p.id = :personId
        UNION ALL
        SELECT new com.stag.identity.user.repository.projection.ForeignAddressProjection(
            'FOREIGN_TEMPORARY',
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
    List<ForeignAddressProjection> findForeignAddressesByPersonId(Integer personId);

}
