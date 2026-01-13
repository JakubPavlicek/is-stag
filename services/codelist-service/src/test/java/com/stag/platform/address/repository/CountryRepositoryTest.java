package com.stag.platform.address.repository;

import com.stag.platform.address.entity.Country;
import com.stag.platform.address.repository.projection.CountryNameProjection;
import com.stag.platform.address.repository.projection.CountryView;
import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestOracleContainerConfig;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestOracleContainerConfig.class, TestCacheConfig.class})
@ActiveProfiles("test")
class CountryRepositoryTest {

    @Autowired
    private CountryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("findAllValidCountries")
    class FindAllValidCountries {

        @Test
        @DisplayName("should return valid countries sorted by abbreviation")
        void shouldReturnValidCountriesSorted() {
            createCountry(203, "Česká republika", "CZ", "Czech Republic", "Česko", "Czechia", null);
            createCountry(703, "Slovensko", "SK", "Slovakia", "Slovensko", "Slovakia", null);
            createCountry(999, "Invalid Country", "XX", "Invalid", "Inv", "Inv", LocalDate.now().minusDays(1));

            Set<CountryView> results = repository.findAllValidCountries("cs");

            assertThat(results).hasSize(2);
            assertThat(results).extracting(CountryView::abbreviation)
                               .containsExactly("CZ", "SK");
        }

        @Test
        @DisplayName("should return localized names for English")
        void shouldReturnLocalizedNamesEn() {
            createCountry(203, "Česká republika", "CZ", "Czech Republic", "Česko", "Czechia", null);

            Set<CountryView> results = repository.findAllValidCountries("en");

            assertThat(results).hasSize(1);
            CountryView view = results.iterator().next();
            assertThat(view.name()).isEqualTo("Czech Republic");
            assertThat(view.commonName()).isEqualTo("Czechia");
        }

        @Test
        @DisplayName("should return localized names for Czech")
        void shouldReturnLocalizedNamesCs() {
            createCountry(203, "Česká republika", "CZ", "Czech Republic", "Česko", "Czechia", null);

            Set<CountryView> results = repository.findAllValidCountries("cs");

            assertThat(results).hasSize(1);
            CountryView view = results.iterator().next();
            assertThat(view.name()).isEqualTo("Česká republika");
            assertThat(view.commonName()).isEqualTo("Česko");
        }
    }

    @Nested
    @DisplayName("findNamesByIds")
    class FindNamesByIds {

        @Test
        @DisplayName("should return names by IDs with localization")
        void shouldReturnNamesByIds() {
            createCountry(203, "Česká republika", "CZ", "Czech Republic", null, null, null);
            createCountry(703, "Slovensko", "SK", "Slovakia", null, null, null);

            List<CountryNameProjection> resultsEn = repository.findNamesByIds(List.of(203, 703), "en");
            List<CountryNameProjection> resultsCs = repository.findNamesByIds(List.of(203, 703), "cs");

            assertThat(resultsEn).extracting(CountryNameProjection::name)
                                 .containsExactlyInAnyOrder("Czech Republic", "Slovakia");

            assertThat(resultsCs).extracting(CountryNameProjection::name)
                                 .containsExactlyInAnyOrder("Česká republika", "Slovensko");
        }
    }

    @Nested
    @DisplayName("findCountryIdByName")
    class FindCountryIdByName {

        @Test
        @DisplayName("should find ID by Czech name")
        void shouldFindIdByCzechName() {
            createCountry(203, "Česká republika", "CZ", "Czech Republic", null, null, null);

            Optional<Integer> result = repository.findCountryIdByName("Česká republika");

            assertThat(result).isPresent().contains(203);
        }

        @Test
        @DisplayName("should find ID by English name")
        void shouldFindIdByEnglishName() {
            createCountry(203, "Česká republika", "CZ", "Czech Republic", null, null, null);

            Optional<Integer> result = repository.findCountryIdByName("Czech Republic");

            assertThat(result).isPresent().contains(203);
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            Optional<Integer> result = repository.findCountryIdByName("Unknown");

            assertThat(result).isEmpty();
        }
    }

    private void createCountry(Integer id, String name, String abbr, String nameEn, String commonCz, String commonEn, LocalDate validTo) {
        Country country = new Country();
        country.setId(id);
        country.setName(name);
        country.setAbbreviation(abbr);
        country.setEnglishName(nameEn);
        country.setCommonNameCz(commonCz);
        country.setCommonNameEn(commonEn);
        country.setValidTo(validTo);
        country.setValidFrom(LocalDate.of(2000, 1, 1));
        country.setIsCommon("A");
        country.setIsInEu("A");
        country.setIsRisky("N");
        entityManager.persist(country);
        entityManager.flush();
    }
}
