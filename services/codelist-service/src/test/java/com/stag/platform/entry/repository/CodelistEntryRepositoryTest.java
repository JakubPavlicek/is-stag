package com.stag.platform.entry.repository;

import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestOracleContainerConfig;
import com.stag.platform.entry.entity.CodelistEntry;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.entity.Domain;
import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
import com.stag.platform.entry.repository.projection.DomainValueView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class })
@ActiveProfiles("test")
class CodelistEntryRepositoryTest {

    @Autowired
    private CodelistEntryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Domain domain;

    @BeforeEach
    void setUp() {
        domain = Domain.builder().domainId("TEST_DOMAIN").description("Test Domain").build();
        entityManager.persist(domain);
    }

    @Nested
    @DisplayName("findDomainValuesByDomain")
    class FindDomainValuesByDomain {

        @Test
        @DisplayName("should return valid entries sorted by meaning")
        void shouldReturnValidEntriesSortedByMeaning() {
            createEntry("A", "Z_Meaning", "A_En", "A", (short) 2, "A");
            createEntry("B", "A_Meaning", "B_En", "B", (short) 1, "A");
            createEntry("C", "C_Meaning", "C_En", "C", (short) 3, "N");

            List<DomainValueView> result = repository.findDomainValuesByDomain("TEST_DOMAIN", "cs");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).key()).isEqualTo("B");
            assertThat(result.get(0).name()).isEqualTo("A_Meaning");
            assertThat(result.get(1).key()).isEqualTo("A");
            assertThat(result.get(1).name()).isEqualTo("Z_Meaning");
        }

        @Test
        @DisplayName("should use English meaning when language is en")
        void shouldUseEnglishMeaningWhenLanguageIsEn() {
            createEntry("A", "Czech", "English", "Abr", (short) 1, "A");

            List<DomainValueView> result = repository.findDomainValuesByDomain("TEST_DOMAIN", "en");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst()
                             .name()).isEqualTo("English");
        }

        @Test
        @DisplayName("should fallback to Czech meaning when English is missing")
        void shouldFallbackToCzechMeaningWhenEnglishIsMissing() {
            createEntry("A", "Czech", null, "Abr", (short) 1, "A");

            List<DomainValueView> result = repository.findDomainValuesByDomain("TEST_DOMAIN", "en");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().name()).isEqualTo("Czech");
        }

        @Test
        @DisplayName("should sort by English meaning when language is en")
        void shouldSortByEnglishMeaning() {
            createEntry("1", "Z_Cz", "A_En", "1", (short) 1, "A");
            createEntry("2", "A_Cz", "Z_En", "2", (short) 2, "A");

            List<DomainValueView> result = repository.findDomainValuesByDomain("TEST_DOMAIN", "en");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).key()).isEqualTo("1");
            assertThat(result.get(1).key()).isEqualTo("2");
        }

        @Test
        @DisplayName("should fallback to Czech for sorting when English is missing")
        void shouldSortByFallbackMeaning() {
            createEntry("1", "B_Cz", null, "1", (short) 1, "A");
            createEntry("2", "A_Cz", "Z_En", "2", (short) 2, "A");

            List<DomainValueView> result = repository.findDomainValuesByDomain("TEST_DOMAIN", "en");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).key()).isEqualTo("1");
            assertThat(result.get(1).key()).isEqualTo("2");
        }

    }

    @Nested
    @DisplayName("findCodelistEntriesByIds")
    class FindCodelistEntriesByIds {

        @Test
        @DisplayName("should return entries with correct meaning for language")
        void shouldReturnEntriesWithCorrectMeaning() {
            createEntry("VAL1", "Cz1", "En1", "A1", (short) 1, "A");
            createEntry("VAL2", "Cz2", null, "A2", (short) 2, "A");

            List<CodelistEntryId> ids = List.of(
                new CodelistEntryId("TEST_DOMAIN", "VAL1", "STA"),
                new CodelistEntryId("TEST_DOMAIN", "VAL2", "STA")
            );

            List<CodelistEntryMeaningProjection> resultEn = repository.findCodelistEntriesByIds(ids, "en");
            List<CodelistEntryMeaningProjection> resultCs = repository.findCodelistEntriesByIds(ids, "cs");

            assertThat(resultEn).hasSize(2);
            assertThat(resultEn).extracting(CodelistEntryMeaningProjection::meaning).containsExactlyInAnyOrder("En1", "Cz2");

            assertThat(resultCs).hasSize(2);
            assertThat(resultCs).extracting(CodelistEntryMeaningProjection::meaning).containsExactlyInAnyOrder("Cz1", "Cz2");
        }

    }

    private void createEntry(String lowValue, String cz, String en, String abbr, short order, String isValid) {
        CodelistEntry entry = CodelistEntry.builder()
                                           .id(new CodelistEntryId(domain.getDomainId(), lowValue, "STA"))
                                           .domain(domain)
                                           .meaningCz(cz)
                                           .meaningEn(en)
                                           .abbreviation(abbr)
                                           .order(order)
                                           .isValid(isValid)
                                           .isDeleted("N")
                                           .isUpdated("N")
                                           .isGlobal("A")
                                           .owner("TEST")
                                           .insertedAt(LocalDate.now())
                                           .build();
        entityManager.persist(entry);
        entityManager.flush();
    }

}
