package com.stag.platform.address.service;

import com.stag.platform.address.exception.CountriesNotFoundException;
import com.stag.platform.address.exception.CountryNotFoundException;
import com.stag.platform.address.repository.CountryRepository;
import com.stag.platform.address.repository.projection.CountryNameProjection;
import com.stag.platform.address.repository.projection.CountryView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @Test
    @DisplayName("should return all countries in requested language")
    void getCountries_ReturnsCountries() {
        String language = "cs";
        CountryView country = new CountryView(203, "Česká republika", "Česko", "CZ");
        when(countryRepository.findAllValidCountries(language)).thenReturn(Set.of(country));

        Set<CountryView> result = countryService.getCountries(language);

        assertThat(result).containsExactly(country);
        verify(countryRepository).findAllValidCountries(language);
    }

    @Test
    @DisplayName("should return country ID when found by name")
    void findCountryIdByName_CountryExists_ReturnsId() {
        String countryName = "Czech Republic";
        Integer countryId = 203;
        when(countryRepository.findCountryIdByName(countryName)).thenReturn(Optional.of(countryId));

        Integer result = countryService.findCountryIdByName(countryName);

        assertThat(result).isEqualTo(countryId);
        verify(countryRepository).findCountryIdByName(countryName);
    }

    @Test
    @DisplayName("should return null when country name is null or blank")
    void findCountryIdByName_NullOrBlankName_ReturnsNull() {
        assertThat(countryService.findCountryIdByName(null)).isNull();
        assertThat(countryService.findCountryIdByName("")).isNull();
        assertThat(countryService.findCountryIdByName("   ")).isNull();
    }

    @Test
    @DisplayName("should throw CountryNotFoundException when country not found by name")
    void findCountryIdByName_CountryNotFound_ThrowsException() {
        String countryName = "NonExistentCountry";
        when(countryRepository.findCountryIdByName(countryName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.findCountryIdByName(countryName))
            .isInstanceOf(CountryNotFoundException.class)
            .hasMessageContaining("Country not found: " + countryName)
            .extracting("countryName").isEqualTo(countryName);

        verify(countryRepository).findCountryIdByName(countryName);
    }

    @Test
    @DisplayName("should return map of country names when all IDs found")
    void findNamesByIds_AllFound_ReturnsMap() {
        List<Integer> ids = List.of(203, 204);
        String language = "en";
        CountryNameProjection cz = new CountryNameProjection(203, "Czech Republic");
        CountryNameProjection sk = new CountryNameProjection(204, "Slovakia");

        when(countryRepository.findNamesByIds(ids, language)).thenReturn(List.of(cz, sk));

        Map<Integer, String> result = countryService.findNamesByIds(ids, language);

        assertThat(result).hasSize(2)
                          .containsEntry(203, "Czech Republic")
                          .containsEntry(204, "Slovakia");
        verify(countryRepository).findNamesByIds(ids, language);
    }

    @Test
    @DisplayName("should throw CountriesNotFoundException when some IDs not found")
    void findNamesByIds_SomeNotFound_ThrowsException() {
        List<Integer> ids = List.of(203, 999);
        String language = "en";
        CountryNameProjection cz = new CountryNameProjection(203, "Czech Republic");

        when(countryRepository.findNamesByIds(ids, language)).thenReturn(List.of(cz));

        assertThatThrownBy(() -> countryService.findNamesByIds(ids, language))
            .isInstanceOf(CountriesNotFoundException.class)
            .hasMessageContaining("Unable to find countries for IDs: [999]")
            .extracting("missingIds").isEqualTo(List.of(999));

        verify(countryRepository).findNamesByIds(ids, language);
    }

    @Test
    @DisplayName("should return empty map when requested IDs is empty")
    void findNamesByIds_EmptyIds_ReturnsEmptyMap() {
        List<Integer> ids = Collections.emptyList();
        String language = "en";
        when(countryRepository.findNamesByIds(ids, language)).thenReturn(Collections.emptyList());

        Map<Integer, String> result = countryService.findNamesByIds(ids, language);

        assertThat(result).isEmpty();
        verify(countryRepository).findNamesByIds(ids, language);
    }
}
