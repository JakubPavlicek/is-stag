package com.stag.identity.user.entity;

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
public class Osoby {

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
    private String zpHlasen;

    @Size(max = 2000)
    @Column(
        name = "POZNAMKA",
        length = 2000
    )
    private String poznamka;

    @Size(max = 2000)
    @Column(
        name = "POZNAMKA2",
        length = 2000
    )
    private String poznamka2;

    @Size(max = 48)
    @Column(
        name = "PR_ULICE",
        length = 48
    )
    private String prUlice;

    @Size(max = 100)
    @NotNull
    @Column(
        name = "PRIJMENI",
        nullable = false,
        length = 100
    )
    private String prijmeni;

    @Size(max = 10)
    @NotNull
    @Column(
        name = "ROD_CISLO",
        nullable = false,
        length = 10
    )
    private String rodCislo;

    @Size(max = 10)
    @Column(
        name = "CISLO_ULICE",
        length = 10
    )
    private String cisloUlice;

    @NotNull
    @Column(
        name = "DATUM_NAROZ",
        nullable = false
    )
    private LocalDate datumNaroz;

    @Size(max = 100)
    @Column(
        name = "JMENO",
        length = 100
    )
    private String jmeno;

    @Size(max = 2)
    @ColumnDefault("NULL")
    @Column(
        name = "STAV",
        length = 2
    )
    private String stav;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "POHLAVI",
        nullable = false,
        length = 1
    )
    private String pohlavi;

    @Size(max = 100)
    @Column(
        name = "RODNE_PRIJMENI",
        length = 100
    )
    private String rodnePrijmeni;

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
    private String titulPred;

    @Size(max = 3)
    @Column(
        name = "TITUL_ZA",
        length = 3
    )
    private String titulZa;

    @Size(max = 1)
    @ColumnDefault("NULL")
    @Column(
        name = "OBCANSTVI_KVALIFIKACE",
        length = 1
    )
    private String obcanstviKvalifikace;

    @Column(name = "MATURITA_DATUM")
    private LocalDate maturitaDatum;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "TRVALY_POBYT",
        nullable = false,
        length = 1
    )
    private String trvalyPobyt;

    @Size(max = 48)
    @Column(
        name = "ULICE",
        length = 48
    )
    private String ulice;

    @Size(max = 75)
    @Column(
        name = "OBEC_CIZI",
        length = 75
    )
    private String obecCizi;

    @Size(max = 75)
    @Column(
        name = "POSTA_CIZI",
        length = 75
    )
    private String postaCizi;

    @Size(max = 75)
    @Column(
        name = "STRED_SKOLA_CIZI",
        length = 75
    )
    private String stredSkolaCizi;

    @Size(max = 50)
    @Column(
        name = "OKRES_CIZI",
        length = 50
    )
    private String okresCizi;

    @Size(max = 10)
    @Column(
        name = "PSC_CIZI",
        length = 10
    )
    private String pscCizi;

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
    private String telefon;

    @Column(name = "IZO")
    private String izo;

    @NotNull
    @ColumnDefault("203")
    @Column(
        name = "STATIDNO_OBCA",
        nullable = false
    )
    private Integer statidnoObca;

    @NotNull
    @ColumnDefault("203")
    @Column(
        name = "STATIDNO_BYDL",
        nullable = false
    )
    private Integer statidnoBydl;

    @ColumnDefault("NULL")
    @Column(name = "STATIDNO_NARO")
    private Integer statidnoNaro;

    @NotNull
    @ColumnDefault("7777")
    @Column(
        name = "OKRESIDNO_BYDL",
        nullable = false
    )
    private Integer okresidnoBydl;

    @NotNull
    @ColumnDefault("7777")
    @Column(
        name = "OKRESIDNO_PRBY",
        nullable = false
    )
    private Integer okresidnoPrby;

    @NotNull
    @ColumnDefault("'0'")
    @Column(
        name = "PSC_BYDL",
        nullable = false
    )
    private String pscBydl;

    @NotNull
    @ColumnDefault("'0'")
    @Column(
        name = "PSC_PRBY",
        nullable = false
    )
    private String pscPrby;

    @Column(name = "PR_OSOBIDNO")
    private Long prOsobidno;

    @Size(max = 11)
    @Column(
        name = "CISLO_PASU",
        length = 11
    )
    private String cisloPasu;

    @Size(max = 10)
    @Column(
        name = "PR_CISLO_ULICE",
        length = 10
    )
    private String prCisloUlice;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "CCOBIDNO_BYDL",
        nullable = false
    )
    private Long ccobidnoBydl;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "CCOBIDNO_PRBY",
        nullable = false
    )
    private Long ccobidnoPrby;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "OBECIDNO_BYDL",
        nullable = false
    )
    private Long obecidnoBydl;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "OBECIDNO_PRBY",
        nullable = false
    )
    private Long obecidnoPrby;

    @Size(max = 30)
    @Column(
        name = "MOBIL",
        length = 30
    )
    private String mobil;

    @Size(max = 255)
    @Column(name = "UCET_MAJITEL")
    private String ucetMajitel;

    @Size(max = 255)
    @Column(name = "UCET_ADRESA")
    private String ucetAdresa;

    @Size(max = 6)
    @Column(
        name = "UCET_PRED",
        length = 6
    )
    private String ucetPred;

    @Size(max = 10)
    @Column(
        name = "UCET_ZA",
        length = 10
    )
    private String ucetZa;

    @Size(max = 4)
    @Column(
        name = "UCET_BANKA",
        length = 4
    )
    private String ucetBanka;

    @Size(max = 30)
    @Column(
        name = "HESLO_PRO_VENEK",
        length = 30
    )
    private String hesloProVenek;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "ZADA_O_KOLEJ",
        nullable = false,
        length = 1
    )
    private String zadaOKolej;

    @Column(name = "ZADA_O_KOLEJ_DATUM")
    private LocalDate zadaOKolejDatum;

    @Size(max = 30)
    @Column(
        name = "UCET_IBAN",
        length = 30
    )
    private String ucetIban;

    @Size(max = 3)
    @Column(
        name = "UCET_MENA",
        length = 3
    )
    private String ucetMena;

    @Size(max = 75)
    @Column(
        name = "STRED_SKOLA_CIZI_MISTO",
        length = 75
    )
    private String stredSkolaCiziMisto;

    @NotNull
    @ColumnDefault("203")
    @Column(
        name = "STATIDNO_STRED_SKOLA",
        nullable = false
    )
    private Integer statidnoStredSkola;

    @Size(max = 255)
    @Column(name = "STRED_SKOLA_CIZI_OBOR")
    private String stredSkolaCiziObor;

    @Size(max = 75)
    @Column(
        name = "PR_OBEC_CIZI",
        length = 75
    )
    private String prObecCizi;

    @Size(max = 50)
    @Column(
        name = "PR_OKRES_CIZI",
        length = 50
    )
    private String prOkresCizi;

    @Size(max = 75)
    @Column(
        name = "PR_POSTA_CIZI",
        length = 75
    )
    private String prPostaCizi;

    @NotNull
    @ColumnDefault("203")
    @Column(
        name = "STATIDNO_PRBY",
        nullable = false
    )
    private Integer statidnoPrby;

    @Size(max = 255)
    @Column(name = "IDENTIFIKATOR")
    private String identifikator;

    @Size(max = 2)
    @NotNull
    @ColumnDefault("'9'")
    @Column(
        name = "ODKUD",
        nullable = false,
        length = 2
    )
    private String odkud;

    @Size(max = 10)
    @Column(
        name = "CISLO_OBORU",
        length = 10
    )
    private String cisloOboru;

    @Size(max = 10)
    @Column(
        name = "PR_PSC_CIZI",
        length = 10
    )
    private String prPscCizi;

    @Size(max = 75)
    @ColumnDefault("NULL")
    @Column(
        name = "MISTO_NAR",
        length = 75
    )
    private String mistoNar;

    @Size(max = 10)
    @Column(
        name = "UCET_EURO_ZA",
        length = 10
    )
    private String ucetEuroZa;

    @Size(max = 6)
    @Column(
        name = "UCET_EURO_PRED",
        length = 6
    )
    private String ucetEuroPred;

    @Size(max = 4)
    @Column(
        name = "UCET_EURO_BANKA",
        length = 4
    )
    private String ucetEuroBanka;

    @Size(max = 3)
    @Column(
        name = "UCET_EURO_MENA",
        length = 3
    )
    private String ucetEuroMena;

    @Size(max = 255)
    @Column(name = "UCET_EURO_ADRESA")
    private String ucetEuroAdresa;

    @Size(max = 255)
    @Column(name = "UCET_EURO_MAJITEL")
    private String ucetEuroMajitel;

    @Size(max = 30)
    @Column(
        name = "UCET_EURO_IBAN",
        length = 30
    )
    private String ucetEuroIban;

    @Size(max = 1)
    @ColumnDefault("NULL")
    @Column(
        name = "ZDR_SPECIFIKA",
        length = 1
    )
    private String zdrSpecifika;

    @Size(max = 12)
    @Column(
        name = "ADRESA_DATOVE_SCHRANKY",
        length = 12
    )
    private String adresaDatoveSchranky;

    @Size(max = 255)
    @Column(name = "EXTERNI_IDENTITA")
    private String externiIdentita;

    @Size(max = 20)
    @Column(
        name = "ID_REGISTR_ZDR_PRAC",
        length = 20
    )
    private String idRegistrZdrPrac;

    @Size(max = 4000)
    @Column(
        name = "ZDR_SPECIFIKA_ULEVY",
        length = 4000
    )
    private String zdrSpecifikaUlevy;

    @Size(max = 4000)
    @Column(
        name = "ZDR_SPECIFIKA_DALSI_OPATRENI",
        length = 4000
    )
    private String zdrSpecifikaDalsiOpatreni;

    @ColumnDefault("203")
    @Column(name = "UCET_EURO_STATIDNO")
    private Integer ucetEuroStatidno;

    @Size(max = 11)
    @Column(
        name = "UCET_EURO_SWIFT",
        length = 11
    )
    private String ucetEuroSwift;

    @Size(max = 4000)
    @Column(
        name = "REPREZENTANT_ULEVY",
        length = 4000
    )
    private String reprezentantUlevy;

    @Column(name = "DATUM_ANONYMIZACE_HNED")
    private LocalDate datumAnonymizaceHned;

    @Column(name = "DATUM_ANONYMIZACE_1_ROK")
    private LocalDate datumAnonymizace1Rok;

    @Column(name = "DATUM_ANONYMIZACE_65_LET")
    private LocalDate datumAnonymizace65Let;

    @Size(max = 1)
    @Column(
        name = "STUPEN_PRED_VZDELANI",
        length = 1
    )
    private String stupenPredVzdelani;

    @Column(name = "ADMIIDNO_BYDL")
    private Long admiidnoBydl;

    @Column(name = "ADMIIDNO_PRBY")
    private Long admiidnoPrby;

    @Column(name = "RODICOVSKA_Z_MPSV_OD")
    private LocalDate rodicovskaZMpsvOd;

    @Column(name = "MATERSKA_Z_MPSV_OD")
    private LocalDate materskaZMpsvOd;

    @Column(name = "MATERSKA_Z_MPSV_DOU")
    private LocalDate materskaZMpsvDou;

    @Column(name = "RODICOVSKA_Z_MPSV_DO")
    private LocalDate rodicovskaZMpsvDo;

    @Column(name = "RODICOVSKA_Z_MPSV_DOU")
    private LocalDate rodicovskaZMpsvDou;

    @Size(max = 1)
    @Column(
        name = "EVIDOVAN_NA_UP",
        length = 1
    )
    private String evidovanNaUp;

    @Column(name = "CERPA_DAVKU_MPSV_DOU")
    private LocalDate cerpaDavkuMpsvDou;

    @Column(name = "DATUM_POSLEDNI_AKTUALIZACE")
    private LocalDate datumPosledniAktualizace;

    @Size(max = 1)
    @Column(
        name = "REPREZENTANT_Z_MSMT",
        length = 1
    )
    private String reprezentantZMsmt;

    @Column(name = "REPREZENTANT_Z_MSMT_DOU")
    private LocalDate reprezentantZMsmtDou;

    @Column(name = "MATERSKA_Z_MPSV_DO")
    private LocalDate materskaZMpsvDo;

    @Column(name = "EVIDOVAN_NA_UP_DOU")
    private LocalDate evidovanNaUpDou;

    @Size(max = 1)
    @Column(
        name = "CERPA_DAVKU_MPSV",
        length = 1
    )
    private String cerpaDavkuMpsv;

    @Size(max = 100)
    @Column(
        name = "ZAHRANICNI_ID",
        length = 100
    )
    private String zahranicniId;

    @Column(name = "ISZRIDNO")
    private Long iszridno;

    @Size(max = 255)
    @Column(name = "SIMS_VLASTNIK")
    private String simsVlastnik;
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
    private Object zdrSpecifikat;
*/
}