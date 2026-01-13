package com.stag.identity.person.controller;

import com.stag.identity.config.TestCacheConfig;
import com.stag.identity.config.TestSecurityConfig;
import com.stag.identity.person.exception.InvalidAccountNumberException;
import com.stag.identity.person.exception.InvalidBankAccountException;
import com.stag.identity.person.exception.InvalidDataBoxException;
import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.exception.PersonProfileFetchException;
import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.model.Banking;
import com.stag.identity.person.model.Education;
import com.stag.identity.person.model.Profile;
import com.stag.identity.person.service.AddressService;
import com.stag.identity.person.service.BankingService;
import com.stag.identity.person.service.EducationService;
import com.stag.identity.person.service.ProfileService;
import com.stag.identity.person.service.dto.PersonUpdateCommand;
import com.stag.identity.shared.config.SecurityConfig;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(PersonController.class)
@Import({ TestCacheConfig.class, TestSecurityConfig.class, SecurityConfig.class })
@ActiveProfiles("test")
class PersonControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private BankingService bankingService;

    @MockitoBean
    private EducationService educationService;

    @Test
    @DisplayName("should return 200 OK with person profile when valid personId and English language provided")
    void getPersonProfile_ValidPersonIdAndEnglishLanguage_ReturnsOkWithPersonProfile() {
        Integer personId = 12345;
        String language = "en";

        Profile profile = Profile.builder()
                                 .personId(personId)
                                 .studentIds(List.of("S001", "S002"))
                                 .firstName("John")
                                 .lastName("Doe")
                                 .birthDate(LocalDate.of(1990, 5, 15))
                                 .gender("M")
                                 .build();

        when(profileService.getPersonProfile(personId, language)).thenReturn(profile);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.personId").isEqualTo(personId);
                json.assertThat().extractingPath("$.studentIds").asArray().containsExactly("S001", "S002").hasSize(2);
                json.assertThat().extractingPath("$.firstName").isEqualTo("John");
                json.assertThat().extractingPath("$.lastName").isEqualTo("Doe");
                json.assertThat().extractingPath("$.birthDate").isEqualTo("1990-05-15");
                json.assertThat().extractingPath("$.gender").isEqualTo("M");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return profile with Czech localization when Accept-Language is cs")
    void getPersonProfile_CzechLanguage_ReturnsLocalizedProfile() {
        Integer personId = 12345;
        String language = "cs";

        Profile profile = Profile.builder()
                                 .personId(personId)
                                 .firstName("Jan")
                                 .lastName("Novák")
                                 .birthDate(LocalDate.of(1990, 5, 15))
                                 .build();

        when(profileService.getPersonProfile(personId, language)).thenReturn(profile);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.personId").isEqualTo(personId);
                json.assertThat().extractingPath("$.firstName").isEqualTo("Jan");
                json.assertThat().extractingPath("$.lastName").isEqualTo("Novák");
                json.assertThat().extractingPath("$.birthDate").isEqualTo("1990-05-15");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return profile with student IDs array")
    void getPersonProfile_WithStudentIds_ReturnsStudentIdsArray() {
        Integer personId = 12345;
        String language = "en";

        Profile profile = Profile.builder()
                                 .personId(personId)
                                 .studentIds(List.of("S001", "S002", "S003"))
                                 .firstName("John")
                                 .lastName("Doe")
                                 .birthDate(LocalDate.of(1990, 5, 15))
                                 .build();

        when(profileService.getPersonProfile(personId, language)).thenReturn(profile);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.personId").isEqualTo(personId);
                json.assertThat().extractingPath("$.studentIds").asArray().containsExactly("S001", "S002", "S003").hasSize(3);
                json.assertThat().extractingPath("$.firstName").isEqualTo("John");
                json.assertThat().extractingPath("$.lastName").isEqualTo("Doe");
                json.assertThat().extractingPath("$.birthDate").isEqualTo("1990-05-15");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return profile with empty student IDs when person has no student records")
    void getPersonProfile_NoStudentIds_ReturnsEmptyArray() {
        Integer personId = 12345;
        String language = "en";

        Profile profile = Profile.builder()
                                 .personId(personId)
                                 .studentIds(List.of())
                                 .firstName("Jane")
                                 .lastName("Smith")
                                 .birthDate(LocalDate.of(1985, 3, 20))
                                 .build();

        when(profileService.getPersonProfile(personId, language)).thenReturn(profile);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.personId").isEqualTo(personId);
                json.assertThat().extractingPath("$.studentIds").asArray().isEmpty();
                json.assertThat().extractingPath("$.firstName").isEqualTo("Jane");
                json.assertThat().extractingPath("$.lastName").isEqualTo("Smith");
                json.assertThat().extractingPath("$.birthDate").isEqualTo("1985-03-20");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return profile with complete contact information")
    void getPersonProfile_CompleteContactInfo_ReturnsAllContactFields() {
        Integer personId = 12345;
        String language = "en";

        Profile.Contact contact = new Profile.Contact(
            "john.doe@example.com",
            "+420123456789",
            "+420987654321",
            "abc123xyz"
        );

        Profile profile = Profile.builder()
                                 .personId(personId)
                                 .firstName("John")
                                 .lastName("Doe")
                                 .birthDate(LocalDate.of(1990, 5, 15))
                                 .contact(contact)
                                 .build();

        when(profileService.getPersonProfile(personId, language)).thenReturn(profile);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.personId").isEqualTo(personId);
                json.assertThat().extractingPath("$.contact.email").isEqualTo("john.doe@example.com");
                json.assertThat().extractingPath("$.contact.phone").isEqualTo("+420123456789");
                json.assertThat().extractingPath("$.contact.mobile").isEqualTo("+420987654321");
                json.assertThat().extractingPath("$.contact.dataBox").isEqualTo("abc123xyz");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return 204 No Content when updating person profile with valid request")
    void updatePersonProfile_ValidRequest_ReturnsNoContent() {
        Integer personId = 12345;

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                        {
                          "contact": {
                            "email": "john.doe@example.com",
                            "phone": "+420123456789",
                            "mobile": "+420987654321"
                          }
                        }
                        """))
            .hasStatus(204);

        verify(profileService).updatePersonProfile(eq(personId), any(PersonUpdateCommand.class));
    }

    @Test
    @DisplayName("should return 200 OK with addresses when valid personId and language provided")
    void getPersonAddresses_ValidRequest_ReturnsAddresses() {
        Integer personId = 12345;
        String language = "en";

        Addresses.Address permanentAddress = Addresses.Address.builder()
                                                              .street("Main Street")
                                                              .streetNumber("123")
                                                              .zipCode("10000")
                                                              .municipality("Prague")
                                                              .country("Czech Republic")
                                                              .build();

        Addresses addresses = Addresses.builder()
                                       .permanentAddress(permanentAddress)
                                       .build();

        when(addressService.getPersonAddresses(personId, language)).thenReturn(addresses);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/addresses", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.permanentAddress.street").isEqualTo("Main Street");
                json.assertThat().extractingPath("$.permanentAddress.streetNumber").isEqualTo("123");
                json.assertThat().extractingPath("$.permanentAddress.zipCode").isEqualTo("10000");
                json.assertThat().extractingPath("$.permanentAddress.municipality").isEqualTo("Prague");
                json.assertThat().extractingPath("$.permanentAddress.country").isEqualTo("Czech Republic");
            });

        verify(addressService).getPersonAddresses(personId, language);
    }

    @Test
    @DisplayName("should return both permanent and temporary addresses")
    void getPersonAddresses_BothAddressTypes_ReturnsBothAddresses() {
        Integer personId = 12345;
        String language = "cs";

        Addresses.Address permanentAddress = Addresses.Address.builder()
                                                              .street("Hlavní")
                                                              .streetNumber("1")
                                                              .zipCode("11000")
                                                              .municipality("Praha")
                                                              .country("Česká republika")
                                                              .build();

        Addresses.Address temporaryAddress = Addresses.Address.builder()
                                                              .street("Vedlejší")
                                                              .streetNumber("2")
                                                              .zipCode("60200")
                                                              .municipality("Brno")
                                                              .country("Česká republika")
                                                              .build();

        Addresses addresses = Addresses.builder()
                                       .permanentAddress(permanentAddress)
                                       .temporaryAddress(temporaryAddress)
                                       .build();

        when(addressService.getPersonAddresses(personId, language)).thenReturn(addresses);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/addresses", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.permanentAddress.street").isEqualTo("Hlavní");
                json.assertThat().extractingPath("$.permanentAddress.streetNumber").isEqualTo("1");
                json.assertThat().extractingPath("$.permanentAddress.zipCode").isEqualTo("11000");
                json.assertThat().extractingPath("$.permanentAddress.municipality").isEqualTo("Praha");
                json.assertThat().extractingPath("$.permanentAddress.country").isEqualTo("Česká republika");
                json.assertThat().extractingPath("$.temporaryAddress.street").isEqualTo("Vedlejší");
                json.assertThat().extractingPath("$.temporaryAddress.streetNumber").isEqualTo("2");
                json.assertThat().extractingPath("$.temporaryAddress.zipCode").isEqualTo("60200");
                json.assertThat().extractingPath("$.temporaryAddress.municipality").isEqualTo("Brno");
                json.assertThat().extractingPath("$.temporaryAddress.country").isEqualTo("Česká republika");
            });

        verify(addressService).getPersonAddresses(personId, language);
    }

    @Test
    @DisplayName("should return foreign addresses when person has international residency")
    void getPersonAddresses_ForeignAddresses_ReturnsForeignAddresses() {
        Integer personId = 12345;
        String language = "en";

        Addresses.ForeignAddress foreignPermanent = Addresses.ForeignAddress.builder()
                                                                            .zipCode("SW1A 1AA")
                                                                            .municipality("London")
                                                                            .district("Westminster")
                                                                            .postOffice("London Central")
                                                                            .build();

        Addresses addresses = Addresses.builder()
                                       .foreignPermanentAddress(foreignPermanent)
                                       .build();

        when(addressService.getPersonAddresses(personId, language)).thenReturn(addresses);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/addresses", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.foreignPermanentAddress.zipCode").isEqualTo("SW1A 1AA");
                json.assertThat().extractingPath("$.foreignPermanentAddress.municipality").isEqualTo("London");
                json.assertThat().extractingPath("$.foreignPermanentAddress.district").isEqualTo("Westminster");
                json.assertThat().extractingPath("$.foreignPermanentAddress.postOffice").isEqualTo("London Central");
            });

        verify(addressService).getPersonAddresses(personId, language);
    }

    @Test
    @DisplayName("should return 200 OK with banking information when valid personId and language provided")
    void getPersonBanking_ValidRequest_ReturnsBanking() {
        Integer personId = 12345;
        String language = "en";

        Banking.BankAccount bankAccount = Banking.BankAccount.builder()
                                                             .owner("John Doe")
                                                             .address("Main Street 123")
                                                             .prefix("123456")
                                                             .suffix("0287")
                                                             .bankCode("0100")
                                                             .bankName("Komerční banka")
                                                             .iban("CZ6501000001231234560287")
                                                             .currency("CZK")
                                                             .build();

        Banking banking = Banking.builder()
                                 .account(bankAccount)
                                 .build();

        when(bankingService.getPersonBanking(personId, language)).thenReturn(banking);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/banking", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.account.holderName").isEqualTo("John Doe");
                json.assertThat().extractingPath("$.account.holderAddress").isEqualTo("Main Street 123");
                json.assertThat().extractingPath("$.account.accountNumberPrefix").isEqualTo("123456");
                json.assertThat().extractingPath("$.account.accountNumberSuffix").isEqualTo("0287");
                json.assertThat().extractingPath("$.account.bankCode").isEqualTo("0100");
                json.assertThat().extractingPath("$.account.bankName").isEqualTo("Komerční banka");
                json.assertThat().extractingPath("$.account.iban").isEqualTo("CZ6501000001231234560287");
                json.assertThat().extractingPath("$.account.currency").isEqualTo("CZK");
            });

        verify(bankingService).getPersonBanking(personId, language);
    }

    @Test
    @DisplayName("should return both Czech and Euro bank accounts")
    void getPersonBanking_BothAccountTypes_ReturnsBothAccounts() {
        Integer personId = 12345;
        String language = "en";

        Banking.BankAccount czechAccount = Banking.BankAccount.builder()
                                                              .owner("John Doe")
                                                              .prefix("123456")
                                                              .suffix("0287")
                                                              .bankCode("0100")
                                                              .currency("CZK")
                                                              .iban("CZ6501000001231234560287")
                                                              .build();

        Banking.EuroBankAccount euroAccount = Banking.EuroBankAccount.builder()
                                                                     .owner("John Doe")
                                                                     .prefix("654321")
                                                                     .suffix("0287")
                                                                     .bankCode("0800")
                                                                     .currency("EUR")
                                                                     .swiftCode("GIBACZPX")
                                                                     .country("Czech Republic")
                                                                     .iban("CZ6508000006543210287")
                                                                     .build();

        Banking banking = Banking.builder()
                                 .account(czechAccount)
                                 .euroAccount(euroAccount)
                                 .build();

        when(bankingService.getPersonBanking(personId, language)).thenReturn(banking);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/banking", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.account.holderName").isEqualTo("John Doe");
                json.assertThat().extractingPath("$.account.accountNumberPrefix").isEqualTo("123456");
                json.assertThat().extractingPath("$.account.accountNumberSuffix").isEqualTo("0287");
                json.assertThat().extractingPath("$.account.bankCode").isEqualTo("0100");
                json.assertThat().extractingPath("$.account.currency").isEqualTo("CZK");
                json.assertThat().extractingPath("$.account.iban").isEqualTo("CZ6501000001231234560287");
                json.assertThat().extractingPath("$.euroAccount.bankCode").isEqualTo("0800");
                json.assertThat().extractingPath("$.euroAccount.currency").isEqualTo("EUR");
                json.assertThat().extractingPath("$.euroAccount.swiftCode").isEqualTo("GIBACZPX");
                json.assertThat().extractingPath("$.euroAccount.country").isEqualTo("Czech Republic");
                json.assertThat().extractingPath("$.euroAccount.iban").isEqualTo("CZ6508000006543210287");
            });

        verify(bankingService).getPersonBanking(personId, language);
    }

    @Test
    @DisplayName("should return Euro account with SWIFT code for international transfers")
    void getPersonBanking_EuroAccount_ReturnsSwiftCode() {
        Integer personId = 12345;
        String language = "en";

        Banking.EuroBankAccount euroAccount = Banking.EuroBankAccount.builder()
                                                                     .owner("John Doe")
                                                                     .currency("EUR")
                                                                     .swiftCode("GIBACZPX")
                                                                     .country("Czech Republic")
                                                                     .iban("CZ6508000006543210287")
                                                                     .build();

        Banking banking = Banking.builder()
                                 .euroAccount(euroAccount)
                                 .build();

        when(bankingService.getPersonBanking(personId, language)).thenReturn(banking);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/banking", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.euroAccount.holderName").isEqualTo("John Doe");
                json.assertThat().extractingPath("$.euroAccount.currency").isEqualTo("EUR");
                json.assertThat().extractingPath("$.euroAccount.swiftCode").isEqualTo("GIBACZPX");
                json.assertThat().extractingPath("$.euroAccount.country").isEqualTo("Czech Republic");
                json.assertThat().extractingPath("$.euroAccount.iban").isEqualTo("CZ6508000006543210287");
            });

        verify(bankingService).getPersonBanking(personId, language);
    }

    @Test
    @DisplayName("should return 200 OK with education information when valid personId and language provided")
    void getPersonEducation_ValidRequest_ReturnsEducation() {
        Integer personId = 12345;
        String language = "en";

        Education.HighSchoolAddress address = Education.HighSchoolAddress.builder()
                                                                         .street("School Street")
                                                                         .zipCode("11000")
                                                                         .municipality("Prague")
                                                                         .country("Czech Republic")
                                                                         .build();

        Education.HighSchool highSchool = Education.HighSchool.builder()
                                                              .name("Prague Technical High School")
                                                              .fieldOfStudy("Computer Science")
                                                              .graduationDate(LocalDate.of(2015, 6, 30))
                                                              .address(address)
                                                              .build();

        Education education = Education.builder()
                                       .highSchool(highSchool)
                                       .build();

        when(educationService.getPersonEducation(personId, language)).thenReturn(education);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/education", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.highSchool.schoolName").isEqualTo("Prague Technical High School");
                json.assertThat().extractingPath("$.highSchool.fieldOfStudy").isEqualTo("Computer Science");
                json.assertThat().extractingPath("$.highSchool.graduationDate").isEqualTo("2015-06-30");
                json.assertThat().extractingPath("$.highSchool.address.street").isEqualTo("School Street");
                json.assertThat().extractingPath("$.highSchool.address.zipCode").isEqualTo("11000");
                json.assertThat().extractingPath("$.highSchool.address.municipality").isEqualTo("Prague");
                json.assertThat().extractingPath("$.highSchool.address.country").isEqualTo("Czech Republic");
            });

        verify(educationService).getPersonEducation(personId, language);
    }

    @Test
    @DisplayName("should return high school with graduation date")
    void getPersonEducation_HighSchool_ReturnsGraduationDate() {
        Integer personId = 12345;
        String language = "en";

        Education.HighSchool highSchool = Education.HighSchool.builder()
                                                              .name("Prague High School")
                                                              .fieldOfStudy("Mathematics")
                                                              .graduationDate(LocalDate.of(2015, 6, 30))
                                                              .build();

        Education education = Education.builder()
                                       .highSchool(highSchool)
                                       .build();

        when(educationService.getPersonEducation(personId, language)).thenReturn(education);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/education", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.highSchool.schoolName").isEqualTo("Prague High School");
                json.assertThat().extractingPath("$.highSchool.fieldOfStudy").isEqualTo("Mathematics");
                json.assertThat().extractingPath("$.highSchool.graduationDate").isEqualTo("2015-06-30");
            });

        verify(educationService).getPersonEducation(personId, language);
    }

    @Test
    @DisplayName("should return both Czech and foreign high school education")
    void getPersonEducation_BothSchoolTypes_ReturnsBothSchools() {
        Integer personId = 12345;
        String language = "en";

        Education.HighSchool czechHighSchool = Education.HighSchool.builder()
                                                                   .name("Prague High School")
                                                                   .fieldOfStudy("Mathematics")
                                                                   .graduationDate(LocalDate.of(2015, 6, 30))
                                                                   .build();

        Education.ForeignHighSchool foreignHighSchool = Education.ForeignHighSchool.builder()
                                                                                   .name("London High School")
                                                                                   .location("London, UK")
                                                                                   .fieldOfStudy("Physics")
                                                                                   .build();

        Education education = Education.builder()
                                       .highSchool(czechHighSchool)
                                       .foreignHighSchool(foreignHighSchool)
                                       .build();

        when(educationService.getPersonEducation(personId, language)).thenReturn(education);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/education", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.highSchool.schoolName").isEqualTo("Prague High School");
                json.assertThat().extractingPath("$.highSchool.fieldOfStudy").isEqualTo("Mathematics");
                json.assertThat().extractingPath("$.highSchool.graduationDate").isEqualTo("2015-06-30");
                json.assertThat().extractingPath("$.foreignHighSchool.schoolName").isEqualTo("London High School");
                json.assertThat().extractingPath("$.foreignHighSchool.location").isEqualTo("London, UK");
                json.assertThat().extractingPath("$.foreignHighSchool.fieldOfStudy").isEqualTo("Physics");
            });

        verify(educationService).getPersonEducation(personId, language);
    }

    @Test
    @DisplayName("should return foreign high school with location when person studied abroad")
    void getPersonEducation_ForeignHighSchool_ReturnsLocation() {
        Integer personId = 12345;
        String language = "en";

        Education.ForeignHighSchool foreignHighSchool = Education.ForeignHighSchool.builder()
                                                                                   .name("Oxford High School")
                                                                                   .location("Oxford, United Kingdom")
                                                                                   .fieldOfStudy("Literature")
                                                                                   .build();

        Education education = Education.builder()
                                       .foreignHighSchool(foreignHighSchool)
                                       .build();

        when(educationService.getPersonEducation(personId, language)).thenReturn(education);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/education", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.foreignHighSchool.schoolName").isEqualTo("Oxford High School");
                json.assertThat().extractingPath("$.foreignHighSchool.location").isEqualTo("Oxford, United Kingdom");
                json.assertThat().extractingPath("$.foreignHighSchool.fieldOfStudy").isEqualTo("Literature");
            });

        verify(educationService).getPersonEducation(personId, language);
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "/api/v1/persons/{personId}",
            "/api/v1/persons/{personId}/addresses",
            "/api/v1/persons/{personId}/banking",
            "/api/v1/persons/{personId}/education"
        }
    )
    @DisplayName("should return 401 Unauthorized when no JWT token provided for GET endpoints")
    void getEndpoints_NoJwtToken_Returns401(String uriTemplate) {
        Integer personId = 12345;
        String language = "en";

        assertThat(mvc.get()
                      .uri(uriTemplate, personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON))
            .hasStatus(401);
    }

    @Test
    @DisplayName("should return 401 Unauthorized when no JWT token provided for updatePersonProfile")
    void updatePersonProfile_NoJwtToken_Returns401() {
        Integer personId = 12345;

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                        {
                          "contact": {
                            "email": "test@example.com"
                          }
                        }
                        """))
            .hasStatus(401);
    }

    @ParameterizedTest
    @CsvSource({
        "/api/v1/persons/{personId}, profile",
        "/api/v1/persons/{personId}/addresses, addresses",
        "/api/v1/persons/{personId}/banking, banking",
        "/api/v1/persons/{personId}/education, education"
    })
    @DisplayName("should return 404 Not Found when person does not exist for GET endpoints")
    void getEndpoints_PersonNotFound_Returns404(String uriTemplate, String serviceType) {
        Integer personId = 99999;
        String language = "en";

        // Mock the appropriate service
        switch (serviceType) {
            case "profile" -> when(profileService.getPersonProfile(personId, language))
                .thenThrow(new PersonNotFoundException(personId));
            case "addresses" -> when(addressService.getPersonAddresses(personId, language))
                .thenThrow(new PersonNotFoundException(personId));
            case "banking" -> when(bankingService.getPersonBanking(personId, language))
                .thenThrow(new PersonNotFoundException(personId));
            case "education" -> when(educationService.getPersonEducation(personId, language))
                .thenThrow(new PersonNotFoundException(personId));
            default -> { /* empty */ }
        }

        assertThat(mvc.get()
                      .uri(uriTemplate, personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(404)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(404);
                json.assertThat().extractingPath("$.title").isEqualTo("Person Not Found");
                json.assertThat().extractingPath("$.detail").asString().contains("Person with ID: 99999 not found");
            });

        // Verify the appropriate service was called
        switch (serviceType) {
            case "profile" -> verify(profileService).getPersonProfile(personId, language);
            case "addresses" -> verify(addressService).getPersonAddresses(personId, language);
            case "banking" -> verify(bankingService).getPersonBanking(personId, language);
            case "education" -> verify(educationService).getPersonEducation(personId, language);
            default -> { /* empty */ }
        }
    }

    @Test
    @DisplayName("should return 500 Internal Server Error when fetching person data failed in getPersonProfile")
    void getPersonProfile_PersonProfileFetchFailed_Returns500() {
        Integer personId = 99999;
        String language = "en";

        doThrow(new PersonProfileFetchException(personId, new RuntimeException("fetch failed")))
            .when(profileService).getPersonProfile(personId, language);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(""))
            .hasStatus(500)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(500);
                json.assertThat().extractingPath("$.title").isEqualTo("Person Profile Fetch Error");
                json.assertThat().extractingPath("$.detail").asString().contains("Failed to fetch person profile for personId=99999");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return 404 Not Found when person does not exist for updatePersonProfile")
    void updatePersonProfile_PersonNotFound_Returns404() {
        Integer personId = 99999;

        doThrow(new PersonNotFoundException(personId)).when(profileService).updatePersonProfile(eq(personId), any(PersonUpdateCommand.class));

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                               {
                                 "contact": {
                                   "email": "test@example.com"
                                 }
                               }
                               """))
            .hasStatus(404)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(404);
                json.assertThat().extractingPath("$.title").isEqualTo("Person Not Found");
                json.assertThat().extractingPath("$.detail").asString().contains("Person with ID: 99999 not found");
            });

        verify(profileService).updatePersonProfile(eq(personId), any(PersonUpdateCommand.class));
    }

    @Test
    @DisplayName("should return 400 Bad Request when invalid JSON provided for updatePersonProfile")
    void updatePersonProfile_InvalidJson_Returns400() {
        Integer personId = 12345;

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("{ invalid json }"))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when empty request body provided for updatePersonProfile")
    void updatePersonProfile_EmptyBody_Returns400() {
        Integer personId = 12345;

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(""))
            .hasStatus(400);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidValidationCases")
    @DisplayName("should return 400 Bad Request when validation fails")
    void updatePersonProfile_InvalidValidation_Returns400(String testName, String requestBody) {
        Integer personId = 12345;

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(requestBody))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    private static Stream<Arguments> provideInvalidValidationCases() {
        return Stream.of(
            Arguments.of("invalid email format",
                """
                {
                  "contact": {
                    "email": "invalid-email"
                  }
                }
                """
            ),
            Arguments.of("invalid phone pattern",
                """
                {
                  "contact": {
                    "phone": "invalid-phone-123abc"
                  }
                }
                """
            ),
            Arguments.of("phone exceeds max length",
                """
                {
                  "contact": {
                    "phone": "+123456789012345678901"
                  }
                }
                """
            ),
            Arguments.of("invalid mobile pattern",
                """
                {
                  "contact": {
                    "mobile": "abc123"
                  }
                }
                """
            ),
            Arguments.of("mobile exceeds max length",
                """
                {
                  "contact": {
                    "mobile": "+1234567890123456789012345678901"
                  }
                }
                """
            ),
            Arguments.of("invalid dataBox pattern",
                """
                {
                  "contact": {
                    "dataBox": "INVALID"
                  }
                }
                """
            ),
            Arguments.of("dataBox invalid length",
                """
                {
                  "contact": {
                    "dataBox": "abc12"
                  }
                }
                """
            ),
            Arguments.of("invalid account prefix pattern",
                """
                {
                  "bankAccount": {
                    "accountNumberPrefix": "abc123"
                  }
                }
                """
            ),
            Arguments.of("account prefix exceeds max length",
                """
                {
                  "bankAccount": {
                    "accountNumberPrefix": "1234567"
                  }
                }
                """
            ),
            Arguments.of("invalid account suffix pattern",
                """
                {
                  "bankAccount": {
                    "accountNumberSuffix": "abc123"
                  }
                }
                """
            ),
            Arguments.of("account suffix exceeds max length",
                """
                {
                  "bankAccount": {
                    "accountNumberSuffix": "12345678901"
                  }
                }
                """
            ),
            Arguments.of("invalid bank code pattern",
                """
                {
                  "bankAccount": {
                    "bankCode": "01AB"
                  }
                }
                """
            ),
            Arguments.of("bank code invalid length",
                """
                {
                  "bankAccount": {
                    "bankCode": "01"
                  }
                }
                """
            )
        );
    }

    @Test
    @DisplayName("should return 400 Bad Request when holder name exceeds max length")
    void updatePersonProfile_HolderNameExceedsMaxLength_Returns400() {
        Integer personId = 12345;
        String longName = "a".repeat(256);

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                               {
                                 "bankAccount": {
                                   "holderName": "%s"
                                 }
                               }
                               """.formatted(longName)))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when holder address exceeds max length")
    void updatePersonProfile_HolderAddressExceedsMaxLength_Returns400() {
        Integer personId = 12345;
        String longAddress = "a".repeat(256);

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                               {
                                 "bankAccount": {
                                   "holderAddress": "%s"
                                 }
                               }
                               """.formatted(longAddress)))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when birth surname exceeds max length")
    void updatePersonProfile_BirthSurnameExceedsMaxLength_Returns400() {
        Integer personId = 12345;
        String longSurname = "a".repeat(101);

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("{ \"birthSurname\": \"%s\" }".formatted(longSurname)))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when birth surname contains invalid characters")
    void updatePersonProfile_BirthSurnameInvalidPattern_Returns400() {
        Integer personId = 12345;

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("{ \"birthSurname\": \"!!!\" }"))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when marital status exceeds max length")
    void updatePersonProfile_MaritalStatusExceedsMaxLength_Returns400() {
        Integer personId = 12345;
        String longStatus = "a".repeat(241);

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("{ \"maritalStatus\": \"%s\" }".formatted(longStatus)))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when title prefix exceeds max length")
    void updatePersonProfile_TitlePrefixExceedsMaxLength_Returns400() {
        Integer personId = 12345;
        String longTitle = "a".repeat(241);

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                               {
                                 "titles": {
                                   "prefix": "%s"
                                 }
                               }
                               """.formatted(longTitle)))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when title suffix exceeds max length")
    void updatePersonProfile_TitleSuffixExceedsMaxLength_Returns400() {
        Integer personId = 12345;
        String longTitle = "a".repeat(241);

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                               {
                                 "titles": {
                                   "suffix": "%s"
                                 }
                               }
                               """.formatted(longTitle)))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
            });
    }

    @Test
    @DisplayName("should return 403 Forbidden when AccessDeniedException is thrown")
    void getPersonProfile_AccessDenied_Returns403() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(new AccessDeniedException("Access is denied"));

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(403)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(403);
                json.assertThat().extractingPath("$.title").isEqualTo("Access Denied");
                json.assertThat().extractingPath("$.detail").asString().contains("Access is denied");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return 400 Bad Request when InvalidAccountNumberException is thrown")
    void updatePersonProfile_InvalidAccountNumber_Returns400() {
        Integer personId = 12345;

        doThrow(new InvalidAccountNumberException("Invalid account number format"))
            .when(profileService).updatePersonProfile(eq(personId), any(PersonUpdateCommand.class));

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                               {
                                 "bankAccount": {
                                   "accountNumberSuffix": "123456"
                                 }
                               }
                               """))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
                json.assertThat().extractingPath("$.detail").asString().contains("Invalid account number format");
            });

        verify(profileService).updatePersonProfile(eq(personId), any(PersonUpdateCommand.class));
    }

    @Test
    @DisplayName("should return 400 Bad Request when InvalidDataBoxException is thrown")
    void updatePersonProfile_InvalidDataBox_Returns400() {
        Integer personId = 12345;

        doThrow(new InvalidDataBoxException("Invalid data box ID"))
            .when(profileService).updatePersonProfile(eq(personId), any(PersonUpdateCommand.class));

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                               {
                                 "contact": {
                                   "dataBox": "invalid"
                                 }
                               }
                               """))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
                json.assertThat().extractingPath("$.errors").asMap().containsKey("contact.dataBox");
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when InvalidBankAccountException is thrown")
    void updatePersonProfile_InvalidBankAccount_Returns400() {
        Integer personId = 12345;

        doThrow(new InvalidBankAccountException("Invalid bank account"))
            .when(profileService).updatePersonProfile(eq(personId), any(PersonUpdateCommand.class));

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", personId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                               {
                                 "bankAccount": {
                                   "accountNumberSuffix": "123456",
                                   "bankCode": "0100"
                                 }
                               }
                               """))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
                json.assertThat().extractingPath("$.detail").asString().contains("Invalid bank account");
            });

        verify(profileService).updatePersonProfile(eq(personId), any(PersonUpdateCommand.class));
    }

    @Test
    @DisplayName("should return 503 Service Unavailable when CallNotPermittedException is thrown")
    void getPersonProfile_CircuitBreakerOpen_Returns503() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(CallNotPermittedException.class);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(503)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(503);
                json.assertThat().extractingPath("$.title").isEqualTo("Service Unavailable");
                json.assertThat().extractingPath("$.detail").asString().contains("Service is currently unavailable");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return 404 Not Found when StatusRuntimeException with NOT_FOUND is thrown")
    void getPersonProfile_GrpcNotFound_Returns404() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(404)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(404);
                json.assertThat().extractingPath("$.title").isEqualTo("Resource Not Found");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @ParameterizedTest(name = "should return mapped status for gRPC status {0}")
    @EnumSource(value = Status.Code.class, names = {
        "PERMISSION_DENIED", "UNAUTHENTICATED", "ALREADY_EXISTS",
        "FAILED_PRECONDITION", "UNIMPLEMENTED"
    })
    @DisplayName("should return mapped status for other gRPC statuses")
    void getStudentProfile_OtherGrpcStatuses_ReturnsMappedStatus(Status.Code code) {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(new StatusRuntimeException(Status.fromCode(code)));

        int expectedStatus = switch (code) {
            case PERMISSION_DENIED -> 403;
            case UNAUTHENTICATED -> 401;
            case ALREADY_EXISTS -> 409;
            case FAILED_PRECONDITION -> 412;
            case UNIMPLEMENTED -> 501;
            default -> 500;
        };

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(expectedStatus)
            .bodyJson()
            .satisfies(json -> json.assertThat().extractingPath("$.status").isEqualTo(expectedStatus));

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return 400 Bad Request when StatusRuntimeException with INVALID_ARGUMENT is thrown")
    void getPersonProfile_GrpcInvalidArgument_Returns400() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Argument");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return 503 Service Unavailable when StatusRuntimeException with UNAVAILABLE is thrown")
    void getPersonProfile_GrpcUnavailable_Returns503() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(503)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(503);
                json.assertThat().extractingPath("$.title").isEqualTo("Service Unavailable");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return 504 Gateway Timeout when StatusRuntimeException with DEADLINE_EXCEEDED is thrown")
    void getPersonProfile_GrpcDeadlineExceeded_Returns504() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(new StatusRuntimeException(Status.DEADLINE_EXCEEDED));

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(504)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(504);
                json.assertThat().extractingPath("$.title").isEqualTo("Gateway Timeout");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should unwrap and handle CompletionException with StatusRuntimeException")
    void getPersonProfile_CompletionExceptionWithGrpc_Returns404() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(new CompletionException(new StatusRuntimeException(Status.NOT_FOUND)));

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(404)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(404);
                json.assertThat().extractingPath("$.title").isEqualTo("Resource Not Found");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should unwrap and handle ExecutionException with CallNotPermittedException")
    void getPersonProfile_ExecutionExceptionWithCircuitBreaker_Returns503() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(CallNotPermittedException.class);

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(503)
            .debug()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(503);
                json.assertThat().extractingPath("$.title").isEqualTo("Service Unavailable");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return 500 Internal Server Error for unexpected exceptions")
    void getPersonProfile_UnexpectedException_Returns500() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(new RuntimeException("Unexpected error"));

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(500)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(500);
                json.assertThat().extractingPath("$.title").isEqualTo("Internal Server Error");
                json.assertThat().extractingPath("$.detail").asString().contains("Unexpected error");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @Test
    @DisplayName("should return 500 Internal Server Error when CompletionException wraps generic exception")
    void getPersonProfile_CompletionExceptionWithGenericException_Returns500() {
        Integer personId = 12345;
        String language = "en";

        when(profileService.getPersonProfile(personId, language))
            .thenThrow(new CompletionException(new RuntimeException("Unexpected error")));

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", personId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(500)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(500);
                json.assertThat().extractingPath("$.title").isEqualTo("Internal Server Error");
            });

        verify(profileService).getPersonProfile(personId, language);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, -100})
    @DisplayName("should return 400 Bad Request when personId is negative or zero (ConstraintViolationException)")
    void getPersonProfile_InvalidPersonId_Returns400(Integer invalidPersonId) {
        String language = "en";

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}", invalidPersonId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
                json.assertThat().extractingPath("$.violations").isNotNull();
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when personId is negative in updatePersonProfile (ConstraintViolationException)")
    void updatePersonProfile_NegativePersonId_Returns400() {
        Integer invalidPersonId = -1;

        assertThat(mvc.patch()
                      .uri("/api/v1/persons/{personId}", invalidPersonId)
                      .with(jwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                               {
                                 "contact": {
                                   "email": "test@example.com"
                                 }
                               }
                               """))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
                json.assertThat().extractingPath("$.violations").isNotNull();
                json.assertThat().extractingPath("$.violations.personId").asArray().isNotEmpty();
            });
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    @DisplayName("should return 400 Bad Request when personId is invalid in getPersonAddresses (ConstraintViolationException)")
    void getPersonAddresses_InvalidPersonId_Returns400(Integer invalidPersonId) {
        String language = "cs";

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/addresses", invalidPersonId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
                json.assertThat().extractingPath("$.violations").isNotNull();
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when personId is zero in getPersonBanking (ConstraintViolationException)")
    void getPersonBanking_ZeroPersonId_Returns400() {
        Integer invalidPersonId = 0;
        String language = "en";

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/banking", invalidPersonId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
                json.assertThat().extractingPath("$.violations").isNotNull();
            });
    }

    @Test
    @DisplayName("should return 400 Bad Request when personId is negative in getPersonEducation (ConstraintViolationException)")
    void getPersonEducation_NegativePersonId_Returns400() {
        Integer invalidPersonId = -999;
        String language = "cs";

        assertThat(mvc.get()
                      .uri("/api/v1/persons/{personId}/education", invalidPersonId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(jwt()))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
                json.assertThat().extractingPath("$.violations").isNotNull();
            });
    }

}
