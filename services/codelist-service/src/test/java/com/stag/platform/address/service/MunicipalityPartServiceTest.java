package com.stag.platform.address.service;

import com.stag.platform.address.exception.MunicipalityPartsNotFoundException;
import com.stag.platform.address.repository.MunicipalityPartRepository;
import com.stag.platform.address.repository.projection.AddressPlaceNameProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MunicipalityPartServiceTest {

    @Mock
    private MunicipalityPartRepository municipalityPartRepository;

    @InjectMocks
    private MunicipalityPartService municipalityPartService;

    @Test
    @DisplayName("should return address names when all IDs found")
    void findAddressNamesByIds_AllFound_ReturnsMap() {
        List<Long> ids = List.of(100L, 200L);
        AddressPlaceNameProjection place1 = new AddressPlaceNameProjection(100L, "Mesto1", "Cast1", "Okres1");
        AddressPlaceNameProjection place2 = new AddressPlaceNameProjection(200L, "Mesto2", "Cast2", "Okres2");

        when(municipalityPartRepository.findAddressNamesByIds(ids)).thenReturn(List.of(place1, place2));

        Map<Long, AddressPlaceNameProjection> result = municipalityPartService.findAddressNamesByIds(ids);

        assertThat(result).hasSize(2)
                          .containsEntry(100L, place1)
                          .containsEntry(200L, place2);
        verify(municipalityPartRepository).findAddressNamesByIds(ids);
    }

    @Test
    @DisplayName("should throw MunicipalityPartsNotFoundException when some IDs not found")
    void findAddressNamesByIds_SomeNotFound_ThrowsException() {
        List<Long> ids = List.of(100L, 999L);
        AddressPlaceNameProjection place1 = new AddressPlaceNameProjection(100L, "Mesto1", "Cast1", "Okres1");

        when(municipalityPartRepository.findAddressNamesByIds(ids)).thenReturn(List.of(place1));

        assertThatThrownBy(() -> municipalityPartService.findAddressNamesByIds(ids))
            .isInstanceOf(MunicipalityPartsNotFoundException.class)
            .hasMessageContaining("Unable to find municipality parts for IDs: [999]")
            .extracting("missingIds").isEqualTo(List.of(999L));

        verify(municipalityPartRepository).findAddressNamesByIds(ids);
    }

    @Test
    @DisplayName("should return empty map when requested IDs is empty")
    void findAddressNamesByIds_EmptyIds_ReturnsEmptyMap() {
        List<Long> ids = Collections.emptyList();
        when(municipalityPartRepository.findAddressNamesByIds(ids)).thenReturn(Collections.emptyList());

        Map<Long, AddressPlaceNameProjection> result = municipalityPartService.findAddressNamesByIds(ids);

        assertThat(result).isEmpty();
        verify(municipalityPartRepository).findAddressNamesByIds(ids);
    }
}
