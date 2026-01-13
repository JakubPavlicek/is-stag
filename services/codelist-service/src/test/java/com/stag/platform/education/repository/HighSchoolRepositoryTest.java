package com.stag.platform.education.repository;

import com.stag.platform.address.entity.District;
import com.stag.platform.address.entity.Municipality;
import com.stag.platform.address.entity.Region;
import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestOracleContainerConfig;
import com.stag.platform.education.entity.HighSchool;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestOracleContainerConfig.class, TestCacheConfig.class})
@ActiveProfiles("test")
class HighSchoolRepositoryTest {

    @Autowired
    private HighSchoolRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findHighSchoolAddressById should return address projection with hierarchy")
    void findHighSchoolAddressById_ReturnsProjection() {
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

        HighSchool highSchool = new HighSchool();
        highSchool.setId("123456789");
        highSchool.setName("Gymnázium Plzeň");
        highSchool.setStreet("Masarykova");
        highSchool.setZipCode("30100");
        highSchool.setMunicipality(municipality);
        highSchool.setValidFrom(LocalDate.of(2000, 1, 1));
        highSchool.setOwner("TEST");
        highSchool.setDateOfInsert(LocalDate.now());
        entityManager.persist(highSchool);

        entityManager.flush();

        Optional<HighSchoolAddressProjection> result = repository.findHighSchoolAddressById("123456789");

        assertThat(result).isPresent();
        HighSchoolAddressProjection projection = result.get();
        assertThat(projection.name()).isEqualTo("Gymnázium Plzeň");
        assertThat(projection.street()).isEqualTo("Masarykova");
        assertThat(projection.zipCode()).isEqualTo("30100");
        assertThat(projection.municipality()).isEqualTo("Plzeň");
        assertThat(projection.district()).isEqualTo("Plzeň-město");
    }

    @Test
    @DisplayName("findHighSchoolAddressById should return empty when high school not found")
    void findHighSchoolAddressById_NotFound_ReturnsEmpty() {
        Optional<HighSchoolAddressProjection> result = repository.findHighSchoolAddressById("UNKNOWN");

        assertThat(result).isEmpty();
    }
}
