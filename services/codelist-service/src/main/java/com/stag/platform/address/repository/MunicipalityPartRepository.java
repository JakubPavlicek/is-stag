package com.stag.platform.address.repository;

import com.stag.platform.address.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.address.entity.MunicipalityPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/// **Municipality Part Repository**
///
/// Data access layer for municipality part entities.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public interface MunicipalityPartRepository extends JpaRepository<MunicipalityPart, Long> {

    /// Retrieves address place names (municipality part, municipality, district) by IDs.
    ///
    /// @param ids collection of municipality part IDs to fetch
    /// @return list of address place name projections
    @Query(
        """
        SELECT new com.stag.platform.address.repository.projection.AddressPlaceNameProjection(
            CAST(mp.id AS Long),
            mp.name,
            m.name,
            d.name
        )
        FROM
            MunicipalityPart mp
        INNER JOIN mp.municipality m
        INNER JOIN m.district d
        WHERE
            mp.id IN :ids
        """
    )
    List<AddressPlaceNameProjection> findAddressNamesByIds(Collection<Long> ids);

}
