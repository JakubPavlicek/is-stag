package com.stag.identity.person.repository;

import com.stag.identity.config.TestCacheConfig;
import com.stag.identity.config.TestOracleContainerConfig;
import com.stag.identity.person.entity.Person;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.repository.projection.EducationView;
import com.stag.identity.person.repository.projection.ProfileView;
import com.stag.identity.person.repository.projection.SimpleProfileView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class })
@ActiveProfiles("test")
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return person when person exists")
        void shouldReturnPersonWhenPersonExists() {
            Person person = createTestPerson(1);
            personRepository.saveAndFlush(person);

            Optional<Person> result = personRepository.findById(1);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1);
            assertThat(result.get().getFirstName()).isEqualTo("Jan");
            assertThat(result.get().getLastName()).isEqualTo("Novák");
        }

        @Test
        @DisplayName("should return empty when person does not exist")
        void shouldReturnEmptyWhenPersonDoesNotExist() {
            Optional<Person> result = personRepository.findById(999);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById with projection")
    class FindByIdWithProjection {

        @Test
        @DisplayName("should return ProfileView when person exists")
        void shouldReturnProfileViewWhenPersonExists() {
            Person person = createTestPerson(1);
            personRepository.saveAndFlush(person);

            Optional<ProfileView> result = personRepository.findById(1, ProfileView.class);

            assertThat(result).isPresent();
            ProfileView profile = result.get();
            assertThat(profile.id()).isEqualTo(1);
            assertThat(profile.firstName()).isEqualTo("Jan");
            assertThat(profile.lastName()).isEqualTo("Novák");
            assertThat(profile.birthNumber()).isEqualTo("9001010001");
            assertThat(profile.birthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
            assertThat(profile.gender()).isEqualTo("M");
            assertThat(profile.email()).isEqualTo("jan.novak@example.com");
            assertThat(profile.phone()).isEqualTo("+420123456789");
            assertThat(profile.mobile()).isEqualTo("+420987654321");
        }

        @Test
        @DisplayName("should return SimpleProfileView when person exists")
        void shouldReturnSimpleProfileViewWhenPersonExists() {
            Person person = createTestPerson(1);
            personRepository.saveAndFlush(person);

            Optional<SimpleProfileView> result = personRepository.findById(1, SimpleProfileView.class);

            assertThat(result).isPresent();
            SimpleProfileView profile = result.get();
            assertThat(profile.firstName()).isEqualTo("Jan");
            assertThat(profile.lastName()).isEqualTo("Novák");
            assertThat(profile.titlePrefix()).isEqualTo("Ing");
            assertThat(profile.titleSuffix()).isEqualTo("PhD");
            assertThat(profile.gender()).isEqualTo("M");
        }

        @Test
        @DisplayName("should return empty when person does not exist for projection")
        void shouldReturnEmptyWhenPersonDoesNotExistForProjection() {
            Optional<ProfileView> result = personRepository.findById(999, ProfileView.class);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAddressesByPersonId")
    class FindAddressesByPersonId {

        @Test
        @DisplayName("should return address view with all address types")
        void shouldReturnAddressViewWithAllAddressTypes() {
            Person person = createTestPerson(1);
            person.setStreet("Hlavní");
            person.setStreetNumber("123");
            person.setDomicileZipCode("30100");
            person.setDomicileMunicipalityPartId(1000L);
            person.setDomicileCountryId(203);
            person.setTemporaryStreet("Vedlejší");
            person.setTemporaryStreetNumber("456");
            person.setTemporaryZipCode("30200");
            person.setTemporaryMunicipalityPartId(2000L);
            person.setTemporaryCountryId(203);
            person.setZipCodeForeign("10115");
            person.setMunicipalityForeign("Berlin");
            person.setDistrictForeign("Mitte");
            person.setPostOfficeForeign("Berlin Mitte");
            personRepository.saveAndFlush(person);

            Optional<AddressView> result = personRepository.findAddressesByPersonId(1);

            assertThat(result).isPresent();
            AddressView address = result.get();
            assertThat(address.permanentStreet()).isEqualTo("Hlavní");
            assertThat(address.permanentStreetNumber()).isEqualTo("123");
            assertThat(address.permanentZipCode()).isEqualTo("30100");
            assertThat(address.permanentMunicipalityPartId()).isEqualTo(1000L);
            assertThat(address.permanentCountryId()).isEqualTo(203);
            assertThat(address.temporaryStreet()).isEqualTo("Vedlejší");
            assertThat(address.temporaryStreetNumber()).isEqualTo("456");
            assertThat(address.temporaryZipCode()).isEqualTo("30200");
            assertThat(address.temporaryMunicipalityPartId()).isEqualTo(2000L);
            assertThat(address.temporaryCountryId()).isEqualTo(203);
            assertThat(address.foreignPermanentZipCode()).isEqualTo("10115");
            assertThat(address.foreignPermanentMunicipality()).isEqualTo("Berlin");
            assertThat(address.foreignPermanentDistrict()).isEqualTo("Mitte");
            assertThat(address.foreignPermanentPostOffice()).isEqualTo("Berlin Mitte");
        }

        @Test
        @DisplayName("should return address view with minimal data")
        void shouldReturnAddressViewWithMinimalData() {
            Person person = createTestPerson(1);
            personRepository.saveAndFlush(person);

            Optional<AddressView> result = personRepository.findAddressesByPersonId(1);

            assertThat(result).isPresent();
            AddressView address = result.get();
            assertThat(address.permanentZipCode()).isEqualTo("0");
            assertThat(address.temporaryZipCode()).isEqualTo("0");
            assertThat(address.permanentCountryId()).isEqualTo(203);
            assertThat(address.temporaryCountryId()).isEqualTo(203);
        }

        @Test
        @DisplayName("should return empty when person does not exist")
        void shouldReturnEmptyWhenPersonDoesNotExist() {
            Optional<AddressView> result = personRepository.findAddressesByPersonId(999);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBankingByPersonId")
    class FindBankingByPersonId {

        @Test
        @DisplayName("should return banking view with standard account")
        void shouldReturnBankingViewWithStandardAccount() {
            Person person = createTestPerson(1);
            person.setAccountHolder("Jan Novák");
            person.setAccountAddress("Hlavní 123, Plzeň");
            person.setAccountPrefix("123456");
            person.setAccountSuffix("1234567890");
            person.setBankCode("0100");
            person.setAccountIban("CZ6508000000192000145399");
            person.setAccountCurrency("CZK");
            personRepository.saveAndFlush(person);

            Optional<BankView> result = personRepository.findBankingByPersonId(1);

            assertThat(result).isPresent();
            BankView banking = result.get();
            assertThat(banking.accountOwner()).isEqualTo("Jan Novák");
            assertThat(banking.accountAddress()).isEqualTo("Hlavní 123, Plzeň");
            assertThat(banking.accountPrefix()).isEqualTo("123456");
            assertThat(banking.accountSuffix()).isEqualTo("1234567890");
            assertThat(banking.accountBank()).isEqualTo("0100");
            assertThat(banking.accountIban()).isEqualTo("CZ6508000000192000145399");
            assertThat(banking.accountCurrency()).isEqualTo("CZK");
        }

        @Test
        @DisplayName("should return banking view with euro account")
        void shouldReturnBankingViewWithEuroAccount() {
            Person person = createTestPerson(1);
            person.setEuroAccountHolder("Jan Novák");
            person.setEuroAccountAddress("Hauptstraße 1, München");
            person.setEuroAccountPrefix("654321");
            person.setEuroAccountSuffix("9876543210");
            person.setEuroBankCode("7000");
            person.setEuroAccountIban("DE89370400440532013000");
            person.setEuroAccountCurrency("EUR");
            person.setEuroAccountCountryId(276);
            person.setEuroAccountSwiftCode("DEUTDEFFXXX");
            personRepository.saveAndFlush(person);

            Optional<BankView> result = personRepository.findBankingByPersonId(1);

            assertThat(result).isPresent();
            BankView banking = result.get();
            assertThat(banking.euroAccountOwner()).isEqualTo("Jan Novák");
            assertThat(banking.euroAccountAddress()).isEqualTo("Hauptstraße 1, München");
            assertThat(banking.euroAccountPrefix()).isEqualTo("654321");
            assertThat(banking.euroAccountSuffix()).isEqualTo("9876543210");
            assertThat(banking.euroAccountBank()).isEqualTo("7000");
            assertThat(banking.euroAccountIban()).isEqualTo("DE89370400440532013000");
            assertThat(banking.euroAccountCurrency()).isEqualTo("EUR");
            assertThat(banking.euroAccountCountryId()).isEqualTo(276);
            assertThat(banking.euroAccountSwiftCode()).isEqualTo("DEUTDEFFXXX");
        }

        @Test
        @DisplayName("should return banking view with both accounts")
        void shouldReturnBankingViewWithBothAccounts() {
            Person person = createTestPerson(1);
            person.setAccountPrefix("123456");
            person.setAccountSuffix("1234567890");
            person.setBankCode("0100");
            person.setAccountIban("CZ6508000000192000145399");
            person.setEuroAccountPrefix("654321");
            person.setEuroAccountSuffix("9876543210");
            person.setEuroBankCode("7000");
            person.setEuroAccountIban("DE89370400440532013000");
            personRepository.saveAndFlush(person);

            Optional<BankView> result = personRepository.findBankingByPersonId(1);

            assertThat(result).isPresent();
            BankView banking = result.get();
            assertThat(banking.accountIban()).isEqualTo("CZ6508000000192000145399");
            assertThat(banking.euroAccountIban()).isEqualTo("DE89370400440532013000");
        }

        @Test
        @DisplayName("should return empty when person does not exist")
        void shouldReturnEmptyWhenPersonDoesNotExist() {
            Optional<BankView> result = personRepository.findBankingByPersonId(999);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findEducationByPersonId")
    class FindEducationByPersonId {

        @Test
        @DisplayName("should return education view with Czech high school")
        void shouldReturnEducationViewWithCzechHighSchool() {
            Person person = createTestPerson(1);
            person.setHighSchoolId("600012345");
            person.setHighSchoolFieldOfStudyNumber("7941K");
            person.setHighSchoolCountryId(203);
            person.setGraduationDate(LocalDate.of(2008, 6, 15));
            personRepository.saveAndFlush(person);

            Optional<EducationView> result = personRepository.findEducationByPersonId(1);

            assertThat(result).isPresent();
            EducationView education = result.get();
            assertThat(education.highSchoolId()).isEqualTo("600012345");
            assertThat(education.highSchoolFieldOfStudyNumber()).isEqualTo("7941K");
            assertThat(education.highSchoolCountryId()).isEqualTo(203);
            assertThat(education.graduationDate()).isEqualTo(LocalDate.of(2008, 6, 15));
        }

        @Test
        @DisplayName("should return education view with foreign high school")
        void shouldReturnEducationViewWithForeignHighSchool() {
            Person person = createTestPerson(1);
            person.setHighSchoolForeign("Gymnasium München");
            person.setHighSchoolForeignPlace("München");
            person.setHighSchoolForeignFieldOfStudy("Mathematics and Physics");
            person.setHighSchoolCountryId(276);
            person.setGraduationDate(LocalDate.of(2009, 7, 20));
            personRepository.saveAndFlush(person);

            Optional<EducationView> result = personRepository.findEducationByPersonId(1);

            assertThat(result).isPresent();
            EducationView education = result.get();
            assertThat(education.highSchoolForeign()).isEqualTo("Gymnasium München");
            assertThat(education.highSchoolForeignPlace()).isEqualTo("München");
            assertThat(education.highSchoolForeignFieldOfStudy()).isEqualTo("Mathematics and Physics");
            assertThat(education.highSchoolCountryId()).isEqualTo(276);
            assertThat(education.graduationDate()).isEqualTo(LocalDate.of(2009, 7, 20));
        }

        @Test
        @DisplayName("should return empty when person does not exist")
        void shouldReturnEmptyWhenPersonDoesNotExist() {
            Optional<EducationView> result = personRepository.findEducationByPersonId(999);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("should persist person with all required fields")
        void shouldPersistPersonWithAllRequiredFields() {
            Person person = createTestPerson(1);

            Person saved = personRepository.save(person);

            assertThat(saved.getId()).isEqualTo(1);
            assertThat(personRepository.findById(1)).isPresent();
        }

        @Test
        @DisplayName("should update existing person")
        void shouldUpdateExistingPerson() {
            Person person = createTestPerson(1);
            personRepository.saveAndFlush(person);

            person.setFirstName("Petr");
            person.setEmail("petr.novak@example.com");
            Person updated = personRepository.saveAndFlush(person);

            assertThat(updated.getFirstName()).isEqualTo("Petr");
            assertThat(updated.getEmail()).isEqualTo("petr.novak@example.com");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete person when person exists")
        void shouldDeletePersonWhenPersonExists() {
            Person person = createTestPerson(1);
            personRepository.saveAndFlush(person);

            personRepository.delete(person);
            personRepository.flush();

            assertThat(personRepository.findById(1)).isEmpty();
        }
    }

    private Person createTestPerson(int id) {
        Person person = new Person();
        person.setId(id);
        person.setFirstName("Jan");
        person.setLastName("Novák");
        person.setBirthNumber("900101" + String.format("%04d", id));
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        person.setGender("M");
        person.setHealthInsuranceNotified("N");
        person.setOwner("SYSTEM");
        person.setDateOfInsert(LocalDate.now());
        person.setHasPermanentResidence("A");
        person.setCitizenshipCountryId(203);
        person.setDomicileCountryId(203);
        person.setHighSchoolCountryId(203);
        person.setTemporaryCountryId(203);
        person.setDomicileDistrictId(7777);
        person.setTemporaryDistrictId(7777);
        person.setDomicileZipCode("0");
        person.setTemporaryZipCode("0");
        person.setDomicileMunicipalityPartId(0L);
        person.setTemporaryMunicipalityPartId(0L);
        person.setDomicileMunicipalityId(0L);
        person.setTemporaryMunicipalityId(0L);
        person.setDormitoryApplication("N");
        person.setFromWhere("9");
        person.setTitlePrefix("Ing");
        person.setTitleSuffix("PhD");
        person.setEmail("jan.novak@example.com");
        person.setPhone("+420123456789");
        person.setMobile("+420987654321");
        return person;
    }
}
