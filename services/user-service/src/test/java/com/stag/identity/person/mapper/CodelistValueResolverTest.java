package com.stag.identity.person.mapper;

import com.stag.identity.person.service.data.BankingLookupData;
import com.stag.identity.person.service.data.CodelistMeaningsLookupData;
import com.stag.identity.person.service.data.ProfileLookupData;
import com.stag.identity.shared.grpc.model.CodelistEntryId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CodelistValueResolverTest {

    private final CodelistValueResolver resolver = new CodelistValueResolver();

    @Nested
    @DisplayName("Gender Resolution")
    class GenderResolution {

        @Test
        @DisplayName("lookupGender should return meaning from ProfileLookupData")
        void lookupGender_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("POHLAVI", "M");
            ProfileLookupData data = ProfileLookupData.builder()
                                                      .codelistMeanings(Map.of(entryId, "Male"))
                                                      .build();

            String result = resolver.lookupGender("M", data);

            assertThat(result).isEqualTo("Male");
        }

        @Test
        @DisplayName("lookupGender should return null when data is null")
        void lookupGender_NullData_ReturnsNull() {
            assertThat(resolver.lookupGender("M", null)).isNull();
        }

        @Test
        @DisplayName("lookupCodelistGender should return meaning from CodelistMeaningsLookupData")
        void lookupCodelistGender_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("POHLAVI", "F");
            CodelistMeaningsLookupData data = CodelistMeaningsLookupData.builder()
                                                                        .codelistMeanings(Map.of(entryId, "Female"))
                                                                        .build();

            String result = resolver.lookupCodelistGender("F", data);

            assertThat(result).isEqualTo("Female");
        }

        @Test
        @DisplayName("lookupCodelistGender should return null when data is null")
        void lookupCodelistGender_NullData_ReturnsNull() {
            assertThat(resolver.lookupCodelistGender("F", null)).isNull();
        }

        @Test
        @DisplayName("lookupCodelistGender should return null when meaning not found")
        void lookupCodelistGender_NotFound_ReturnsNull() {
            CodelistMeaningsLookupData data = CodelistMeaningsLookupData.builder()
                                                                        .codelistMeanings(Collections.emptyMap())
                                                                        .build();

            String result = resolver.lookupCodelistGender("X", data);

            assertThat(result).isNull();
        }

    }

    @Nested
    @DisplayName("Marital Status Resolution")
    class MaritalStatusResolution {

        @Test
        @DisplayName("lookupMaritalStatus should return meaning")
        void lookupMaritalStatus_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("STAV", "S");
            ProfileLookupData data = ProfileLookupData.builder()
                                                      .codelistMeanings(Map.of(entryId, "Single"))
                                                      .build();

            String result = resolver.lookupMaritalStatus("S", data);

            assertThat(result).isEqualTo("Single");
        }

        @Test
        @DisplayName("lookupMaritalStatus should return null when data is null")
        void lookupMaritalStatus_NullData_ReturnsNull() {
            assertThat(resolver.lookupMaritalStatus("S", null)).isNull();
        }

    }

    @Nested
    @DisplayName("Titles Resolution")
    class TitlesResolution {

        @Test
        @DisplayName("lookupTitlePrefix should return meaning from ProfileLookupData")
        void lookupTitlePrefix_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("TITUL_PRED", "Ing");
            ProfileLookupData data = ProfileLookupData.builder()
                                                      .codelistMeanings(Map.of(entryId, "Inženýr"))
                                                      .build();

            String result = resolver.lookupTitlePrefix("Ing", data);

            assertThat(result).isEqualTo("Inženýr");
        }

        @Test
        @DisplayName("lookupTitlePrefix should return null when data is null")
        void lookupTitlePrefix_NullData_ReturnsNull() {
            assertThat(resolver.lookupTitlePrefix("Ing", null)).isNull();
        }

        @Test
        @DisplayName("lookupCodelistTitlePrefix should return meaning from CodelistMeaningsLookupData")
        void lookupCodelistTitlePrefix_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("TITUL_PRED", "Mgr");
            CodelistMeaningsLookupData data = CodelistMeaningsLookupData.builder()
                                                                        .codelistMeanings(Map.of(entryId, "Magistr"))
                                                                        .build();

            String result = resolver.lookupCodelistTitlePrefix("Mgr", data);

            assertThat(result).isEqualTo("Magistr");
        }

        @Test
        @DisplayName("lookupCodelistTitlePrefix should return null when data is null")
        void lookupCodelistTitlePrefix_NullData_ReturnsNull() {
            assertThat(resolver.lookupCodelistTitlePrefix("Mgr", null)).isNull();
        }

        @Test
        @DisplayName("lookupTitleSuffix should return meaning from ProfileLookupData")
        void lookupTitleSuffix_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("TITUL_ZA", "PhD");
            ProfileLookupData data = ProfileLookupData.builder()
                                                      .codelistMeanings(Map.of(entryId, "Doktor"))
                                                      .build();

            String result = resolver.lookupTitleSuffix("PhD", data);

            assertThat(result).isEqualTo("Doktor");
        }

        @Test
        @DisplayName("lookupTitleSuffix should return null when data is null")
        void lookupTitleSuffix_NullData_ReturnsNull() {
            assertThat(resolver.lookupTitleSuffix("PhD", null)).isNull();
        }

        @Test
        @DisplayName("lookupCodelistTitleSuffix should return meaning from CodelistMeaningsLookupData")
        void lookupCodelistTitleSuffix_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("TITUL_ZA", "CSc");
            CodelistMeaningsLookupData data = CodelistMeaningsLookupData.builder()
                                                                        .codelistMeanings(Map.of(entryId, "Kandidát věd"))
                                                                        .build();

            String result = resolver.lookupCodelistTitleSuffix("CSc", data);

            assertThat(result).isEqualTo("Kandidát věd");
        }

        @Test
        @DisplayName("lookupCodelistTitleSuffix should return null when data is null")
        void lookupCodelistTitleSuffix_NullData_ReturnsNull() {
            assertThat(resolver.lookupCodelistTitleSuffix("CSc", null)).isNull();
        }

    }

    @Nested
    @DisplayName("Citizenship Resolution")
    class CitizenshipResolution {

        @Test
        @DisplayName("lookupCitizenshipQualifier should return meaning")
        void lookupCitizenshipQualifier_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("KVANT_OBCAN", "1");
            ProfileLookupData data = ProfileLookupData.builder()
                                                      .codelistMeanings(Map.of(entryId, "Občan"))
                                                      .build();

            String result = resolver.lookupCitizenshipQualifier("1", data);

            assertThat(result).isEqualTo("Občan");
        }

        @Test
        @DisplayName("lookupCitizenshipQualifier should return null when data is null")
        void lookupCitizenshipQualifier_NullData_ReturnsNull() {
            assertThat(resolver.lookupCitizenshipQualifier("1", null)).isNull();
        }

    }

    @Nested
    @DisplayName("Bank Name Resolution")
    class BankResolution {

        @Test
        @DisplayName("lookupBankName should return Czech bank name")
        void lookupBankName_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("CIS_BANK", "0100");
            BankingLookupData data = BankingLookupData.builder()
                                                      .codelistMeanings(Map.of(entryId, "Komerční banka"))
                                                      .build();

            String result = resolver.lookupBankName("0100", data);

            assertThat(result).isEqualTo("Komerční banka");
        }

        @Test
        @DisplayName("lookupBankName should return null when data is null")
        void lookupBankName_NullData_ReturnsNull() {
            assertThat(resolver.lookupBankName("0100", null)).isNull();
        }

        @Test
        @DisplayName("lookupEuroBankName should return Euro bank name")
        void lookupEuroBankName_ValidData_ReturnsMeaning() {
            CodelistEntryId entryId = new CodelistEntryId("CIS_BANK_EURO", "7000");
            BankingLookupData data = BankingLookupData.builder()
                                                      .codelistMeanings(Map.of(entryId, "Deutsche Bank"))
                                                      .build();

            String result = resolver.lookupEuroBankName("7000", data);

            assertThat(result).isEqualTo("Deutsche Bank");
        }

        @Test
        @DisplayName("lookupEuroBankName should return null when data is null")
        void lookupEuroBankName_NullData_ReturnsNull() {
            assertThat(resolver.lookupEuroBankName("7000", null)).isNull();
        }

    }

    @Test
    @DisplayName("Generic lookup should return null when meanings map is null")
    void lookupCodelistValue_NullMeanings_ReturnsNull() {
        // We test this through one of the public methods by providing a data object with null meanings
        ProfileLookupData data = ProfileLookupData.builder()
                                                  .codelistMeanings(null)
                                                  .build();

        String result = resolver.lookupGender("M", data);

        assertThat(result).isNull();
    }

}