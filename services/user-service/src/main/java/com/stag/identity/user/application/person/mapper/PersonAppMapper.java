package com.stag.identity.user.application.person.mapper;

import com.stag.identity.user.application.person.dto.PersonAddressData;
import com.stag.identity.user.application.person.dto.PersonAddressesResult;
import com.stag.identity.user.application.person.dto.PersonAddressesResult.Address;
import com.stag.identity.user.application.person.dto.PersonAddressesResult.ForeignAddress;
import com.stag.identity.user.application.person.dto.PersonAddressesResult.HighSchoolAddress;
import com.stag.identity.user.application.person.dto.PersonBankingData;
import com.stag.identity.user.application.person.dto.PersonBankingResult;
import com.stag.identity.user.application.person.dto.PersonBankingResult.BankAccount;
import com.stag.identity.user.application.person.dto.PersonBankingResult.EuroBankAccount;
import com.stag.identity.user.application.person.dto.PersonEducationData;
import com.stag.identity.user.application.person.dto.PersonEducationResult;
import com.stag.identity.user.application.person.dto.PersonEducationResult.ForeignHighSchool;
import com.stag.identity.user.application.person.dto.PersonEducationResult.HighSchool;
import com.stag.identity.user.application.person.dto.PersonProfileData;
import com.stag.identity.user.application.person.dto.PersonProfileResult;
import com.stag.identity.user.application.person.dto.PersonProfileResult.BirthPlace;
import com.stag.identity.user.application.person.dto.PersonProfileResult.Citizenship;
import com.stag.identity.user.application.person.dto.PersonProfileResult.Contact;
import com.stag.identity.user.application.person.dto.PersonProfileResult.Titles;
import com.stag.identity.user.domain.person.model.PersonAddress;
import com.stag.identity.user.domain.person.model.PersonBank;
import com.stag.identity.user.domain.person.model.PersonEducation;
import com.stag.identity.user.domain.person.model.PersonProfile;
import com.stag.identity.user.domain.shared.model.CodelistDomain;
import com.stag.identity.user.domain.shared.model.CodelistEntryId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class PersonAppMapper {

    // --- Profile Mapping ---

    public PersonProfileResult toPersonProfile(
        PersonProfile projection,
        List<String> personalNumbers,
        PersonProfileData profileData
    ) {
        Map<CodelistEntryId, String> meanings = profileData.codelistMeanings();

        return PersonProfileResult.builder()
                                  .personId(projection.id())
                                  .firstName(projection.firstName())
                                  .lastName(projection.lastName())
                                  .birthSurname(projection.birthName())
                                  .birthNumber(projection.birthNumber())
                                  .birthDate(projection.birthDate())
                                  .passportNumber(projection.passportNumber())
                                  .personalNumbers(personalNumbers)
                                  .gender(lookupCodelistValue(CodelistDomain.POHLAVI, projection.gender(), meanings))
                                  .maritalStatus(lookupCodelistValue(CodelistDomain.STAV, projection.maritalStatus(), meanings))
                                  .contact(toContact(projection))
                                  .titles(toTitles(projection, meanings))
                                  .birthPlace(toBirthPlace(projection, profileData))
                                  .citizenship(toCitizenship(projection, profileData, meanings))
                                  .build();
    }

    abstract Contact toContact(PersonProfile projection);

    @Mapping(
        source = "personProfile.birthPlace",
        target = "city"
    )
    @Mapping(
        source = "profileData.birthCountryName",
        target = "country"
    )
    abstract BirthPlace toBirthPlace(PersonProfile personProfile, PersonProfileData profileData);

    private Titles toTitles(PersonProfile personProfile, Map<CodelistEntryId, String> meanings) {
        return new Titles(
            lookupCodelistValue(CodelistDomain.TITUL_PRED, personProfile.titlePrefix(), meanings),
            lookupCodelistValue(CodelistDomain.TITUL_ZA, personProfile.titleSuffix(), meanings)
        );
    }

    private Citizenship toCitizenship(
        PersonProfile personProfile,
        PersonProfileData profileData,
        Map<CodelistEntryId, String> meanings
    ) {
        return new Citizenship(
            profileData.citizenshipCountryName(),
            lookupCodelistValue(CodelistDomain.KVANT_OBCAN, personProfile.citizenshipQualification(), meanings)
        );
    }

    private String lookupCodelistValue(CodelistDomain domain, String lowValue, Map<CodelistEntryId, String> meanings) {
        return Optional.ofNullable(lowValue)
                       .map(value -> meanings.get(new CodelistEntryId(domain.name(), value)))
                       .orElse(null);
    }

    // --- Address Mapping ---

    public PersonAddressesResult toPersonAddresses(PersonAddress personAddress, PersonAddressData personAddressData) {
        return PersonAddressesResult.builder()
                                    .permanentAddress(buildPersonAddress(
                                        personAddress.permanentStreet(),
                                        personAddress.permanentStreetNumber(),
                                        personAddress.permanentZipCode(),
                                        personAddressData.permanentMunicipality(),
                                        personAddressData.permanentMunicipalityPart(),
                                        personAddressData.permanentDistrict(),
                                        personAddressData.permanentCountry()
                                    ))
                                    .temporaryAddress(buildPersonAddress(
                                        personAddress.temporaryStreet(),
                                        personAddress.temporaryStreetNumber(),
                                        personAddress.temporaryZipCode(),
                                        personAddressData.temporaryMunicipality(),
                                        personAddressData.temporaryMunicipalityPart(),
                                        personAddressData.temporaryDistrict(),
                                        personAddressData.temporaryCountry()
                                    ))
                                    .foreignPermanentAddress(buildPersonForeignAddress(
                                        personAddress.foreignPermanentZipCode(),
                                        personAddress.foreignPermanentMunicipality(),
                                        personAddress.foreignPermanentDistrict(),
                                        personAddress.foreignPermanentPostOffice()
                                    ))
                                    .foreignTemporaryAddress(buildPersonForeignAddress(
                                        personAddress.foreignTemporaryZipCode(),
                                        personAddress.foreignTemporaryMunicipality(),
                                        personAddress.foreignTemporaryDistrict(),
                                        personAddress.foreignTemporaryPostOffice()
                                    ))
                                    .build();
    }

    private Address buildPersonAddress(
        String street,
        String streetNumber,
        String zipCode,
        String municipality,
        String municipalityPart,
        String district,
        String country
    ) {
        return Address.builder()
                      .street(street)
                      .streetNumber(streetNumber)
                      .zipCode(zipCode)
                      .municipality(municipality)
                      .municipalityPart(municipalityPart)
                      .district(district)
                      .country(country)
                      .build();
    }

    private ForeignAddress buildPersonForeignAddress(
        String zipCode,
        String municipality,
        String district,
        String postOffice
    ) {
        return ForeignAddress.builder()
                             .zipCode(zipCode)
                             .municipality(municipality)
                             .district(district)
                             .postOffice(postOffice)
                             .build();
    }

    // --- Banking Mapping ---

    public PersonBankingResult toPersonBanking(PersonBank personBank, PersonBankingData personBankingData) {
        Map<CodelistEntryId, String> meanings = personBankingData.codelistMeanings();

        return PersonBankingResult.builder()
                                  .account(toBankAccount(personBank, meanings))
                                  .euroAccount(toEuroBankAccount(personBank, personBankingData, meanings))
                                  .build();
    }

    private BankAccount toBankAccount(PersonBank personBank, Map<CodelistEntryId, String> meanings) {
        return BankAccount.builder()
                          .owner(personBank.accountOwner())
                          .address(personBank.accountAddress())
                          .prefix(personBank.accountPrefix())
                          .suffix(personBank.accountSuffix())
                          .bankCode(personBank.accountBank())
                          .bankName(lookupCodelistValue(CodelistDomain.CIS_BANK, personBank.accountBank(), meanings))
                          .iban(personBank.accountIban())
                          .currency(personBank.accountCurrency())
                          .build();
    }

    private EuroBankAccount toEuroBankAccount(
        PersonBank personBank,
        PersonBankingData personBankingData,
        Map<CodelistEntryId, String> meanings
    ) {
        return EuroBankAccount.builder()
                              .owner(personBank.euroAccountOwner())
                              .address(personBank.euroAccountAddress())
                              .prefix(personBank.euroAccountPrefix())
                              .suffix(personBank.euroAccountSuffix())
                              .bankCode(personBank.euroAccountBank())
                              .bankName(lookupCodelistValue(CodelistDomain.CIS_BANK_EURO, personBank.euroAccountBank(), meanings))
                              .iban(personBank.euroAccountIban())
                              .currency(personBank.euroAccountCurrency())
                              .country(personBankingData.euroAccountCountryName())
                              .swift(personBank.euroAccountSwift())
                              .build();
    }

    // --- Education Mapping ---

    public PersonEducationResult toPersonEducation(PersonEducation personEducation, PersonEducationData personEducationData) {
        return PersonEducationResult.builder()
                                    .highSchool(toHighSchool(personEducation, personEducationData))
                                    .foreignHighSchool(toForeignHighSchool(personEducation, personEducationData))
                                    .build();
    }

    private HighSchool toHighSchool(PersonEducation personEducation, PersonEducationData personEducationData) {
        return HighSchool.builder()
                         .name(personEducationData.highSchoolName())
                         .fieldOfStudy(personEducationData.highSchoolFieldOfStudy())
                         .graduationDate(personEducation.graduationDate())
                         .address(toAddressWithoutMunicipalityPart(personEducationData))
                         .build();
    }

    private ForeignHighSchool toForeignHighSchool(PersonEducation personEducation, PersonEducationData personEducationData) {
        return ForeignHighSchool.builder()
                                .name(personEducation.highSchoolForeign())
                                .location(personEducation.highSchoolForeignPlace())
                                .fieldOfStudy(personEducationData.highSchoolFieldOfStudy())
                                .build();
    }

    private HighSchoolAddress toAddressWithoutMunicipalityPart(PersonEducationData personEducationData) {
        return HighSchoolAddress.builder()
                                .street(personEducationData.highSchoolStreet())
                                .streetNumber(personEducationData.highSchoolStreetNumber())
                                .zipCode(personEducationData.highSchoolZipCode())
                                .municipality(personEducationData.highSchoolMunicipalityName())
                                .district(personEducationData.highSchoolDistrictName())
                                .country(personEducationData.highSchoolCountryName())
                                .build();
    }

}
