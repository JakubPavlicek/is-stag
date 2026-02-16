package com.stag.platform.shared.grpc.service;

import com.stag.platform.address.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.GetCodelistValuesRequest;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import com.stag.platform.codelist.v1.GetPersonAddressDataRequest;
import com.stag.platform.codelist.v1.GetPersonAddressDataResponse;
import com.stag.platform.codelist.v1.GetPersonBankingDataRequest;
import com.stag.platform.codelist.v1.GetPersonBankingDataResponse;
import com.stag.platform.codelist.v1.GetPersonEducationDataRequest;
import com.stag.platform.codelist.v1.GetPersonEducationDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileUpdateDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileUpdateDataResponse;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import com.stag.platform.entry.service.dto.PersonProfileLowValues;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodelistGrpcServiceTest {

    @Mock
    private CodelistDataService dataService;

    @InjectMocks
    private CodelistGrpcService grpcService;

    @Nested
    @DisplayName("getCodelistValues")
    class GetCodelistValues {

        @Mock
        private StreamObserver<GetCodelistValuesResponse> responseObserver;

        @Test
        @DisplayName("should complete successfully")
        void success() {
            GetCodelistValuesRequest request = GetCodelistValuesRequest.newBuilder()
                .setLanguage("en")
                .build();

            List<CodelistMeaning> meanings = List.of(CodelistMeaning.newBuilder().setMeaning("M").build());
            when(dataService.fetchCodelistMeanings(any(), eq("en"))).thenReturn(meanings);

            grpcService.getCodelistValues(request, responseObserver);

            verify(responseObserver).onNext(any(GetCodelistValuesResponse.class));
            verify(responseObserver).onCompleted();
        }
    }

    @Nested
    @DisplayName("getPersonProfileData")
    class GetPersonProfileData {

        @Mock
        private StreamObserver<GetPersonProfileDataResponse> responseObserver;

        @Test
        @DisplayName("should complete successfully and map fields correctly")
        void success() {
            GetPersonProfileDataRequest request = GetPersonProfileDataRequest.newBuilder()
                .setLanguage("en")
                .setBirthCountryId(1)
                .setCitizenshipCountryId(2)
                .build();

            List<CodelistMeaning> meanings = List.of(CodelistMeaning.newBuilder().setMeaning("Meaning").build());
            Map<Integer, String> countryNames = Map.of(1, "BirthCountry", 2, "CitizenshipCountry");

            when(dataService.fetchCodelistMeanings(any(), eq("en"))).thenReturn(meanings);
            when(dataService.fetchCountryNames(any(), eq("en"))).thenReturn(countryNames);

            grpcService.getPersonProfileData(request, responseObserver);

            ArgumentCaptor<GetPersonProfileDataResponse> captor = ArgumentCaptor.forClass(GetPersonProfileDataResponse.class);
            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            GetPersonProfileDataResponse response = captor.getValue();
            assertThat(response.getBirthCountryName()).isEqualTo("BirthCountry");
            assertThat(response.getCitizenshipCountryName()).isEqualTo("CitizenshipCountry");
            assertThat(response.getCodelistMeaningsList()).hasSize(1);
        }

        @Test
        @DisplayName("should report error when a task fails")
        void taskFailure() {
            GetPersonProfileDataRequest request = GetPersonProfileDataRequest.newBuilder()
                .setLanguage("en")
                .build();

            RuntimeException exception = new RuntimeException("Fetch failed");
            when(dataService.fetchCodelistMeanings(any(), any())).thenThrow(exception);

            grpcService.getPersonProfileData(request, responseObserver);

            verify(responseObserver).onError(exception);
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();
        }

        @Test
        @DisplayName("should handle InterruptedException")
        void interrupted() {
            GetPersonProfileDataRequest request = GetPersonProfileDataRequest.newBuilder().build();
            CountDownLatch taskStarted = new CountDownLatch(1);

            when(dataService.fetchCodelistMeanings(any(), any())).thenAnswer(_ -> {
                taskStarted.countDown();
                Thread.sleep(Long.MAX_VALUE);
                return List.of();
            });

            Thread caller = new Thread(() -> grpcService.getPersonProfileData(request, responseObserver));
            caller.start();

            await().atMost(5, SECONDS).until(() -> taskStarted.getCount() == 0);
            caller.interrupt();

            await().atMost(5, SECONDS).until(() -> !caller.isAlive());
            verify(responseObserver).onError(any());
        }
    }

    @Nested
    @DisplayName("getPersonProfileUpdateData")
    class GetPersonProfileUpdateData {

        @Mock
        private StreamObserver<GetPersonProfileUpdateDataResponse> responseObserver;

        @Test
        @DisplayName("should complete successfully when tasks succeed")
        void success() {
            GetPersonProfileUpdateDataRequest request = GetPersonProfileUpdateDataRequest.newBuilder()
                .setBirthCountryName("Czechia")
                .build();

            PersonProfileLowValues lowValues = new PersonProfileLowValues("S", "Ing", "PhD");
            when(dataService.fetchCodelistLowValues(any(), any(), any())).thenReturn(lowValues);
            when(dataService.fetchCountryId("Czechia")).thenReturn(203);

            grpcService.getPersonProfileUpdateData(request, responseObserver);

            ArgumentCaptor<GetPersonProfileUpdateDataResponse> captor = ArgumentCaptor.forClass(GetPersonProfileUpdateDataResponse.class);
            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            GetPersonProfileUpdateDataResponse response = captor.getValue();
            assertThat(response.getMaritalStatusLowValue()).isEqualTo("S");
            assertThat(response.getTitlePrefixLowValue()).isEqualTo("Ing");
            assertThat(response.getTitleSuffixLowValue()).isEqualTo("PhD");
            assertThat(response.getBirthCountryId()).isEqualTo(203);
        }

        @Test
        @DisplayName("should report error when a task fails")
        void taskFailure() {
            GetPersonProfileUpdateDataRequest request = GetPersonProfileUpdateDataRequest.newBuilder().build();

            RuntimeException exception = new RuntimeException("Fetch failed");
            when(dataService.fetchCodelistLowValues(any(), any(), any())).thenThrow(exception);

            grpcService.getPersonProfileUpdateData(request, responseObserver);

            verify(responseObserver).onError(exception);
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();
        }

        @Test
        @DisplayName("should handle InterruptedException")
        void interrupted() {
            GetPersonProfileUpdateDataRequest request = GetPersonProfileUpdateDataRequest.newBuilder().build();
            CountDownLatch taskStarted = new CountDownLatch(1);

            when(dataService.fetchCodelistLowValues(any(), any(), any())).thenAnswer(_ -> {
                taskStarted.countDown();
                Thread.sleep(Long.MAX_VALUE);
                return null;
            });

            Thread caller = new Thread(() -> grpcService.getPersonProfileUpdateData(request, responseObserver));
            caller.start();

            await().atMost(5, SECONDS).until(() -> taskStarted.getCount() == 0);
            caller.interrupt();

            await().atMost(5, SECONDS).until(() -> !caller.isAlive());
            verify(responseObserver).onError(any());
        }
    }

    @Nested
    @DisplayName("getPersonAddressData")
    class GetPersonAddressData {

        @Mock
        private StreamObserver<GetPersonAddressDataResponse> responseObserver;

        @Test
        @DisplayName("should complete successfully and map address fields correctly")
        void success() {
            GetPersonAddressDataRequest request = GetPersonAddressDataRequest.newBuilder()
                .setLanguage("en")
                .setPermanentCountryId(1)
                .setTemporaryCountryId(2)
                .setPermanentMunicipalityPartId(10L)
                .setTemporaryMunicipalityPartId(20L)
                .build();

            AddressPlaceNameProjection permAddress = new AddressPlaceNameProjection(10L, "PermMuni", "PermPart", "PermDist");
            AddressPlaceNameProjection tempAddress = new AddressPlaceNameProjection(20L, "TempMuni", "TempPart", "TempDist");
            Map<Long, AddressPlaceNameProjection> addresses = Map.of(10L, permAddress, 20L, tempAddress);
            Map<Integer, String> countryNames = Map.of(1, "PermCountry", 2, "TempCountry");

            when(dataService.fetchAddressNames(any())).thenReturn(addresses);
            when(dataService.fetchCountryNames(any(), eq("en"))).thenReturn(countryNames);

            grpcService.getPersonAddressData(request, responseObserver);

            ArgumentCaptor<GetPersonAddressDataResponse> captor = ArgumentCaptor.forClass(GetPersonAddressDataResponse.class);
            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            GetPersonAddressDataResponse response = captor.getValue();
            assertThat(response.getPermanentCountryName()).isEqualTo("PermCountry");
            assertThat(response.getTemporaryCountryName()).isEqualTo("TempCountry");

            assertThat(response.getPermanentMunicipalityName()).isEqualTo("PermMuni");
            assertThat(response.getPermanentMunicipalityPartName()).isEqualTo("PermPart");
            assertThat(response.getPermanentDistrictName()).isEqualTo("PermDist");

            assertThat(response.getTemporaryMunicipalityName()).isEqualTo("TempMuni");
            assertThat(response.getTemporaryMunicipalityPartName()).isEqualTo("TempPart");
            assertThat(response.getTemporaryDistrictName()).isEqualTo("TempDist");
        }

        @Test
        @DisplayName("should report error when a task fails")
        void taskFailure() {
            GetPersonAddressDataRequest request = GetPersonAddressDataRequest.newBuilder().build();

            RuntimeException exception = new RuntimeException("Fetch failed");
            when(dataService.fetchAddressNames(any())).thenThrow(exception);

            grpcService.getPersonAddressData(request, responseObserver);

            verify(responseObserver).onError(exception);
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();
        }

        @Test
        @DisplayName("should handle InterruptedException")
        void interrupted() {
            GetPersonAddressDataRequest request = GetPersonAddressDataRequest.newBuilder().build();
            CountDownLatch taskStarted = new CountDownLatch(1);

            when(dataService.fetchAddressNames(any())).thenAnswer(_ -> {
                taskStarted.countDown();
                Thread.sleep(Long.MAX_VALUE);
                return Map.of();
            });

            Thread caller = new Thread(() -> grpcService.getPersonAddressData(request, responseObserver));
            caller.start();

            await().atMost(5, SECONDS).until(() -> taskStarted.getCount() == 0);
            caller.interrupt();

            await().atMost(5, SECONDS).until(() -> !caller.isAlive());
            verify(responseObserver).onError(any());
        }
    }

    @Nested
    @DisplayName("getPersonBankingData")
    class GetPersonBankingData {

        @Mock
        private StreamObserver<GetPersonBankingDataResponse> responseObserver;

        @Test
        @DisplayName("should complete successfully and map fields")
        void success() {
            GetPersonBankingDataRequest request = GetPersonBankingDataRequest.newBuilder()
                .setLanguage("en")
                .setEuroAccountCountryId(5)
                .build();

            List<CodelistMeaning> meanings = List.of(CodelistMeaning.newBuilder().setMeaning("Meaning").build());
            Map<Integer, String> countryNames = Map.of(5, "EuroCountry");

            when(dataService.fetchCodelistMeanings(any(), eq("en"))).thenReturn(meanings);
            when(dataService.fetchCountryNames(any(), eq("en"))).thenReturn(countryNames);

            grpcService.getPersonBankingData(request, responseObserver);

            ArgumentCaptor<GetPersonBankingDataResponse> captor = ArgumentCaptor.forClass(GetPersonBankingDataResponse.class);
            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            GetPersonBankingDataResponse response = captor.getValue();
            assertThat(response.getEuroAccountCountryName()).isEqualTo("EuroCountry");
            assertThat(response.getCodelistMeaningsList()).hasSize(1);
        }

        @Test
        @DisplayName("should report error when a task fails")
        void taskFailure() {
            GetPersonBankingDataRequest request = GetPersonBankingDataRequest.newBuilder().build();

            RuntimeException exception = new RuntimeException("Fetch failed");
            when(dataService.fetchCodelistMeanings(any(), any())).thenThrow(exception);

            grpcService.getPersonBankingData(request, responseObserver);

            verify(responseObserver).onError(exception);
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();
        }

        @Test
        @DisplayName("should handle InterruptedException")
        void interrupted() {
            GetPersonBankingDataRequest request = GetPersonBankingDataRequest.newBuilder().build();
            CountDownLatch taskStarted = new CountDownLatch(1);

            when(dataService.fetchCodelistMeanings(any(), any())).thenAnswer(_ -> {
                taskStarted.countDown();
                Thread.sleep(Long.MAX_VALUE);
                return List.of();
            });

            Thread caller = new Thread(() -> grpcService.getPersonBankingData(request, responseObserver));
            caller.start();

            await().atMost(5, SECONDS).until(() -> taskStarted.getCount() == 0);
            caller.interrupt();

            await().atMost(5, SECONDS).until(() -> !caller.isAlive());
            verify(responseObserver).onError(any());
        }
    }

    @Nested
    @DisplayName("getPersonEducationData")
    class GetPersonEducationData {

        @Mock
        private StreamObserver<GetPersonEducationDataResponse> responseObserver;

        @Test
        @DisplayName("should complete successfully and map fields")
        void success() {
            GetPersonEducationDataRequest request = GetPersonEducationDataRequest.newBuilder()
                .setLanguage("en")
                .setHighSchoolCountryId(10)
                .build();

            HighSchoolAddressProjection address = new HighSchoolAddressProjection("SchoolName", "Street", "123", "City", "Dist");
            Map<Integer, String> countryNames = Map.of(10, "HighSchoolCountry");

            when(dataService.fetchHighSchoolAddress(anyBoolean(), any())).thenReturn(address);
            when(dataService.fetchHighSchoolFieldOfStudy(anyBoolean(), any())).thenReturn("Field");
            when(dataService.fetchCountryNames(any(), eq("en"))).thenReturn(countryNames);

            grpcService.getPersonEducationData(request, responseObserver);

            ArgumentCaptor<GetPersonEducationDataResponse> captor = ArgumentCaptor.forClass(GetPersonEducationDataResponse.class);
            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            GetPersonEducationDataResponse response = captor.getValue();
            assertThat(response.getHighSchoolName()).isEqualTo("SchoolName");
            assertThat(response.getHighSchoolMunicipalityName()).isEqualTo("City");
            assertThat(response.getHighSchoolCountryName()).isEqualTo("HighSchoolCountry");
            assertThat(response.getHighSchoolFieldOfStudy()).isEqualTo("Field");
        }

        @Test
        @DisplayName("should report error when a task fails")
        void taskFailure() {
            GetPersonEducationDataRequest request = GetPersonEducationDataRequest.newBuilder().build();

            RuntimeException exception = new RuntimeException("Fetch failed");
            when(dataService.fetchHighSchoolAddress(anyBoolean(), any())).thenThrow(exception);

            grpcService.getPersonEducationData(request, responseObserver);

            verify(responseObserver).onError(exception);
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();
        }

        @Test
        @DisplayName("should handle InterruptedException")
        void interrupted() {
            GetPersonEducationDataRequest request = GetPersonEducationDataRequest.newBuilder().build();
            CountDownLatch taskStarted = new CountDownLatch(1);

            when(dataService.fetchHighSchoolAddress(anyBoolean(), any())).thenAnswer(_ -> {
                taskStarted.countDown();
                Thread.sleep(Long.MAX_VALUE);
                return null;
            });

            Thread caller = new Thread(() -> grpcService.getPersonEducationData(request, responseObserver));
            caller.start();

            await().atMost(5, SECONDS).until(() -> taskStarted.getCount() == 0);
            caller.interrupt();

            await().atMost(5, SECONDS).until(() -> !caller.isAlive());
            verify(responseObserver).onError(any());
        }
    }
}