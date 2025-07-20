package com.stag.academics.student.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "STUDENTI",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "STUD_PRED_OS_CISLO_I",
            columnList = "PRED_OS_CISLO",
            unique = true
        ),
        @Index(
            name = "STUD_DAT_UKONCENI_I",
            columnList = "DAT_UKONCENI"
        ),
        @Index(
            name = "STUD_STAV",
            columnList = "STAV"
        ),
        @Index(
            name = "STUD_OSOB_FK_I",
            columnList = "OSOBIDNO"
        ),
        @Index(
            name = "STUD_STPR_FK_I",
            columnList = "STPRIDNO"
        ),
        @Index(
            name = "STUD_UCIT_FK_I",
            columnList = "UCITIDNO"
        ),
        @Index(
            name = "STUD_PRAC_FK_I",
            columnList = "PRAC_ZKR"
        ),
        @Index(
            name = "STUD_CISK_FK_I",
            columnList = "SKOLAIDNO"
        ),
        @Index(
            name = "STUD_PRAC_FK2_I",
            columnList = "DOKT_DRUHE_PRAC"
        ),
        @Index(
            name = "STUD_CISLO_SPISU",
            columnList = "CISLO_SPISU"
        ),
        @Index(
            name = "STUD_CSTA_FK_I",
            columnList = "STATIDNO_DOMOVSKA_SKOLA"
        ),
        @Index(
            name = "STUD_UCIT2_FK_I",
            columnList = "UCITIDNO_SPECIAL"
        ),
        @Index(
            name = "STUD_CCOB_FK_I",
            columnList = "PLATCE_CCOBIDNO"
        ),
        @Index(
            name = "STUD_COBC_FK_I",
            columnList = "PLATCE_OBECIDNO"
        ),
        @Index(
            name = "STUD_STUDIUM_PRED_AKRED",
            columnList = "STUDIUM_PRED_AKRED"
        ),
        @Index(
            name = "STUD_CAM1_FK_I",
            columnList = "PLATCE_ADMIIDNO"
        ),
        @Index(
            name = "STUD_CAM2_FK_I",
            columnList = "ADMIIDNO_PRBY"
        ),
        @Index(
            name = "STUD_STUD_OSCI_FK_I",
            columnList = "OS_CISLO_PRESTUP"
        ),
        @Index(
            name = "STUD_OBRA_FK_I",
            columnList = "OBRAIDNO"
        )
    }
)
public class Student {

    @Id
    @Size(max = 10)
    @Column(
        name = "OS_CISLO",
        nullable = false,
        length = 10
    )
    private String personalNumber;

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

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "ABSOLVENT",
        nullable = false,
        length = 1
    )
    private String graduate;

    @Column(name = "DAT_NASTUPU")
    private LocalDate startDate;

    @Column(name = "DAT_UKONCENI")
    private LocalDate endDate;

    @Column(name = "DAT_PREDP_UKONCENI")
    private LocalDate expectedEndDate;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "NOVE_PRIJATY",
        nullable = false,
        length = 1
    )
    private String newlyAdmitted;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "VYKAZOVAN",
        nullable = false,
        length = 1
    )
    private String reported;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "STAV",
        nullable = false,
        length = 1
    )
    private String status;

    @NotNull
    @Column(
        name = "OSOBIDNO",
        nullable = false
    )
    private Integer personId;

    @NotNull
    @Column(
        name = "STPRIDNO",
        nullable = false
    )
    private Long studyProgramId;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "DOBA_PRED_STUD",
        nullable = false,
        precision = 3,
        scale = 1
    )
    private BigDecimal previousStudyDuration;

    @Size(max = 4000)
    @Column(
        name = "POZNAMKA",
        length = 4000
    )
    private String note;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PRED_OS_CISLO")
    private Student previousPersonalNumber;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "VYKAZOVAT_ZP",
        nullable = false,
        length = 1
    )
    private String reportToHealthInsurance;

    @Column(name = "LAST_LOGIN_DATE")
    private LocalDate lastLoginDate;

    @Size(max = 100)
    @Column(
        name = "LAST_LOGIN_ADR",
        length = 100
    )
    private String lastLoginAdr;

    @Column(name = "DOKT_STPL_SESTAVEN")
    private LocalDate doctoralPlanAssembledDate;

    @Column(name = "DOKT_STPL_SCHVALEN")
    private LocalDate doctoralPlanApprovedDate;

    @Size(max = 255)
    @Column(name = "DOKT_STPL_NAZEV")
    private String doctoralPlanName;

    @Size(max = 1)
    @Column(
        name = "UCET_ZASILAT_STIPENDIUM",
        length = 1
    )
    private String sendScholarshipToAccount;

    @Size(max = 255)
    @Column(name = "VS_SKOLA")
    private String universitySchool;

    @Size(max = 255)
    @Column(name = "VS_MISTO")
    private String universityPlace;

    @Size(max = 255)
    @Column(name = "VS_FAKULTA")
    private String universityFaculty;

    @Size(max = 255)
    @Column(name = "VS_OBOR")
    private String universityFieldOfStudy;

    @Size(max = 255)
    @Column(name = "VS_STUD_PROG")
    private String universityStudyProgram;

    @Size(max = 4)
    @Column(
        name = "VS_ROK_ABSOL",
        length = 4
    )
    private String universityGraduationYear;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "PLATIT_PREKROCENI_DOBY",
        nullable = false,
        length = 1
    )
    private String payForExceedingStudyTime;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "PLATIT_DALSI_STUDIUM",
        nullable = false,
        length = 1
    )
    private String payForFurtherStudy;

    @Column(name = "UCITIDNO")
    private Long teacherId;

    @Column(name = "PRAC_ZKR")
    private String departmentAbbreviation;

    @Size(max = 4000)
    @Column(
        name = "CZ_INF_DS",
        length = 4000
    )
    private String infoDsCzech;

    @Size(max = 4000)
    @Column(
        name = "AN_INF_DS",
        length = 4000
    )
    private String infoDsEnglish;

    @Column(name = "SKOLAIDNO")
    private Long schoolId;

    @Size(max = 255)
    @Column(name = "VS_KVALIF_PRACE")
    private String universityThesis;

    @Column(name = "DOKT_DRUHE_PRAC")
    private String doctoralSecondDepartment;

    @Size(max = 255)
    @Column(name = "IDENTIFIKATOR")
    private String identifier;

    @Size(max = 1)
    @ColumnDefault("'N'")
    @Column(
        name = "INDEX_ODEVZDAN",
        length = 1
    )
    private String indexSubmitted;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'K'")
    @Column(
        name = "STUPEN_PRED_VZDELANI",
        nullable = false,
        length = 1
    )
    private String previousEducationLevel;

    @Size(max = 100)
    @Column(
        name = "CISLO_SPISU",
        length = 100
    )
    private String fileNumber;

    @Column(name = "DATUM_STANOVENO_58_3")
    private LocalDate dateEstablished583;

    @Column(name = "DATUM_ZACATKU_58_3")
    private LocalDate startDate583;

    @Column(name = "DATUM_KONCE_58_3")
    private LocalDate endDate583;

    @Size(max = 30)
    @Column(
        name = "STUDIJNI_REFERENTKA",
        length = 30
    )
    private String studyAdvisor;

    @Column(name = "POCET_ZAPISOVYCH_PROPUSTEK")
    private Short enrollmentExemptionsCount;

    @Column(
        name = "MAX_DELKA_STUDIA",
        precision = 5,
        scale = 3
    )
    private BigDecimal maxStudyDuration;

    @Column(
        name = "MAX_DELKA_PRERUS",
        precision = 5,
        scale = 3
    )
    private BigDecimal maxInterruptionDuration;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "SPIS_JE_UZAVRENY",
        nullable = false,
        length = 1
    )
    private String fileIsClosed;

    @Column(name = "DATUM_SPLNENI_STUD_POVINNOSTI")
    private LocalDate studyObligationsMetDate;

    @Column(name = "STATIDNO_DOMOVSKA_SKOLA")
    private Integer homeSchoolCountryId;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "STAV_AKTUALNI",
        nullable = false,
        length = 1
    )
    private String currentStatus;

    @Size(max = 255)
    @Column(name = "CISLO_JEDNACI")
    private String referenceNumber;

    @Column(name = "NEVYHOVUJE_ZDR_ZPUSOBILOSTI_OD")
    private LocalDate unfitForHealthReasonsFrom;

    @Size(max = 4000)
    @Column(
        name = "ZNEPLATNENI_DUVOD",
        length = 4000
    )
    private String invalidationReason;

    @Column(name = "ZNEPLATNENI_DATUM")
    private LocalDate invalidationDate;

    @Size(max = 4000)
    @Column(
        name = "NEVYHOVUJE_ZDR_ZPUSOBILOSTI",
        length = 4000
    )
    private String unfitForHealthReasons;

    @Column(name = "UCITIDNO_SPECIAL")
    private Long specialTeacherId;

    @Size(max = 100)
    @Column(
        name = "EMAIL",
        length = 100
    )
    private String email;

    @Column(name = "DAT_NASTUPU_DOU")
    private LocalDate startDateDou;

    @Column(name = "DAT_UKONCENI_DOU")
    private LocalDate endDateDou;

    @Size(max = 100)
    @ColumnDefault("NULL")
    @Column(
        name = "NAHLASEN_NA_REGISTR_ZDR_PRAC",
        length = 100
    )
    private String reportedToHealthRegister;

    @Column(name = "PLATCE_CCOBIDNO")
    private Long payerMunicipalityPartId;

    @Column(name = "PLATCE_OBECIDNO")
    private Long payerMunicipalityId;

    @Size(max = 100)
    @Column(
        name = "PLATCE_ULICE_CISLO",
        length = 100
    )
    private String payerStreetAndNumber;

    @Size(max = 100)
    @Column(
        name = "PLATCE_NAZEV",
        length = 100
    )
    private String payerName;

    @Size(max = 8)
    @Column(
        name = "PLATCE_ICO",
        length = 8
    )
    private String payerIco;

    @Size(max = 12)
    @Column(
        name = "PLATCE_DIC",
        length = 12
    )
    private String payerDic;

    @Size(max = 5)
    @Column(
        name = "PLATCE_PSC",
        length = 5
    )
    private String payerZipCode;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "PODMINENY_ZAPIS",
        nullable = false,
        length = 1
    )
    private String conditionalEnrollment;

    @Size(max = 10)
    @Column(
        name = "STUDIUM_PRED_AKRED",
        length = 10
    )
    private String studyBeforeAccreditation;

    @Column(
        name = "MAX_DELKA_STUDIA_VYPOCET",
        precision = 5,
        scale = 3
    )
    private BigDecimal maxStudyDurationCalculation;

    @Column(name = "POTVRZENI_TISK_DATUM")
    private LocalDate confirmationPrintDate;

    @Size(max = 100)
    @Column(
        name = "PLATCE_ULICE",
        length = 100
    )
    private String payerStreet;

    @Size(max = 100)
    @Column(
        name = "GDPR_ST_ST_VLASTNI_NASTAVENI",
        length = 100
    )
    private String gdprCustomSettings;

    @Column(name = "PLATCE_ADMIIDNO")
    private Long payerAddressId;

    @Column(name = "ADMIIDNO_PRBY")
    private Long temporaryAddressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OS_CISLO_PRESTUP")
    private Student transferPersonalNumber;

    @Column(name = "MC_DOBA_PLATNOSTI_DO")
    private LocalDate mcValidityDateTo;

    @Column(name = "MC_SOUBOR")
    private byte[] mcFile;

    @Column(name = "MC_DOBA_PLATNOSTI_OD")
    private LocalDate mcValidityDateFrom;

    @Size(max = 1)
    @Column(
        name = "MC_VYSTAVIT",
        length = 1
    )
    private String mcIssue;

    @Size(max = 100)
    @Column(
        name = "MC_IDENTIFIKATOR",
        length = 100
    )
    private String mcIdentifier;

    @Size(max = 4000)
    @Column(
        name = "MC_URL_PRO_OVERENI",
        length = 4000
    )
    private String mcVerificationUrl;

    @Column(name = "MEZNI_DATUM_SZZ_DO")
    private LocalDate finalExamDeadline;

    @Column(name = "OBRAIDNO")
    private Long subjectCouncilId;

    @Column(name = "SIMS_ID_STUDIUM")
    private Long simsStudyId;

    @Size(max = 255)
    @Column(name = "SIMS_ID_ETAPY")
    private String simsStageId;

    @Column(name = "SIMS_ETAPA_OD")
    private LocalDate simsStageFrom;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "JEDNA_SE_O_JOINTDEGRE",
        nullable = false,
        length = 1
    )
    private String isJointDegree;

    @Column(name = "PRUBEH_STUDIA_TISK_DATUM")
    private LocalDate studyProgressPrintDate;

}