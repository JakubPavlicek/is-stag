package com.stag.identity.person.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
    name = "OSOBY",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "OSOB_PRIJMENI",
            columnList = "PRIJMENI"
        ),
        @Index(
            name = "OSOB_CISS_FK_I",
            columnList = "IZO"
        ),
        @Index(
            name = "OSOB_CSTA_OBCA_FK_I",
            columnList = "STATIDNO_OBCA"
        ),
        @Index(
            name = "OSOB_CSTA_BYDL_FK_I",
            columnList = "STATIDNO_BYDL"
        ),
        @Index(
            name = "OSOB_CSTA_NARO_FK_I",
            columnList = "STATIDNO_NARO"
        ),
        @Index(
            name = "OSOB_COKR_BYDL_FK_I",
            columnList = "OKRESIDNO_BYDL"
        ),
        @Index(
            name = "OSOB_COKR_PRBY_FK_I",
            columnList = "OKRESIDNO_PRBY"
        ),
        @Index(
            name = "OSOB_CPSC_BYDL_FK_I",
            columnList = "PSC_BYDL"
        ),
        @Index(
            name = "OSOB_CPSC_PRBY_FK_I",
            columnList = "PSC_PRBY"
        ),
        @Index(
            name = "OSOB_CCOB_BYDL_FK_I",
            columnList = "CCOBIDNO_BYDL"
        ),
        @Index(
            name = "OSOB_CCOB_PRBY_FK_I",
            columnList = "CCOBIDNO_PRBY"
        ),
        @Index(
            name = "OSOB_COBC_BYDL_FK_I",
            columnList = "OBECIDNO_BYDL"
        ),
        @Index(
            name = "OSOB_COBC_PRBY_FK_I",
            columnList = "OBECIDNO_PRBY"
        ),
        @Index(
            name = "OSOB_CSTA_FK_I",
            columnList = "STATIDNO_STRED_SKOLA"
        ),
        @Index(
            name = "OSOB_ZDR_SPEC_I",
            columnList = "ZDR_SPECIFIKA"
        ),
        @Index(
            name = "OSOB_CSTA_UCET_FK_I",
            columnList = "UCET_EURO_STATIDNO"
        ),
        @Index(
            name = "OSOB_CAM1_FK_I",
            columnList = "ADMIIDNO_BYDL"
        ),
        @Index(
            name = "OSOB_CAM2_FK_I",
            columnList = "ADMIIDNO_PRBY"
        ),
        @Index(
            name = "OSOB_ISZR_FK_I",
            columnList = "ISZRIDNO"
        )
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "OSOB_UK",
            columnNames = { "ROD_CISLO" }
        ),
        @UniqueConstraint(
            name = "OSOB3_UK",
            columnNames = { "EXTERNI_IDENTITA" }
        ),
        @UniqueConstraint(
            name = "SYS_C0098979",
            columnNames = { "ZDR_SPECIFIKAT" }
        )
    }
)
public class Person {

    @Id
    @Column(
        name = "OSOBIDNO",
        nullable = false
    )
    private Integer id;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "ZP_HLASEN",
        nullable = false,
        length = 1
    )
    private String healthInsuranceNotified;

    @Size(max = 2000)
    @Column(
        name = "POZNAMKA",
        length = 2000
    )
    private String note;

    @Size(max = 2000)
    @Column(
        name = "POZNAMKA2",
        length = 2000
    )
    private String note2;

    @Size(max = 48)
    @Column(
        name = "PR_ULICE",
        length = 48
    )
    private String temporaryStreet;

    @Size(max = 100)
    @NotNull
    @Column(
        name = "PRIJMENI",
        nullable = false,
        length = 100
    )
    private String lastName;

    @Size(max = 10)
    @NotNull
    @Column(
        name = "ROD_CISLO",
        nullable = false,
        length = 10
    )
    private String birthNumber;

    @Size(max = 10)
    @Column(
        name = "CISLO_ULICE",
        length = 10
    )
    private String streetNumber;

    @NotNull
    @Column(
        name = "DATUM_NAROZ",
        nullable = false
    )
    private LocalDate birthDate;

    @Size(max = 100)
    @Column(
        name = "JMENO",
        length = 100
    )
    private String firstName;

    @Size(max = 2)
    @ColumnDefault("NULL")
    @Column(
        name = "STAV",
        length = 2
    )
    private String maritalStatus;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "POHLAVI",
        nullable = false,
        length = 1
    )
    private String gender;

    @Size(max = 100)
    @Column(
        name = "RODNE_PRIJMENI",
        length = 100
    )
    private String birthName;

    @Size(max = 30)
    @NotNull
    @Column(
        name = "OWNER",
        nullable = false,
        length = 30
    )
    private String owner;

    @Size(max = 30)
    @Column(
        name = "UPDATOR",
        length = 30
    )
    private String updator;

    @NotNull
    @Column(
        name = "DATE_OF_INSERT",
        nullable = false
    )
    private LocalDate dateOfInsert;

    @Column(name = "DATE_OF_UPDATE")
    private LocalDate dateOfUpdate;

    @Size(max = 3)
    @Column(
        name = "TITUL_PRED",
        length = 3
    )
    private String titlePrefix;

    @Size(max = 3)
    @Column(
        name = "TITUL_ZA",
        length = 3
    )
    private String titleSuffix;

    @Size(max = 1)
    @ColumnDefault("NULL")
    @Column(
        name = "OBCANSTVI_KVALIFIKACE",
        length = 1
    )
    private String citizenshipQualification;

    @Column(name = "MATURITA_DATUM")
    private LocalDate graduationDate;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "TRVALY_POBYT",
        nullable = false,
        length = 1
    )
    private String hasPermanentResidence;

    @Size(max = 48)
    @Column(
        name = "ULICE",
        length = 48
    )
    private String street;

    @Size(max = 75)
    @Column(
        name = "OBEC_CIZI",
        length = 75
    )
    private String municipalityForeign;

    @Size(max = 75)
    @Column(
        name = "POSTA_CIZI",
        length = 75
    )
    private String postOfficeForeign;

    @Size(max = 75)
    @Column(
        name = "STRED_SKOLA_CIZI",
        length = 75
    )
    private String highSchoolForeign;

    @Size(max = 50)
    @Column(
        name = "OKRES_CIZI",
        length = 50
    )
    private String districtForeign;

    @Size(max = 10)
    @Column(
        name = "PSC_CIZI",
        length = 10
    )
    private String zipCodeForeign;

    @Size(max = 100)
    @Column(
        name = "EMAIL",
        length = 100
    )
    private String email;

    @Size(max = 20)
    @Column(
        name = "TELEFON",
        length = 20
    )
    private String phone;

    @Column(name = "IZO")
    private String highSchoolId;

    @NotNull
    @ColumnDefault("203")
    @Column(
        name = "STATIDNO_OBCA",
        nullable = false
    )
    private Integer residenceCountryId;

    @NotNull
    @ColumnDefault("203")
    @Column(
        name = "STATIDNO_BYDL",
        nullable = false
    )
    private Integer domicileCountryId;

    @ColumnDefault("NULL")
    @Column(name = "STATIDNO_NARO")
    private Integer birthCountryId;

    @NotNull
    @ColumnDefault("7777")
    @Column(
        name = "OKRESIDNO_BYDL",
        nullable = false
    )
    private Integer domicileDistrictId;

    @NotNull
    @ColumnDefault("7777")
    @Column(
        name = "OKRESIDNO_PRBY",
        nullable = false
    )
    private Integer temporaryDistrictId;

    @NotNull
    @ColumnDefault("'0'")
    @Column(
        name = "PSC_BYDL",
        nullable = false
    )
    private String domicileZipCode;

    @NotNull
    @ColumnDefault("'0'")
    @Column(
        name = "PSC_PRBY",
        nullable = false
    )
    private String temporaryZipCode;

    @Column(name = "PR_OSOBIDNO")
    private Long admissionPersonId;

    @Size(max = 11)
    @Column(
        name = "CISLO_PASU",
        length = 11
    )
    private String passportNumber;

    @Size(max = 10)
    @Column(
        name = "PR_CISLO_ULICE",
        length = 10
    )
    private String temporaryStreetNumber;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "CCOBIDNO_BYDL",
        nullable = false
    )
    private Long domicileMunicipalityPartId;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "CCOBIDNO_PRBY",
        nullable = false
    )
    private Long temporaryMunicipalityPartId;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "OBECIDNO_BYDL",
        nullable = false
    )
    private Long domicileMunicipalityId;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "OBECIDNO_PRBY",
        nullable = false
    )
    private Long temporaryMunicipalityId;

    @Size(max = 30)
    @Column(
        name = "MOBIL",
        length = 30
    )
    private String mobile;

    @Size(max = 255)
    @Column(name = "UCET_MAJITEL")
    private String accountOwner;

    @Size(max = 255)
    @Column(name = "UCET_ADRESA")
    private String accountAddress;

    @Size(max = 6)
    @Column(
        name = "UCET_PRED",
        length = 6
    )
    private String accountPrefix;

    @Size(max = 10)
    @Column(
        name = "UCET_ZA",
        length = 10
    )
    private String accountSuffix;

    @Size(max = 4)
    @Column(
        name = "UCET_BANKA",
        length = 4
    )
    private String accountBank;

    @Size(max = 30)
    @Column(
        name = "HESLO_PRO_VENEK",
        length = 30
    )
    private String externalPassword;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "ZADA_O_KOLEJ",
        nullable = false,
        length = 1
    )
    private String dormitoryApplication;

    @Column(name = "ZADA_O_KOLEJ_DATUM")
    private LocalDate dormitoryApplicationDate;

    @Size(max = 30)
    @Column(
        name = "UCET_IBAN",
        length = 30
    )
    private String accountIban;

    @Size(max = 3)
    @Column(
        name = "UCET_MENA",
        length = 3
    )
    private String accountCurrency;

    @Size(max = 75)
    @Column(
        name = "STRED_SKOLA_CIZI_MISTO",
        length = 75
    )
    private String highSchoolForeignPlace;

    @NotNull
    @ColumnDefault("203")
    @Column(
        name = "STATIDNO_STRED_SKOLA",
        nullable = false
    )
    private Integer highSchoolCountryId;

    @Size(max = 255)
    @Column(name = "STRED_SKOLA_CIZI_OBOR")
    private String highSchoolForeignFieldOfStudy;

    @Size(max = 75)
    @Column(
        name = "PR_OBEC_CIZI",
        length = 75
    )
    private String temporaryMunicipalityForeign;

    @Size(max = 50)
    @Column(
        name = "PR_OKRES_CIZI",
        length = 50
    )
    private String temporaryDistrictForeign;

    @Size(max = 75)
    @Column(
        name = "PR_POSTA_CIZI",
        length = 75
    )
    private String temporaryPostOfficeForeign;

    @NotNull
    @ColumnDefault("203")
    @Column(
        name = "STATIDNO_PRBY",
        nullable = false
    )
    private Integer temporaryCountryId;

    @Size(max = 255)
    @Column(name = "IDENTIFIKATOR")
    private String identifier;

    @Size(max = 2)
    @NotNull
    @ColumnDefault("'9'")
    @Column(
        name = "ODKUD",
        nullable = false,
        length = 2
    )
    private String fromWhere;

    @Size(max = 10)
    @Column(
        name = "CISLO_OBORU",
        length = 10
    )
    private String highSchoolFieldOfStudyNumber;

    @Size(max = 10)
    @Column(
        name = "PR_PSC_CIZI",
        length = 10
    )
    private String temporaryZipCodeForeign;

    @Size(max = 75)
    @ColumnDefault("NULL")
    @Column(
        name = "MISTO_NAR",
        length = 75
    )
    private String birthPlace;

    @Size(max = 10)
    @Column(
        name = "UCET_EURO_ZA",
        length = 10
    )
    private String euroAccountSuffix;

    @Size(max = 6)
    @Column(
        name = "UCET_EURO_PRED",
        length = 6
    )
    private String euroAccountPrefix;

    @Size(max = 4)
    @Column(
        name = "UCET_EURO_BANKA",
        length = 4
    )
    private String euroAccountBank;

    @Size(max = 3)
    @Column(
        name = "UCET_EURO_MENA",
        length = 3
    )
    private String euroAccountCurrency;

    @Size(max = 255)
    @Column(name = "UCET_EURO_ADRESA")
    private String euroAccountAddress;

    @Size(max = 255)
    @Column(name = "UCET_EURO_MAJITEL")
    private String euroAccountOwner;

    @Size(max = 30)
    @Column(
        name = "UCET_EURO_IBAN",
        length = 30
    )
    private String euroAccountIban;

    @Size(max = 1)
    @ColumnDefault("NULL")
    @Column(
        name = "ZDR_SPECIFIKA",
        length = 1
    )
    private String healthSpecifics;

    @Size(max = 12)
    @Column(
        name = "ADRESA_DATOVE_SCHRANKY",
        length = 12
    )
    private String dataBoxAddress;

    @Size(max = 255)
    @Column(name = "EXTERNI_IDENTITA")
    private String externalIdentity;

    @Size(max = 20)
    @Column(
        name = "ID_REGISTR_ZDR_PRAC",
        length = 20
    )
    private String healthPractitionerRegisterId;

    @Size(max = 4000)
    @Column(
        name = "ZDR_SPECIFIKA_ULEVY",
        length = 4000
    )
    private String healthSpecificsRelief;

    @Size(max = 4000)
    @Column(
        name = "ZDR_SPECIFIKA_DALSI_OPATRENI",
        length = 4000
    )
    private String healthSpecificsOtherMeasures;

    @ColumnDefault("203")
    @Column(name = "UCET_EURO_STATIDNO")
    private Integer euroAccountCountryId;

    @Size(max = 11)
    @Column(
        name = "UCET_EURO_SWIFT",
        length = 11
    )
    private String euroAccountSwift;

    @Size(max = 4000)
    @Column(
        name = "REPREZENTANT_ULEVY",
        length = 4000
    )
    private String representativeRelief;

    @Column(name = "DATUM_ANONYMIZACE_HNED")
    private LocalDate anonymizationDateImmediate;

    @Column(name = "DATUM_ANONYMIZACE_1_ROK")
    private LocalDate anonymizationDate1Year;

    @Column(name = "DATUM_ANONYMIZACE_65_LET")
    private LocalDate anonymizationDate65Years;

    @Size(max = 1)
    @Column(
        name = "STUPEN_PRED_VZDELANI",
        length = 1
    )
    private String previousEducationLevel;

    @Column(name = "ADMIIDNO_BYDL")
    private Long domicileAdminId;

    @Column(name = "ADMIIDNO_PRBY")
    private Long temporaryAdminId;

    @Column(name = "RODICOVSKA_Z_MPSV_OD")
    private LocalDate parentalLeaveFromMpsvFrom;

    @Column(name = "MATERSKA_Z_MPSV_OD")
    private LocalDate maternityLeaveFromMpsvFrom;

    @Column(name = "MATERSKA_Z_MPSV_DOU")
    private LocalDate maternityLeaveFromMpsvDou;

    @Column(name = "RODICOVSKA_Z_MPSV_DO")
    private LocalDate parentalLeaveFromMpsvTo;

    @Column(name = "RODICOVSKA_Z_MPSV_DOU")
    private LocalDate parentalLeaveFromMpsvDou;

    @Size(max = 1)
    @Column(
        name = "EVIDOVAN_NA_UP",
        length = 1
    )
    private String registeredAtLaborOffice;

    @Column(name = "CERPA_DAVKU_MPSV_DOU")
    private LocalDate drawingBenefitMpsvDou;

    @Column(name = "DATUM_POSLEDNI_AKTUALIZACE")
    private LocalDate lastUpdateDate;

    @Size(max = 1)
    @Column(
        name = "REPREZENTANT_Z_MSMT",
        length = 1
    )
    private String representativeFromMsmt;

    @Column(name = "REPREZENTANT_Z_MSMT_DOU")
    private LocalDate representativeFromMsmtDou;

    @Column(name = "MATERSKA_Z_MPSV_DO")
    private LocalDate maternityLeaveFromMpsvTo;

    @Column(name = "EVIDOVAN_NA_UP_DOU")
    private LocalDate registeredAtLaborOfficeDou;

    @Size(max = 1)
    @Column(
        name = "CERPA_DAVKU_MPSV",
        length = 1
    )
    private String drawingBenefitMpsv;

    @Size(max = 100)
    @Column(
        name = "ZAHRANICNI_ID",
        length = 100
    )
    private String foreignId;

    @Column(name = "ISZRIDNO")
    private Long iszrId;

    @Size(max = 255)
    @Column(name = "SIMS_VLASTNIK")
    private String simsOwner;

    @Size(max = 255)
    @Column(name = "SIMS_ID")
    private String simsId;

/*
 TODO [Reverse Engineering] create field to map the 'ZDR_SPECIFIKAT' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(
    name = "ZDR_SPECIFIKAT",
    columnDefinition = "ZDR_SPECIFIKAT"
    )
    private Object healthSpecifics;
*/
}