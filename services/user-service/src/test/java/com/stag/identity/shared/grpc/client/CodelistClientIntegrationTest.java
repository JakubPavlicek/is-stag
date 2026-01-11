package com.stag.identity.shared.grpc.client;

import com.stag.identity.config.TestCacheConfig;
import com.stag.identity.config.TestOracleContainerConfig;
import com.stag.identity.person.model.Profile;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.repository.projection.EducationView;
import com.stag.identity.person.repository.projection.ProfileView;
import com.stag.identity.person.repository.projection.SimpleProfileView;
import com.stag.identity.person.service.data.AddressLookupData;
import com.stag.identity.person.service.data.BankingLookupData;
import com.stag.identity.person.service.data.CodelistMeaningsLookupData;
import com.stag.identity.person.service.data.EducationLookupData;
import com.stag.identity.person.service.data.ProfileLookupData;
import com.stag.identity.person.service.data.ProfileUpdateLookupData;
import com.stag.identity.shared.grpc.server.FakeCodelistService;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import com.stag.platform.codelist.v1.GetPersonAddressDataResponse;
import com.stag.platform.codelist.v1.GetPersonBankingDataResponse;
import com.stag.platform.codelist.v1.GetPersonEducationDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileUpdateDataResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class, FakeCodelistService.class })
@ActiveProfiles("test")
class CodelistClientIntegrationTest {

    @Autowired
    private CodelistClient codelistClient;

    @Autowired
    private FakeCodelistService fakeCodelistService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        fakeCodelistService.resetStats();
        circuitBreakerRegistry.circuitBreaker("codelist-service")
                              .transitionToClosedState();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub() {
            ManagedChannel channel = InProcessChannelBuilder.forName("test")
                                                            .directExecutor()
                                                            .build();
            return CodelistServiceGrpc.newBlockingStub(channel);
        }

    }

    @Test
    @DisplayName("getSimpleProfileData should return enriched data")
    void getSimpleProfileData_ReturnsData() {
        SimpleProfileView simpleProfile = new SimpleProfileView("John", "Doe", "Mr.", null, "M");

        CodelistMeaning codelistMeaning = CodelistMeaning.newBuilder()
                                                         .setDomain("TITUL_PRED")
                                                         .setLowValue("Mr.")
                                                         .setMeaning("Mister")
                                                         .build();

        fakeCodelistService.setNextCodelistValuesResponse(
            GetCodelistValuesResponse.newBuilder()
                                     .addCodelistMeanings(codelistMeaning)
                                     .build()
        );

        CodelistMeaningsLookupData result = codelistClient.getSimpleProfileData(simpleProfile, "en");

        assertThat(result).isNotNull();
        assertThat(result.codelistMeanings()).hasSize(1);
    }

    @Test
    @DisplayName("getPersonProfileData should return enriched data")
    void getPersonProfileData_ReturnsData() {
        ProfileView profile = new ProfileView(
            1, "John", "Doe", null, null, null, null, null,
            "Mr.", null, null, LocalDate.now(),
            123, "Prague", null, null, null, "M", "S"
        );

        CodelistMeaning codelistMeaning = CodelistMeaning.newBuilder()
                                                         .setDomain("TITUL_PRED")
                                                         .setLowValue("Mr.")
                                                         .setMeaning("Mister")
                                                         .build();

        fakeCodelistService.setNextPersonProfileDataResponse(
            GetPersonProfileDataResponse.newBuilder()
                                        .setBirthCountryName("Czech Republic")
                                        .addCodelistMeanings(codelistMeaning)
                                        .build()
        );

        ProfileLookupData result = codelistClient.getPersonProfileData(profile, "en");

        assertThat(result).isNotNull();
        assertThat(result.birthCountryName()).isEqualTo("Czech Republic");
        assertThat(result.codelistMeanings()).hasSize(1);
    }

    @Test
    @DisplayName("getPersonProfileUpdateData should return validated data")
    void getPersonProfileUpdateData_ReturnsData() {
        Profile.Titles titles = new Profile.Titles("Ing.", "Ph.D.");

        fakeCodelistService.setNextPersonProfileUpdateDataResponse(
            GetPersonProfileUpdateDataResponse.newBuilder()
                                              .setMaritalStatusLowValue("S")
                                              .setBirthCountryId(203)
                                              .build()
        );

        ProfileUpdateLookupData result = codelistClient.getPersonProfileUpdateData("Single", "Czechia", titles);

        assertThat(result).isNotNull();
        assertThat(result.maritalStatusLowValue()).isEqualTo("S");
        assertThat(result.birthCountryId()).isEqualTo(203);
    }

    @Test
    @DisplayName("getPersonAddressData should return address data")
    void getPersonAddressData_ReturnsData() {
        AddressView address = new AddressView(
            "Street", "1", "10000", 1L, 203,
            null, null, null, null, null,
            null, null, null, null,
            null, null, null, null
        );

        fakeCodelistService.setNextPersonAddressDataResponse(
            GetPersonAddressDataResponse.newBuilder()
                                        .setPermanentCountryName("Czech Republic")
                                        .setPermanentMunicipalityName("Prague")
                                        .build()
        );

        AddressLookupData result = codelistClient.getPersonAddressData(address, "en");

        assertThat(result).isNotNull();
        assertThat(result.permanentCountry()).isEqualTo("Czech Republic");
        assertThat(result.permanentMunicipality()).isEqualTo("Prague");
    }

    @Test
    @DisplayName("getPersonBankingData should return banking data")
    void getPersonBankingData_ReturnsData() {
        BankView bank = new BankView(
            "Owner", "Addr", "123", "456", "0100", "CZ...", "CZK",
            null, null, null, null, null, null, null, null, null
        );

        CodelistMeaning codelistMeaning = CodelistMeaning.newBuilder()
                                                         .setDomain("CIS_BANK")
                                                         .setLowValue("0100")
                                                         .setMeaning("KB")
                                                         .build();

        fakeCodelistService.setNextPersonBankingDataResponse(
            GetPersonBankingDataResponse.newBuilder()
                                        .addCodelistMeanings(codelistMeaning)
                                        .build()
        );

        BankingLookupData result = codelistClient.getPersonBankingData(bank, "en");

        assertThat(result).isNotNull();
        assertThat(result.codelistMeanings()).hasSize(1);
    }

    @Test
    @DisplayName("getPersonEducationData should return education data")
    void getPersonEducationData_ReturnsData() {
        EducationView education = new EducationView(
            "123456", "7941K", 203, LocalDate.now(),
            null, null, null
        );

        fakeCodelistService.setNextPersonEducationDataResponse(
            GetPersonEducationDataResponse.newBuilder()
                                          .setHighSchoolName("Gymnazium")
                                          .setHighSchoolCountryName("Czech Republic")
                                          .build()
        );

        EducationLookupData result = codelistClient.getPersonEducationData(education, "en");

        assertThat(result).isNotNull();
        assertThat(result.highSchoolName()).isEqualTo("Gymnazium");
        assertThat(result.highSchoolCountryName()).isEqualTo("Czech Republic");
    }

    @Test
    @DisplayName("getSimpleProfileData should retry on failure")
    void getSimpleProfileData_RetriesOnFailure() {
        SimpleProfileView simpleProfile = new SimpleProfileView("John", "Doe", "Mr.", null, "M");

        // Fail 2 times, succeed on 3rd
        fakeCodelistService.setFailNextCalls(2, Status.UNAVAILABLE);

        codelistClient.getSimpleProfileData(simpleProfile, "en");

        // Initial call + 2 retries = 3 calls
        assertThat(fakeCodelistService.getCallCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("getSimpleProfileData should fail after max retries")
    void getSimpleProfileData_FailsAfterMaxRetries() {
        SimpleProfileView simpleProfile = new SimpleProfileView("John", "Doe", "Mr.", null, "M");

        // Fail 3 times (max attempts are 3)
        fakeCodelistService.setFailNextCalls(3, Status.UNAVAILABLE);

        assertThatThrownBy(() -> codelistClient.getSimpleProfileData(simpleProfile, "en"))
            .isInstanceOf(StatusRuntimeException.class);

        assertThat(fakeCodelistService.getCallCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("getSimpleProfileData should open circuit breaker after threshold failures")
    void getSimpleProfileData_CircuitBreakerOpens() {
        SimpleProfileView simpleProfile = new SimpleProfileView("John", "Doe", "Mr.", null, "M");

        // Fail enough times to trip circuit breaker (min calls 5, threshold 50%)
        fakeCodelistService.setFailNextCalls(10, Status.UNAVAILABLE);

        // Call until CB opens
        for (int i = 0; i < 5; i++) {
            try {
                codelistClient.getSimpleProfileData(simpleProfile, "en");
            } catch (Exception _) {
                // ignore
            }
        }

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("codelist-service");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // The next call should fail fast
        assertThatThrownBy(() -> codelistClient.getSimpleProfileData(simpleProfile, "en"))
            .isInstanceOf(CallNotPermittedException.class);
    }

}