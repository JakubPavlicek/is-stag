package com.stag.platform.education.repository;

import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestOracleContainerConfig;
import com.stag.platform.education.entity.HighSchoolFieldOfStudy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestOracleContainerConfig.class, TestCacheConfig.class})
@ActiveProfiles("test")
class HighSchoolFieldOfStudyRepositoryTest {

    @Autowired
    private HighSchoolFieldOfStudyRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findNameById should return name when found")
    void findNameById_Found_ReturnsName() {
        HighSchoolFieldOfStudy field = new HighSchoolFieldOfStudy();
        field.setId("79-41-K/41");
        field.setName("Gymnázium");
        entityManager.persist(field);
        entityManager.flush();

        Optional<String> result = repository.findNameById("79-41-K/41");

        assertThat(result).isPresent().contains("Gymnázium");
    }

    @Test
    @DisplayName("findNameById should return empty when not found")
    void findNameById_NotFound_ReturnsEmpty() {
        Optional<String> result = repository.findNameById("UNKNOWN");

        assertThat(result).isEmpty();
    }
}
