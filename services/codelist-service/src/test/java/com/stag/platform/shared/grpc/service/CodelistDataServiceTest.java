package com.stag.platform.shared.grpc.service;

import com.stag.platform.address.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.address.service.CountryService;
import com.stag.platform.address.service.MunicipalityPartService;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.GetPersonAddressDataRequest;
import com.stag.platform.codelist.v1.GetPersonBankingDataRequest;
import com.stag.platform.codelist.v1.GetPersonEducationDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileDataRequest;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import com.stag.platform.education.service.HighSchoolFieldOfStudyService;
import com.stag.platform.education.service.HighSchoolService;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
import com.stag.platform.entry.service.CodelistEntryService;
import com.stag.platform.entry.service.dto.PersonProfileLowValues;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodelistDataServiceTest {

    @Mock
    private CodelistEntryService codelistEntryService;
    @Mock
    private CountryService countryService;
    @Mock
    private MunicipalityPartService municipalityPartService;
    @Mock
    private HighSchoolService highSchoolService;
    @Mock
    private HighSchoolFieldOfStudyService highSchoolFieldOfStudyService;

    @InjectMocks
    private CodelistDataService dataService;

    @Nested
    @DisplayName("fetchCodelistMeanings")
    class FetchCodelistMeanings {

        @Test
        @DisplayName("should fetch and map meanings")
        void success() {
            List<CodelistKey> keys = List.of(
                CodelistKey.newBuilder().setDomain("D").setLowValue("V").build()
            );
            List<CodelistEntryMeaningProjection> projections = List.of(
                new CodelistEntryMeaningProjection(new CodelistEntryId("D", "V", "STA"), "Meaning")
            );

            when(codelistEntryService.findMeaningsByIds(any(), eq("en"))).thenReturn(projections);

            List<CodelistMeaning> result = dataService.fetchCodelistMeanings(keys, "en");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getMeaning()).isEqualTo("Meaning");
        }
    }

    @Nested
    @DisplayName("fetchCountryNames")
    class FetchCountryNames {

        @Test
        @DisplayName("should return empty map when no IDs present")
        void noIds() {
            GetPersonProfileDataRequest request = GetPersonProfileDataRequest.newBuilder().build();

            Map<Integer, String> result = dataService.fetchCountryNames(request, "en");

            assertThat(result).isEmpty();
            verifyNoInteractions(countryService);
        }

        @Test
        @DisplayName("should fetch names for profile data request")
        void profileDataRequest() {
            GetPersonProfileDataRequest request = GetPersonProfileDataRequest.newBuilder()
                .setBirthCountryId(1)
                .setCitizenshipCountryId(2)
                .build();

            when(countryService.findNamesByIds(argThat(ids -> ids.containsAll(Set.of(1, 2))), eq("en")))
                .thenReturn(Map.of(1, "C1", 2, "C2"));

            Map<Integer, String> result = dataService.fetchCountryNames(request, "en");

            assertThat(result).hasSize(2).containsEntry(1, "C1").containsEntry(2, "C2");
        }

        @Test
        @DisplayName("should fetch names for address data request")
        void addressDataRequest() {
            GetPersonAddressDataRequest request = GetPersonAddressDataRequest.newBuilder()
                .setPermanentCountryId(3)
                .setTemporaryCountryId(4)
                .build();

            when(countryService.findNamesByIds(argThat(ids -> ids.containsAll(Set.of(3, 4))), eq("en")))
                .thenReturn(Map.of(3, "C3", 4, "C4"));

            Map<Integer, String> result = dataService.fetchCountryNames(request, "en");

            assertThat(result).hasSize(2).containsEntry(3, "C3").containsEntry(4, "C4");
        }

        @Test
        @DisplayName("should fetch names for banking data request")
        void bankingDataRequest() {
            GetPersonBankingDataRequest request = GetPersonBankingDataRequest.newBuilder()
                .setEuroAccountCountryId(5)
                .build();

            when(countryService.findNamesByIds(argThat(ids -> ids.contains(5)), eq("en")))
                .thenReturn(Map.of(5, "C5"));

            Map<Integer, String> result = dataService.fetchCountryNames(request, "en");

            assertThat(result).hasSize(1).containsEntry(5, "C5");
        }

        @Test
        @DisplayName("should fetch names for education data request")
        void educationDataRequest() {
            GetPersonEducationDataRequest request = GetPersonEducationDataRequest.newBuilder()
                .setHighSchoolCountryId(6)
                .build();

            when(countryService.findNamesByIds(argThat(ids -> ids.contains(6)), eq("en")))
                .thenReturn(Map.of(6, "C6"));

            Map<Integer, String> result = dataService.fetchCountryNames(request, "en");

            assertThat(result).hasSize(1).containsEntry(6, "C6");
        }
    }

    @Nested
    @DisplayName("fetchCodelistLowValues")
    class FetchCodelistLowValues {

        @Test
        @DisplayName("should delegate to service")
        void delegate() {
            PersonProfileLowValues expected = new PersonProfileLowValues("S", "Ing", "PhD");
            when(codelistEntryService.findPersonProfileLowValues("S", "I", "P")).thenReturn(expected);

            PersonProfileLowValues result = dataService.fetchCodelistLowValues("S", "I", "P");

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("fetchCountryId")
    class FetchCountryId {

        @Test
        @DisplayName("should delegate to service")
        void delegate() {
            when(countryService.findCountryIdByName("Czechia")).thenReturn(203);

            Integer result = dataService.fetchCountryId("Czechia");

            assertThat(result).isEqualTo(203);
        }
    }

    @Nested
    @DisplayName("fetchAddressNames")
    class FetchAddressNames {

        @Test
        @DisplayName("should return empty map when no IDs present")
        void noIds() {
            GetPersonAddressDataRequest request = GetPersonAddressDataRequest.newBuilder().build();

            Map<Long, AddressPlaceNameProjection> result = dataService.fetchAddressNames(request);

            assertThat(result).isEmpty();
            verifyNoInteractions(municipalityPartService);
        }

        @Test
        @DisplayName("should fetch names for address request with multiple IDs")
        void withIds() {
            GetPersonAddressDataRequest request = GetPersonAddressDataRequest.newBuilder()
                .setPermanentMunicipalityPartId(10L)
                .setTemporaryMunicipalityPartId(20L)
                .build();

            AddressPlaceNameProjection proj1 = new AddressPlaceNameProjection(10L, "M1", "P1", "D1");
            AddressPlaceNameProjection proj2 = new AddressPlaceNameProjection(20L, "M2", "P2", "D2");

            when(municipalityPartService.findAddressNamesByIds(argThat(ids -> ids.containsAll(Set.of(10L, 20L)))))
                .thenReturn(Map.of(10L, proj1, 20L, proj2));

            Map<Long, AddressPlaceNameProjection> result = dataService.fetchAddressNames(request);

            assertThat(result).hasSize(2).containsEntry(10L, proj1).containsEntry(20L, proj2);
        }
    }

    @Nested
    @DisplayName("fetchHighSchoolAddress")
    class FetchHighSchoolAddress {

        @Test
        @DisplayName("should return null when ID not present")
        void noId() {
            HighSchoolAddressProjection result = dataService.fetchHighSchoolAddress(false, "123");

            assertThat(result).isNull();
            verifyNoInteractions(highSchoolService);
        }

        @Test
        @DisplayName("should fetch address when ID present")
        void withId() {
            HighSchoolAddressProjection expected = new HighSchoolAddressProjection("N", "S", "Z", "M", "D");
            when(highSchoolService.findHighSchoolAddressById("123")).thenReturn(expected);

            HighSchoolAddressProjection result = dataService.fetchHighSchoolAddress(true, "123");

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("fetchHighSchoolFieldOfStudy")
    class FetchHighSchoolFieldOfStudy {

        @Test
        @DisplayName("should return null when number not present")
        void noNumber() {
            String result = dataService.fetchHighSchoolFieldOfStudy(false, "F1");

            assertThat(result).isNull();
            verifyNoInteractions(highSchoolFieldOfStudyService);
        }

        @Test
        @DisplayName("should fetch name when number present")
        void withNumber() {
            when(highSchoolFieldOfStudyService.findFieldOfStudyName("F1")).thenReturn("Field Name");

            String result = dataService.fetchHighSchoolFieldOfStudy(true, "F1");

            assertThat(result).isEqualTo("Field Name");
        }
    }
}