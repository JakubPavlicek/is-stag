package com.stag.platform.codelist.repository;

import com.stag.platform.codelist.entity.MunicipalityPart;
import com.stag.platform.codelist.repository.projection.AddressPlaceNameProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MunicipalityPartRepository extends JpaRepository<MunicipalityPart, Long> {

    @Query(
        """
        SELECT new com.stag.platform.codelist.repository.projection.AddressPlaceNameProjection(
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
