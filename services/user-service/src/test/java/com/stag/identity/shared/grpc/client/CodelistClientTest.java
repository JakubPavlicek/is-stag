package com.stag.identity.shared.grpc.client;

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
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodelistClientTest {

    @Mock
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    @InjectMocks
    private CodelistClient codelistClient;

    private static final String LANGUAGE = "en";

    @Test
    @DisplayName("getSimpleProfileData should return enriched data")
    void getSimpleProfileData_ReturnsEnrichedData() {
        SimpleProfileView simpleProfile = new SimpleProfileView(
            "John", "Doe", "Mr.", null, "M"
        );

        CodelistMeaning codelistMeaning = CodelistMeaning.newBuilder()
                                                         .setDomain("TITUL_PRED")
                                                         .setLowValue("Mr.")
                                                         .setMeaning("Mister")
                                                         .build();

        GetCodelistValuesResponse response = GetCodelistValuesResponse.newBuilder()
                                                                      .addCodelistMeanings(codelistMeaning)
                                                                      .build();

        when(codelistServiceStub.getCodelistValues(any(GetCodelistValuesRequest.class)))
            .thenReturn(response);

        CodelistMeaningsLookupData result = codelistClient.getSimpleProfileData(simpleProfile, LANGUAGE);

        assertThat(result).isNotNull();
        assertThat(result.codelistMeanings()).isNotNull();
        verify(codelistServiceStub).getCodelistValues(any(GetCodelistValuesRequest.class));
    }

    @Test
    @DisplayName("getPersonProfileData should return data when request has fields")
    void getPersonProfileData_WithFields_ReturnsData() {
        ProfileView profile = new ProfileView(
            1, "John", "Doe", null, null, null, null, null,
            "Mr.", null, null, LocalDate.now(),
            123, null, null, null, null, "M", "SINGLE"
        );

        CodelistMeaning codelistMeaning = CodelistMeaning.newBuilder()
                                                         .setDomain("TITUL_PRED")
                                                         .setLowValue("Mr.")
                                                         .setMeaning("Mister")
                                                         .build();

        GetPersonProfileDataResponse response = GetPersonProfileDataResponse.newBuilder()
                                                                            .addCodelistMeanings(codelistMeaning)
                                                                            .build();

        when(codelistServiceStub.getPersonProfileData(any(GetPersonProfileDataRequest.class)))
            .thenReturn(response);

        ProfileLookupData result = codelistClient.getPersonProfileData(profile, LANGUAGE);

        assertThat(result).isNotNull();
        verify(codelistServiceStub).getPersonProfileData(any(GetPersonProfileDataRequest.class));
    }

    @Test
    @DisplayName("getPersonProfileData should return null when request is empty (skipped)")
    void getPersonProfileData_EmptyRequest_ReturnsNull() {
        ProfileView profile = new ProfileView(
            1, "John", "Doe", null, null, null, null, null,
            null, null, null, LocalDate.now(),
            null, null, null, null, null, null, null
        );

        ProfileLookupData result = codelistClient.getPersonProfileData(profile, LANGUAGE);

        assertThat(result).isNull();
        verify(codelistServiceStub, never()).getPersonProfileData(any());
    }

    @Test
    @DisplayName("getPersonProfileUpdateData should return data when inputs provided")
    void getPersonProfileUpdateData_WithInputs_ReturnsData() {
        String maritalStatus = "SINGLE";
        String birthCountry = "Czechia";
        Profile.Titles titles = new Profile.Titles("Ing.", "Ph.D.");

        GetPersonProfileUpdateDataResponse response = GetPersonProfileUpdateDataResponse.newBuilder().build();

        when(codelistServiceStub.getPersonProfileUpdateData(any(GetPersonProfileUpdateDataRequest.class)))
            .thenReturn(response);

        ProfileUpdateLookupData result = codelistClient.getPersonProfileUpdateData(maritalStatus, birthCountry, titles);

        assertThat(result).isNotNull();
        verify(codelistServiceStub).getPersonProfileUpdateData(any(GetPersonProfileUpdateDataRequest.class));
    }

    @Test
    @DisplayName("getPersonProfileUpdateData should return empty object when inputs are null (skipped)")
    void getPersonProfileUpdateData_NullInputs_ReturnsEmpty() {
        Profile.Titles titles = new Profile.Titles(null, null);

        ProfileUpdateLookupData result = codelistClient.getPersonProfileUpdateData(null, null, titles);

        assertThat(result).isNotNull();
        assertThat(result.maritalStatusLowValue()).isNull();
        verify(codelistServiceStub, never()).getPersonProfileUpdateData(any());
    }

    @Test
    @DisplayName("getPersonProfileUpdateData should return empty object when titles is null (skipped)")
    void getPersonProfileUpdateData_NullTitles_ReturnsEmpty() {
        ProfileUpdateLookupData result = codelistClient.getPersonProfileUpdateData(null, null, null);

        assertThat(result).isNotNull();
        assertThat(result.maritalStatusLowValue()).isNull();
        verify(codelistServiceStub, never()).getPersonProfileUpdateData(any());
    }

    @Test
    @DisplayName("getPersonAddressData should return data when address has IDs")
    void getPersonAddressData_WithIds_ReturnsData() {
        AddressView address = new AddressView(
            "Main", "1", "10000", 1L, 10,
            null, null, null, null, null,
            null, null, null, null,
            null, null, null, null
        );

        GetPersonAddressDataResponse response = GetPersonAddressDataResponse.newBuilder()
                                                                            .setPermanentCountryName("Czechia")
                                                                            .build();

        when(codelistServiceStub.getPersonAddressData(any(GetPersonAddressDataRequest.class)))
            .thenReturn(response);

        AddressLookupData result = codelistClient.getPersonAddressData(address, LANGUAGE);

        assertThat(result).isNotNull();
        assertThat(result.permanentCountry()).isEqualTo("Czechia");
        verify(codelistServiceStub).getPersonAddressData(any(GetPersonAddressDataRequest.class));
    }

    @Test
    @DisplayName("getPersonAddressData should return null when address has no IDs (skipped)")
    void getPersonAddressData_NoIds_ReturnsNull() {
        AddressView address = new AddressView(
            "Main", "1", "10000", null, null,
            null, null, null, null, null,
            null, null, null, null,
            null, null, null, null
        );

        AddressLookupData result = codelistClient.getPersonAddressData(address, LANGUAGE);

        assertThat(result).isNull();
        verify(codelistServiceStub, never()).getPersonAddressData(any());
    }

    @Test
    @DisplayName("getPersonBankingData should return data when bank has codes")
    void getPersonBankingData_WithCodes_ReturnsData() {
        BankView bank = new BankView(
            "Owner", "Addr", "123", "456", "0100", "CZ...", "CZK",
            null, null, null, null, null, null, null, null, null
        );

        CodelistMeaning codelistMeaning = CodelistMeaning.newBuilder()
                                                         .setDomain("CIS_BANK")
                                                         .setLowValue("0100")
                                                         .setMeaning("KB")
                                                         .build();

        GetPersonBankingDataResponse response = GetPersonBankingDataResponse.newBuilder()
                                                                            .addCodelistMeanings(codelistMeaning)
                                                                            .build();

        when(codelistServiceStub.getPersonBankingData(any(GetPersonBankingDataRequest.class)))
            .thenReturn(response);

        BankingLookupData result = codelistClient.getPersonBankingData(bank, LANGUAGE);

        assertThat(result).isNotNull();
        verify(codelistServiceStub).getPersonBankingData(any(GetPersonBankingDataRequest.class));
    }

    @Test
    @DisplayName("getPersonBankingData should return null when bank has no codes (skipped)")
    void getPersonBankingData_NoCodes_ReturnsNull() {
        BankView bank = new BankView(
            "Owner", "Addr", "123", "456", null, "CZ...", "CZK",
            null, null, null, null, null, null, null, null, null
        );

        BankingLookupData result = codelistClient.getPersonBankingData(bank, LANGUAGE);

        assertThat(result).isNull();
        verify(codelistServiceStub, never()).getPersonBankingData(any());
    }

    @Test
    @DisplayName("getPersonEducationData should return data when education has IDs")
    void getPersonEducationData_WithIds_ReturnsData() {
        EducationView education = new EducationView(
            "SCHOOL1", "FIELD1", 10, LocalDate.now(),
            null, null, null
        );

        GetPersonEducationDataResponse response = GetPersonEducationDataResponse.newBuilder()
                                                                                .setHighSchoolName("School Name")
                                                                                .build();

        when(codelistServiceStub.getPersonEducationData(any(GetPersonEducationDataRequest.class)))
            .thenReturn(response);

        EducationLookupData result = codelistClient.getPersonEducationData(education, LANGUAGE);

        assertThat(result).isNotNull();
        verify(codelistServiceStub).getPersonEducationData(any(GetPersonEducationDataRequest.class));
    }

    @Test
    @DisplayName("getPersonEducationData should return null when education has no IDs (skipped)")
    void getPersonEducationData_NoIds_ReturnsNull() {
        EducationView education = new EducationView(
            null, null, null, LocalDate.now(),
            "Foreign School", "London", "IT"
        );

        EducationLookupData result = codelistClient.getPersonEducationData(education, LANGUAGE);

        assertThat(result).isNull();
        verify(codelistServiceStub, never()).getPersonEducationData(any());
    }

}
