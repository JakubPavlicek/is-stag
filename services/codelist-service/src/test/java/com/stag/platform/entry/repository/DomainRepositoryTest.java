package com.stag.platform.entry.repository;

import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestOracleContainerConfig;
import com.stag.platform.entry.entity.Domain;
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
class DomainRepositoryTest {

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findAllDomainNames should return all domains sorted alphabetically")
    void findAllDomainNames_ReturnsSortedDomains() {
        Domain domainC = Domain.builder().domainId("C_DOMAIN").description("Desc C").build();
        Domain domainA = Domain.builder().domainId("A_DOMAIN").description("Desc A").build();
        Domain domainB = Domain.builder().domainId("B_DOMAIN").description("Desc B").build();

        entityManager.persist(domainC);
        entityManager.persist(domainA);
        entityManager.persist(domainB);
        entityManager.flush();

        List<String> result = domainRepository.findAllDomainNames();

        assertThat(result).containsExactly("A_DOMAIN", "B_DOMAIN", "C_DOMAIN");
    }

    @Test
    @DisplayName("findAllDomainNames should return empty list when no domains exist")
    void findAllDomainNames_NoDomains_ReturnsEmptyList() {
        List<String> result = domainRepository.findAllDomainNames();

        assertThat(result).isEmpty();
    }

}
