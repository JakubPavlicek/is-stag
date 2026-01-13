package com.stag.platform.entry.repository.specification;

import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestOracleContainerConfig;
import com.stag.platform.entry.entity.CodelistEntry;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.entity.Domain;
import com.stag.platform.entry.repository.CodelistEntryRepository;
import com.stag.platform.shared.grpc.model.CodelistDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestOracleContainerConfig.class, TestCacheConfig.class})
@ActiveProfiles("test")
class CodelistEntrySpecificationTest {

    @Autowired
    private CodelistEntryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        createDomain(CodelistDomain.STAV.name());
        createDomain(CodelistDomain.TITUL_PRED.name());
        createDomain(CodelistDomain.TITUL_ZA.name());
        createDomain("OTHER");
    }

    @Test
    @DisplayName("should match marital status by abbreviation")
    void shouldMatchMaritalStatusByAbbreviation() {
        createEntry(CodelistDomain.STAV.name(), "S", "Svobodný", "Single", "S");
        createEntry(CodelistDomain.STAV.name(), "M", "Ženatý", "Married", "M");

        Specification<CodelistEntry> spec = CodelistEntrySpecification.byPersonProfileCriteria("S", null, null);
        List<CodelistEntry> results = repository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getId().getLowValue()).isEqualTo("S");
    }

    @Test
    @DisplayName("should match title prefix by Czech meaning")
    void shouldMatchTitlePrefixByCzechMeaning() {
        createEntry(CodelistDomain.TITUL_PRED.name(), "Ing.", "Inženýr", "Engineer", "Ing.");
        createEntry(CodelistDomain.TITUL_PRED.name(), "Mgr.", "Magistr", "Master", "Mgr.");

        Specification<CodelistEntry> spec = CodelistEntrySpecification.byPersonProfileCriteria(null, "Inženýr", null);
        List<CodelistEntry> results = repository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getId().getLowValue()).isEqualTo("Ing.");
    }

    @Test
    @DisplayName("should match title suffix by English meaning")
    void shouldMatchTitleSuffixByEnglishMeaning() {
        createEntry(CodelistDomain.TITUL_ZA.name(), "Ph.D.", "Doktor", "Doctor", "Ph.D.");
        createEntry(CodelistDomain.TITUL_ZA.name(), "CSc.", "Kandidát", "Candidate", "CSc.");

        Specification<CodelistEntry> spec = CodelistEntrySpecification.byPersonProfileCriteria(null, null, "Doctor");
        List<CodelistEntry> results = repository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getId().getLowValue()).isEqualTo("Ph.D.");
    }

    @Test
    @DisplayName("should match multiple criteria (OR logic)")
    void shouldMatchMultipleCriteria() {
        createEntry(CodelistDomain.STAV.name(), "S", "Svobodný", "Single", "S");
        createEntry(CodelistDomain.TITUL_PRED.name(), "Ing.", "Inženýr", "Engineer", "Ing.");
        createEntry(CodelistDomain.TITUL_ZA.name(), "Ph.D.", "Doktor", "Doctor", "Ph.D.");

        Specification<CodelistEntry> spec = CodelistEntrySpecification.byPersonProfileCriteria("S", "Inženýr", null);
        List<CodelistEntry> results = repository.findAll(spec);

        assertThat(results).hasSize(2);
        assertThat(results)
            .extracting(e -> e.getId().getLowValue())
            .containsExactlyInAnyOrder("S", "Ing.");
    }

    @Test
    @DisplayName("should ignore null or blank criteria")
    void shouldIgnoreNullOrBlankCriteria() {
        createEntry(CodelistDomain.STAV.name(), "S", "Svobodný", "Single", "S");

        Specification<CodelistEntry> spec = CodelistEntrySpecification.byPersonProfileCriteria("S", "", "   ");
        List<CodelistEntry> results = repository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getId().getLowValue()).isEqualTo("S");
    }

    @Test
    @DisplayName("should return empty list when no criteria matches")
    void shouldReturnEmptyListWhenNoCriteriaMatches() {
        createEntry(CodelistDomain.STAV.name(), "S", "Svobodný", "Single", "S");

        Specification<CodelistEntry> spec = CodelistEntrySpecification.byPersonProfileCriteria("X", "Y", "Z");
        List<CodelistEntry> results = repository.findAll(spec);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should return empty list when all criteria are null")
    void shouldReturnEmptyListWhenAllCriteriaAreNull() {
        createEntry(CodelistDomain.STAV.name(), "S", "Svobodný", "Single", "S");

        Specification<CodelistEntry> spec = CodelistEntrySpecification.byPersonProfileCriteria(null, null, null);
        List<CodelistEntry> results = repository.findAll(spec);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should not match correct meaning in wrong domain")
    void shouldNotMatchCorrectMeaningInWrongDomain() {
        createEntry("OTHER", "S", "Svobodný", "Single", "S");

        Specification<CodelistEntry> spec = CodelistEntrySpecification.byPersonProfileCriteria("S", null, null);
        List<CodelistEntry> results = repository.findAll(spec);

        assertThat(results).isEmpty();
    }

    private void createDomain(String domainId) {
        Domain domain = Domain.builder().domainId(domainId).description("Desc").build();
        entityManager.persist(domain);
    }

    private void createEntry(String domainId, String lowValue, String cz, String en, String abbr) {
        Domain domain = entityManager.find(Domain.class, domainId);
        CodelistEntry entry = CodelistEntry.builder()
            .id(new CodelistEntryId(domainId, lowValue, "STA"))
            .domain(domain)
            .meaningCz(cz)
            .meaningEn(en)
            .abbreviation(abbr)
            .isValid("A")
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
