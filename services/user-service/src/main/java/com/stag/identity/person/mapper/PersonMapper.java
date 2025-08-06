package com.stag.identity.person.mapper;

import com.stag.identity.dto.AddressesDTO;
import com.stag.identity.dto.BankAccountDetailsDTO;
import com.stag.identity.dto.BankAccountsDTO;
import com.stag.identity.dto.EducationDetailsDTO;
import com.stag.identity.dto.EducationDetailsForeignHighSchoolDTO;
import com.stag.identity.dto.EducationDetailsHighSchoolDTO;
import com.stag.identity.dto.EuroBankAccountDetailsDTO;
import com.stag.identity.dto.PersonProfileDTO;
import com.stag.identity.person.model.CodelistDomain;
import com.stag.identity.person.model.CodelistEntryId;
import com.stag.identity.person.model.PersonAddresses;
import com.stag.identity.person.model.PersonBanking;
import com.stag.identity.person.model.PersonBanking.BankAccount;
import com.stag.identity.person.model.PersonBanking.EuroBankAccount;
import com.stag.identity.person.model.PersonEducation;
import com.stag.identity.person.model.PersonEducation.ForeignHighSchool;
import com.stag.identity.person.model.PersonEducation.HighSchool;
import com.stag.identity.person.model.PersonProfile;
import com.stag.identity.person.model.PersonProfile.BirthPlace;
import com.stag.identity.person.model.PersonProfile.Citizenship;
import com.stag.identity.person.model.PersonProfile.Contact;
import com.stag.identity.person.model.PersonProfile.Titles;
import com.stag.identity.person.repository.projection.PersonAddressProjection;
import com.stag.identity.person.repository.projection.PersonBankProjection;
import com.stag.identity.person.repository.projection.PersonEducationProjection;
import com.stag.identity.person.repository.projection.PersonProfileProjection;
import com.stag.identity.person.service.data.PersonAddressData;
import com.stag.identity.person.service.data.PersonBankingData;
import com.stag.identity.person.service.data.PersonEducationData;
import com.stag.identity.person.service.data.PersonProfileData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.stag.identity.person.model.PersonAddresses.ForeignAddress;

@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class PersonMapper {

    // --- API Mapping Methods ---
    public abstract PersonProfileDTO toPersonProfileDTO(PersonProfile personProfile);
    public abstract AddressesDTO toAddressesDTO(PersonAddresses personAddresses);

    // --- Main Mapping Method ---
    public PersonProfile toPersonProfile(
        PersonProfileProjection projection,
        List<String> personalNumbers,
        PersonProfileData codelistData
    ) {
        Map<CodelistEntryId, String> meanings = codelistData.codelistMeanings();

        return PersonProfile.builder()
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
                            .birthPlace(toBirthPlace(projection, codelistData))
                            .citizenship(toCitizenship(projection, codelistData, meanings))
                            .build();
    }

    // --- Nested Object Builders ---
    abstract Contact toContact(PersonProfileProjection projection);

    @Mapping(source = "projection.birthPlace", target = "city")
    @Mapping(source = "codelistData.birthCountryName", target = "country")
    abstract BirthPlace toBirthPlace(PersonProfileProjection projection, PersonProfileData codelistData);

    private Titles toTitles(PersonProfileProjection projection, Map<CodelistEntryId, String> meanings) {
        return new Titles(
            lookupCodelistValue(CodelistDomain.TITUL_PRED, projection.titlePrefix(), meanings),
            lookupCodelistValue(CodelistDomain.TITUL_ZA, projection.titleSuffix(), meanings)
        );
    }

    private Citizenship toCitizenship(
        PersonProfileProjection projection,
        PersonProfileData codelistData,
        Map<CodelistEntryId, String> meanings
    ) {
        return new Citizenship(
            codelistData.citizenshipCountryName(),
            lookupCodelistValue(CodelistDomain.KVANT_OBCAN, projection.citizenshipQualification(), meanings)
        );
    }

    private String lookupCodelistValue(CodelistDomain domain, String lowValue, Map<CodelistEntryId, String> meanings) {
        return Optional.ofNullable(lowValue)
                       .map(value -> meanings.get(new CodelistEntryId(domain.name(), value)))
                       .orElse(null);
    }

    // --- Address Mapping Methods ---
    public PersonAddresses toPersonAddresses(PersonAddressProjection personAddress, PersonAddressData personAddressData) {
        return PersonAddresses.builder()
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

    private PersonAddresses.Address buildPersonAddress(
        String street,
        String streetNumber,
        String zipCode,
        String municipality,
        String municipalityPart,
        String district,
        String country
    ) {
        return PersonAddresses.Address.builder()
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

    public PersonBanking toPersonBanking(PersonBankProjection personBank, PersonBankingData personBankingData) {
        Map<CodelistEntryId, String> meanings = personBankingData.codelistMeanings();

        return PersonBanking.builder()
                            .account(toBankAccount(personBank, meanings))
                            .euroAccount(toEuroBankAccount(personBank, personBankingData, meanings))
                            .build();
    }

    private BankAccount toBankAccount(PersonBankProjection personBank, Map<CodelistEntryId, String> meanings) {
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
        PersonBankProjection personBank,
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

    public BankAccountsDTO toBankAccountsDTO(PersonBanking personBanking) {
        return BankAccountsDTO.builder()
                              .account(toBankAccountDTO(personBanking.account()))
                              .euroAccount(toEuroBankAccountDTO(personBanking.euroAccount()))
                              .build();
    }

    private EuroBankAccountDetailsDTO toEuroBankAccountDTO(EuroBankAccount euroBankAccount) {
        return EuroBankAccountDetailsDTO.builder()
                                        .holderName(euroBankAccount.owner())
                                        .holderAddress(euroBankAccount.address())
                                        .bankAccountNumberPrefix(euroBankAccount.prefix())
                                        .bankAccountNumberSuffix(euroBankAccount.suffix())
                                        .bankCode(euroBankAccount.bankCode())
                                        .bankName(euroBankAccount.bankName())
                                        .iban(euroBankAccount.iban())
                                        .currency(euroBankAccount.currency())
                                        .country(euroBankAccount.country())
                                        .swift(euroBankAccount.swift())
                                        .build();
    }

    private BankAccountDetailsDTO toBankAccountDTO(BankAccount account) {
        return BankAccountDetailsDTO.builder()
                                    .holderName(account.owner())
                                    .holderAddress(account.address())
                                    .bankAccountNumberPrefix(account.prefix())
                                    .bankAccountNumberSuffix(account.suffix())
                                    .bankCode(account.bankCode())
                                    .bankName(account.bankName())
                                    .iban(account.iban())
                                    .currency(account.currency())
                                    .build();
    }

    public PersonEducation toPersonEducation(PersonEducationProjection personEducation, PersonEducationData personEducationData) {
        return PersonEducation.builder()
                              .highSchool(toHighSchool(personEducation, personEducationData))
                              .foreignHighSchool(toForeignHighSchool(personEducation, personEducationData))
                              .build();
    }

    private HighSchool toHighSchool(PersonEducationProjection personEducation, PersonEducationData personEducationData) {
        return HighSchool.builder()
                         .name(personEducationData.highSchoolName())
                         .fieldOfStudy(personEducationData.highSchoolFieldOfStudy())
                         .graduationDate(personEducation.graduationDate())
                         .address(toAddressWithoutMunicipalityPart(personEducationData))
                         .build();
    }

    private ForeignHighSchool toForeignHighSchool(PersonEducationProjection personEducation, PersonEducationData personEducationData) {
        return ForeignHighSchool.builder()
                                .name(personEducation.highSchoolForeign())
                                .location(personEducation.highSchoolForeignPlace())
                                .fieldOfStudy(personEducationData.highSchoolFieldOfStudy())
                                .build();
    }

    private PersonAddresses.HighSchoolAddress toAddressWithoutMunicipalityPart(PersonEducationData personEducationData) {
        return PersonAddresses.HighSchoolAddress.builder()
                                                .street(personEducationData.highSchoolStreet())
                                                .streetNumber(personEducationData.highSchoolStreetNumber())
                                                .zipCode(personEducationData.highSchoolZipCode())
                                                .municipality(personEducationData.highSchoolMunicipalityName())
                                                .district(personEducationData.highSchoolDistrictName())
                                                .country(personEducationData.highSchoolCountryName())
                                                .build();
    }

    public EducationDetailsDTO toEducationDetailsDTO(PersonEducation personEducation) {
        return EducationDetailsDTO.builder()
                                  .highSchool(toEducationDetailsHighSchoolDTO(personEducation.highSchool()))
                                  .foreignHighSchool(toEducationDetailsForeignHighSchoolDTO(personEducation.foreignHighSchool()))
                                  .build();
    }

    @Mapping(source = "name", target = "institutionName")
    @Mapping(source = "fieldOfStudy", target = "fieldOfStudy")
    @Mapping(source = "graduationDate", target = "graduationDate")
    @Mapping(source = "address", target = "address")
    abstract EducationDetailsHighSchoolDTO toEducationDetailsHighSchoolDTO(HighSchool highSchool);

    @Mapping(source = "name", target = "institutionName")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "fieldOfStudy", target = "fieldOfStudy")
    abstract EducationDetailsForeignHighSchoolDTO toEducationDetailsForeignHighSchoolDTO(ForeignHighSchool foreignHighSchool);

}
