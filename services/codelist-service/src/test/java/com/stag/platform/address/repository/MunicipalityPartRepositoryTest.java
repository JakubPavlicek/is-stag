package com.stag.platform.address.repository;

import com.stag.platform.address.entity.District;
import com.stag.platform.address.entity.Municipality;
import com.stag.platform.address.entity.MunicipalityPart;
import com.stag.platform.address.entity.Region;
import com.stag.platform.address.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestOracleContainerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestOracleContainerConfig.class, TestCacheConfig.class})
@ActiveProfiles("test")
class MunicipalityPartRepositoryTest {

    @Autowired
    private MunicipalityPartRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findAddressNamesByIds should return projections with hierarchy names")
    void findAddressNamesByIds_ReturnsProjections() {
        Region region = new Region();
        region.setId((short) 1);
        region.setName("Plzeňský kraj");
        region.setNuts3("CZ032");
        region.setAbbreviation("PK");
        region.setStatus(true);
        entityManager.persist(region);

        District district = new District();
        district.setId(10);
        district.setName("Plzeň-město");
        district.setNuts4("CZ0323");
        district.setAbbreviation("PM");
        district.setStatus(true);
        district.setRegionCode(region);
        entityManager.persist(district);

        Municipality municipality = new Municipality();
        municipality.setId(100L);
        municipality.setName("Plzeň");
        municipality.setNuts5("CZ0323");
        municipality.setAbbreviation("PL");
        municipality.setStatus(true);
        municipality.setDistrict(district);
        entityManager.persist(municipality);

        MunicipalityPart part1 = new MunicipalityPart();
        part1.setId(1000L);
        part1.setName("Bory");
        part1.setMunicipality(municipality);
        part1.setAbbreviation("BO");
        part1.setStatus(true);
        entityManager.persist(part1);

        MunicipalityPart part2 = new MunicipalityPart();
        part2.setId(2000L);
        part2.setName("Slovany");
        part2.setMunicipality(municipality);
        part2.setAbbreviation("SL");
        part2.setStatus(true);
        entityManager.persist(part2);

        entityManager.flush();

        List<AddressPlaceNameProjection> results = repository.findAddressNamesByIds(List.of(1000L, 2000L));

        assertThat(results).hasSize(2);
        
        assertThat(results)
            .extracting(AddressPlaceNameProjection::municipalityPartId)
            .containsExactlyInAnyOrder(1000L, 2000L);

        assertThat(results)
            .extracting(AddressPlaceNameProjection::municipalityPartName)
            .containsExactlyInAnyOrder("Bory", "Slovany");
            
        assertThat(results)
            .extracting(AddressPlaceNameProjection::municipalityName)
            .containsOnly("Plzeň");
            
        assertThat(results)
            .extracting(AddressPlaceNameProjection::districtName)
            .containsOnly("Plzeň-město");
    }

    @Test
    @DisplayName("findAddressNamesByIds should return empty list when no IDs found")
    void findAddressNamesByIds_NotFound_ReturnsEmpty() {
        List<AddressPlaceNameProjection> results = repository.findAddressNamesByIds(List.of(9999L));

        assertThat(results).isEmpty();
    }
}
