package com.stag.platform.address.repository;

import com.stag.platform.address.entity.AddressPoint;
import com.stag.platform.address.repository.projection.AddressSuggestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressPointRepository extends JpaRepository<AddressPoint, Long> {

    @Query(
        """
        SELECT new com.stag.platform.address.repository.projection.AddressSuggestion(
            ap.id,
            TRIM(BOTH ' ' FROM TRIM(BOTH ',' FROM CONCAT(
                COALESCE(s.name, ''),
                CASE WHEN ap.houseNumber IS NOT NULL THEN CONCAT(' ', ap.houseNumber) ELSE '' END,
                CASE WHEN ap.orientationNumber IS NOT NULL THEN CONCAT('/', ap.orientationNumber, COALESCE(ap.orientationNumberLetter, '')) ELSE '' END,
                CASE WHEN mp.name IS NOT NULL AND mp.name <> m.name THEN CONCAT(', ', mp.name) ELSE '' END,
                CASE WHEN m.name IS NOT NULL THEN CONCAT(', ', m.name) ELSE '' END,
                CASE WHEN ap.zipCode IS NOT NULL THEN CONCAT(' ', ap.zipCode) ELSE '' END
            )))
        )
        FROM AddressPoint ap
        LEFT JOIN ap.street s
        LEFT JOIN ap.municipalityPart mp
        LEFT JOIN ap.municipality m
        WHERE LOWER(
            CONCAT(
                COALESCE(s.name, ''),
                CASE WHEN ap.houseNumber IS NOT NULL THEN CONCAT(' ', ap.houseNumber) ELSE '' END,
                CASE WHEN ap.orientationNumber IS NOT NULL THEN CONCAT('/', ap.orientationNumber, COALESCE(ap.orientationNumberLetter, '')) ELSE '' END,
                CASE WHEN mp.name IS NOT NULL AND mp.name <> m.name THEN CONCAT(', ', mp.name) ELSE '' END,
                CASE WHEN m.name IS NOT NULL THEN CONCAT(', ', m.name) ELSE '' END,
                CASE WHEN ap.zipCode IS NOT NULL THEN CONCAT(' ', ap.zipCode) ELSE '' END
            )
        ) LIKE LOWER(CONCAT('%', :query, '%'))
        """
    )
    List<AddressSuggestion> findAddressSuggestions(String query, Pageable pageable);
}
