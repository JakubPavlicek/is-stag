ALTER SESSION SET CONTAINER=FREEPDB1;
ALTER USER INSTALL2 QUOTA UNLIMITED ON USERS;

create table INSTALL2.OSOBY
(
    OSOBIDNO                     NUMBER(8)                      not null
        constraint OSOB_PK
            primary key,
    ZP_HLASEN                    VARCHAR2(1)       default 'N'  not null,
    POZNAMKA                     VARCHAR2(2000),
    POZNAMKA2                    VARCHAR2(2000),
    PR_ULICE                     VARCHAR2(48),
    PRIJMENI                     VARCHAR2(100 char)             not null,
    ROD_CISLO                    VARCHAR2(10)                   not null
        constraint OSOB_UK
            unique,
    CISLO_ULICE                  VARCHAR2(10),
    DATUM_NAROZ                  DATE                           not null,
    JMENO                        VARCHAR2(100 char),
    STAV                         VARCHAR2(2 char)  default NULL,
    POHLAVI                      VARCHAR2(1)                    not null,
    RODNE_PRIJMENI               VARCHAR2(100 char),
    OWNER                        VARCHAR2(30)                   not null,
    UPDATOR                      VARCHAR2(30),
    DATE_OF_INSERT               DATE                           not null,
    DATE_OF_UPDATE               DATE,
    TITUL_PRED                   VARCHAR2(3),
    TITUL_ZA                     VARCHAR2(3 char),
    OBCANSTVI_KVALIFIKACE        VARCHAR2(1 char)  default NULL,
    MATURITA_DATUM               DATE,
    TRVALY_POBYT                 VARCHAR2(1)       default 'A'  not null,
    ULICE                        VARCHAR2(48),
    OBEC_CIZI                    VARCHAR2(75),
    POSTA_CIZI                   VARCHAR2(75),
    STRED_SKOLA_CIZI             VARCHAR2(75),
    OKRES_CIZI                   VARCHAR2(50),
    PSC_CIZI                     VARCHAR2(10),
    EMAIL                        VARCHAR2(100),
    TELEFON                      VARCHAR2(20),
    IZO                          VARCHAR2(10),
    STATIDNO_OBCA                NUMBER(8)         default 203  not null,
    STATIDNO_BYDL                NUMBER(8)         default 203  not null,
    STATIDNO_NARO                NUMBER(8)         default NULL,
    OKRESIDNO_BYDL               NUMBER(8)         default 7777 not null,
    OKRESIDNO_PRBY               NUMBER(8)         default 7777 not null,
    PSC_BYDL                     VARCHAR2(5)       default '0'  not null,
    PSC_PRBY                     VARCHAR2(5)       default '0'  not null,
    PR_OSOBIDNO                  NUMBER,
    CISLO_PASU                   VARCHAR2(11),
    PR_CISLO_ULICE               VARCHAR2(10),
    CCOBIDNO_BYDL                NUMBER(10)        default 0    not null,
    CCOBIDNO_PRBY                NUMBER(10)        default 0    not null,
    OBECIDNO_BYDL                NUMBER(10)        default 0    not null,
    OBECIDNO_PRBY                NUMBER(10)        default 0    not null,
    MOBIL                        VARCHAR2(30),
    UCET_MAJITEL                 VARCHAR2(255),
    UCET_ADRESA                  VARCHAR2(255),
    UCET_PRED                    VARCHAR2(6),
    UCET_ZA                      VARCHAR2(10),
    UCET_BANKA                   VARCHAR2(4),
    HESLO_PRO_VENEK              VARCHAR2(30),
    ZADA_O_KOLEJ                 VARCHAR2(1)       default 'N'  not null,
    ZADA_O_KOLEJ_DATUM           DATE,
    UCET_IBAN                    VARCHAR2(30 char)
        constraint OSOB_UCET_IBAN
            check (ucet_iban is null or length(ucet_iban) > 12),
    UCET_MENA                    VARCHAR2(3),
    STRED_SKOLA_CIZI_MISTO       VARCHAR2(75),
    STATIDNO_STRED_SKOLA         NUMBER(10)        default 203  not null,
    STRED_SKOLA_CIZI_OBOR        VARCHAR2(255),
    PR_OBEC_CIZI                 VARCHAR2(75),
    PR_OKRES_CIZI                VARCHAR2(50),
    PR_POSTA_CIZI                VARCHAR2(75),
    STATIDNO_PRBY                NUMBER(8)         default 203  not null,
    IDENTIFIKATOR                VARCHAR2(255),
    ODKUD                        VARCHAR2(2)       default '9'  not null,
    CISLO_OBORU                  VARCHAR2(10),
    PR_PSC_CIZI                  VARCHAR2(10),
    MISTO_NAR                    VARCHAR2(75 char) default NULL,
    UCET_EURO_ZA                 VARCHAR2(10),
    UCET_EURO_PRED               VARCHAR2(6),
    UCET_EURO_BANKA              VARCHAR2(4),
    UCET_EURO_MENA               VARCHAR2(3),
    UCET_EURO_ADRESA             VARCHAR2(255),
    UCET_EURO_MAJITEL            VARCHAR2(255),
    UCET_EURO_IBAN               VARCHAR2(30 char)
        constraint OSOB_UCET_EURO_IBAN
            check (ucet_euro_iban is null or length(ucet_euro_iban) > 12),
    ZDR_SPECIFIKA                VARCHAR2(1 char)  default NULL,
    ADRESA_DATOVE_SCHRANKY       VARCHAR2(12),
    EXTERNI_IDENTITA             VARCHAR2(255)
        constraint OSOB3_UK
            unique,
    ID_REGISTR_ZDR_PRAC          VARCHAR2(20),
    ZDR_SPECIFIKA_ULEVY          VARCHAR2(4000),
    ZDR_SPECIFIKA_DALSI_OPATRENI VARCHAR2(4000),
    UCET_EURO_STATIDNO           NUMBER(8)         default 203,
    UCET_EURO_SWIFT              VARCHAR2(11 char),
    REPREZENTANT_ULEVY           VARCHAR2(4000 char),
    DATUM_ANONYMIZACE_HNED       DATE,
    DATUM_ANONYMIZACE_1_ROK      DATE,
    DATUM_ANONYMIZACE_65_LET     DATE,
    STUPEN_PRED_VZDELANI         VARCHAR2(1 char),
    ADMIIDNO_BYDL                NUMBER(10),
    ADMIIDNO_PRBY                NUMBER(10),
    RODICOVSKA_Z_MPSV_OD         DATE,
    MATERSKA_Z_MPSV_OD           DATE,
    MATERSKA_Z_MPSV_DOU          DATE,
    RODICOVSKA_Z_MPSV_DO         DATE,
    RODICOVSKA_Z_MPSV_DOU        DATE,
    EVIDOVAN_NA_UP               VARCHAR2(1 char),
    CERPA_DAVKU_MPSV_DOU         DATE,
    DATUM_POSLEDNI_AKTUALIZACE   DATE,
    REPREZENTANT_Z_MSMT          VARCHAR2(1 char),
    REPREZENTANT_Z_MSMT_DOU      DATE,
    MATERSKA_Z_MPSV_DO           DATE,
    EVIDOVAN_NA_UP_DOU           DATE,
    CERPA_DAVKU_MPSV             VARCHAR2(1 char),
    ZAHRANICNI_ID                VARCHAR2(100 char),
    ISZRIDNO                     NUMBER(10),
    ZDR_SPECIFIKAT               VARCHAR2(255 char)
        unique,
    SIMS_VLASTNIK                VARCHAR2(255 char),
    SIMS_ID                      VARCHAR2(255 char),
    constraint JMENO_CK
        check (statidno_obca <> 203 or jmeno is not null),
    constraint MATURITA_CK
        check (maturita_datum > add_months(datum_naroz, 180)),
    constraint OBCANSTVI_CK
        check (obcanstvi_kvalifikace in (0, 9)
            or
               (obcanstvi_kvalifikace = 1 and statidno_obca is not null and statidno_obca <> 999)
            or
               (obcanstvi_kvalifikace = 2 and statidno_obca is not null and statidno_obca <> 203)),
    constraint OBCA_CK
        check ((obcanstvi_kvalifikace in (0, 9) and statidno_obca = 999)
            or
               (obcanstvi_kvalifikace = 1 and statidno_obca is not null and statidno_obca <> 999)
            or
               (obcanstvi_kvalifikace = 2 and statidno_obca is not null and statidno_obca <> 203)),
    constraint UCET_CK
        check ((UCET_IBAN IS NULL AND UCET_PRED IS NULL AND UCET_ZA IS NULL AND UCET_BANKA IS NULL)
            OR (UCET_IBAN IS NULL AND UCET_PRED IS NULL AND UCET_ZA IS NOT NULL AND UCET_BANKA IS NOT NULL)
            OR (UCET_IBAN IS NULL AND UCET_PRED IS NOT NULL AND UCET_ZA IS NOT NULL AND UCET_BANKA IS NOT NULL)
            OR (UCET_IBAN IS NOT NULL AND UCET_PRED IS NOT NULL AND UCET_ZA IS NOT NULL AND UCET_BANKA IS NOT NULL)
            OR (UCET_IBAN IS NOT NULL AND UCET_PRED IS NULL AND UCET_ZA IS NOT NULL AND UCET_BANKA IS NOT NULL)),
    constraint UCET_EURO_CK
        check ((UCET_EURO_IBAN IS NULL AND UCET_EURO_PRED IS NULL AND
                UCET_EURO_ZA IS NULL AND UCET_EURO_BANKA IS NULL)
            OR (UCET_EURO_IBAN IS NULL AND UCET_EURO_PRED IS NULL AND
                UCET_EURO_ZA IS NOT NULL AND UCET_EURO_BANKA IS NOT NULL)
            OR (UCET_EURO_IBAN IS NULL AND UCET_EURO_PRED IS NOT NULL AND
                UCET_EURO_ZA IS NOT NULL AND UCET_EURO_BANKA IS NOT NULL)
            OR (UCET_EURO_IBAN IS NOT NULL AND UCET_EURO_PRED IS NOT NULL AND
                UCET_EURO_ZA IS NOT NULL AND UCET_EURO_BANKA IS NOT NULL)
            OR (UCET_EURO_IBAN IS NOT NULL AND UCET_EURO_PRED IS NULL AND
                UCET_EURO_ZA IS NOT NULL AND UCET_EURO_BANKA IS NOT NULL))
);