package com.stag.academics.studyprogram.entity;

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

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
    name = "STUDIJNI_PROGRAMY",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "STPR_PCIPR_FK_I",
            columnList = "FAKULTA_SP"
        ),
        @Index(
            name = "STPR_CISP_FK_I",
            columnList = "CISP_KOD"
        ),
        @Index(
            name = "STPR_UCIT_FK_I",
            columnList = "UCITIDNO_GARANT"
        ),
        @Index(
            name = "STPR_CIHT_FK_I",
            columnList = "TYHOIDNO_CELKEM"
        ),
        @Index(
            name = "STPR_CIHT_TYCF_FK_I",
            columnList = "TYHOIDNO_CERTIFIKATY"
        ),
        @Index(
            name = "STPR_CIHT_TYOP_FK_I",
            columnList = "TYHOIDNO_OPONENT"
        ),
        @Index(
            name = "STPR_CIHT_TYZP_FK_I",
            columnList = "TYHOIDNO_ZAVER_PRACE"
        ),
        @Index(
            name = "STPR_CIHT_TYPR_FK_I",
            columnList = "TYHOIDNO_VSKP"
        ),
        @Index(
            name = "STPR_CIHT_TYVE_FK_I",
            columnList = "TYHOIDNO_VEDOUCI"
        ),
        @Index(
            name = "STPR_CIHT_TYPC_FK_I",
            columnList = "TYHOIDNO_PRED_CELKEM"
        ),
        @Index(
            name = "STPR_UCIT_UCAD_FK_I",
            columnList = "UCITIDNO_GARANT_ADMINISTRACE"
        )
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "STPR_UK",
            columnNames = { "FORMA", "FAKULTA_SP", "TYP", "KODSP_I", "STAND_DELKA", "JAZYK", "PROFIL_PROGRAMU" }
        )
    }
)
public class StudyProgram {

    @Id
    @Column(
        name = "STPRIDNO",
        nullable = false
    )
    private Long id;

    @NotNull
    @Column(
        name = "FAKULTA_SP",
        nullable = false
    )
    private String faculty;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "TYP",
        nullable = false,
        length = 1
    )
    private String type;

    @Size(max = 200)
    @Column(
        name = "AN_NAZEV",
        length = 200
    )
    private String nameEn;

    @Size(max = 200)
    @Column(
        name = "J3_NAZEV",
        length = 200
    )
    private String nameLang3;

    @Size(max = 200)
    @Column(
        name = "J4_NAZEV",
        length = 200
    )
    private String nameLang4;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "FORMA",
        nullable = false,
        length = 1
    )
    private String form;

    @Size(max = 4000)
    @Column(
        name = "AN_CILE",
        length = 4000
    )
    private String goalsEn;

    @Size(max = 4000)
    @Column(
        name = "J3_CILE",
        length = 4000
    )
    private String goalsLang3;

    @Size(max = 4000)
    @Column(
        name = "J4_CILE",
        length = 4000
    )
    private String goalsLang4;

    @NotNull
    @Column(
        name = "MAX_DELKA",
        nullable = false,
        precision = 4,
        scale = 2
    )
    private BigDecimal maxLength;

    @NotNull
    @Column(
        name = "STAND_DELKA",
        nullable = false,
        precision = 3,
        scale = 1
    )
    private BigDecimal standardLength;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "NAVAZUJICI",
        nullable = false,
        length = 1
    )
    private String isFollowUp;

    @Size(max = 10)
    @NotNull
    @Column(
        name = "TITUL",
        nullable = false,
        length = 10
    )
    private String degree;

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
    @ColumnDefault("'A'")
    @Column(
        name = "VYKAZOVAN",
        nullable = false,
        length = 1
    )
    private String reportedStatus;

    @Size(max = 4)
    @NotNull
    @Column(
        name = "PLATNY_OD",
        nullable = false,
        length = 4
    )
    private String validFrom;

    @Size(max = 4)
    @Column(
        name = "NEPLATNY_OD",
        length = 4
    )
    private String invalidFrom;

    @Size(max = 12)
    @NotNull
    @Column(
        name = "KODSP_I",
        nullable = false,
        length = 12
    )
    private String internalCode;

    @NotNull
    @Column(
        name = "LIMIT_CRD",
        nullable = false
    )
    private Short creditLimit;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "DIPLOM",
        nullable = false,
        length = 1
    )
    private String diploma;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "DOKUMENT",
        nullable = false,
        length = 1
    )
    private String document;

    @Size(max = 10)
    @NotNull
    @Column(
        name = "TYP_DIPLOMU",
        nullable = false,
        length = 10
    )
    private String diplomaType;

    @Size(max = 10)
    @NotNull
    @Column(
        name = "TYP_VYSVEDCENI",
        nullable = false,
        length = 10
    )
    private String reportType;

    @Size(max = 10)
    @NotNull
    @Column(
        name = "TYP_OSVEDCENI",
        nullable = false,
        length = 10
    )
    private String certificateType;

    @Column(name = "CISP_KOD")
    private String code;

    @Size(max = 200)
    @NotNull
    @ColumnDefault("'neexistuje'")
    @Column(
        name = "CZ_NAZEV",
        nullable = false,
        length = 200
    )
    private String nameCz;

    @Size(max = 4000)
    @NotNull
    @ColumnDefault("'nejsou'")
    @Column(
        name = "CZ_CILE",
        nullable = false,
        length = 4000
    )
    private String goalsCz;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "UNI_PAPIR",
        nullable = false,
        length = 1
    )
    private String universityPaper;

    @Size(max = 10)
    @Column(
        name = "CIS_RADA_DIPLOM",
        length = 10
    )
    private String diplomaSeriesNumber;

    @Size(max = 10)
    @Column(
        name = "CIS_RADA_VYSV",
        length = 10
    )
    private String reportSeriesNumber;

    @Size(max = 10)
    @Column(
        name = "CIS_RADA_OSVE",
        length = 10
    )
    private String certificateSeriesNumber;

    @Size(max = 10)
    @Column(
        name = "CIS_RADA_ARVY",
        length = 10
    )
    private String archiveReportSeriesNumber;

    @Size(max = 10)
    @Column(
        name = "CIS_RADA_AROS",
        length = 10
    )
    private String archiveCertificateSeriesNumber;

    @Size(max = 4000)
    @Column(
        name = "PROF_STATUS",
        length = 4000
    )
    private String professionalStatusEn;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "VYKAZOVAT_ZP",
        nullable = false,
        length = 1
    )
    private String reportToHealthInsurance;

    @Size(max = 4000)
    @Column(
        name = "PROF_STATUS_CZ",
        length = 4000
    )
    private String professionalStatusCz;

    @Size(max = 2000)
    @Column(
        name = "POZNAMKA",
        length = 2000
    )
    private String note;

    @Size(max = 240)
    @Column(
        name = "TYP_ZADANI_DP",
        length = 240
    )
    private String thesisAssignmentType;

    @Size(max = 240)
    @Column(
        name = "TYP_ZAPISU_SZZ",
        length = 240
    )
    private String finalExamEnrollmentType;

    @Size(max = 10)
    @Column(
        name = "TYP_DODATKU_DIPLOMU",
        length = 10
    )
    private String diplomaSupplementType;

    @Size(max = 10)
    @NotNull
    @ColumnDefault("'A##1'")
    @Column(
        name = "MASKA_OS_CISLO",
        nullable = false,
        length = 10
    )
    private String studentIdMask;

    @Size(max = 4000)
    @Column(
        name = "POZADAVKY_NA_PRIJETI_CZ",
        length = 4000
    )
    private String admissionRequirementsCz;

    @Size(max = 4000)
    @Column(
        name = "POZADAVKY_NA_PRIJETI_AN",
        length = 4000
    )
    private String admissionRequirementsEn;

    @Size(max = 255)
    @Column(name = "POZADAVKY_NA_PRIJETI_URL")
    private String admissionRequirementsUrl;

    @Size(max = 255)
    @Column(name = "IDENTIFIKATOR")
    private String identifier;

    @Size(max = 10)
    @NotNull
    @ColumnDefault("'nespec'")
    @Column(
        name = "PROFSTATUS",
        nullable = false,
        length = 10
    )
    private String professionalStatus;

    @Size(max = 2)
    @NotNull
    @ColumnDefault("'CZ'")
    @Column(
        name = "JAZYK",
        nullable = false,
        length = 2
    )
    private String language;

    @Column(name = "UCITIDNO_GARANT")
    private Long guarantorTeacherId;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "AKRED_S_CISLEM_OBORU",
        nullable = false,
        length = 1
    )
    private String accreditationWithFieldNumber;

    @Column(
        name = "MAX_DELKA_PRERUS",
        precision = 3,
        scale = 1
    )
    private BigDecimal maxInterruptionLength;

    @Size(max = 350)
    @Column(
        name = "CZ_NAZEV_PRO_DIPLOM",
        length = 350
    )
    private String nameForDiplomaCz;

    @Size(max = 350)
    @Column(
        name = "AN_NAZEV_PRO_DIPLOM",
        length = 350
    )
    private String nameForDiplomaEn;

    @Column(name = "ZAPISOVE_PROPUSTKY")
    private Short enrollmentPermits;

    @Size(max = 2)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "PROFIL_PROGRAMU",
        nullable = false,
        length = 2
    )
    private String programProfile;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "AKREDITACE_INSTITUCIONALNI",
        nullable = false,
        length = 1
    )
    private String institutionalAccreditation;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "AKREDITACE_PO_NOVELE",
        nullable = false,
        length = 1
    )
    private String accreditationPostAmendment;

    @Column(name = "AKREDITACE_OD")
    private LocalDate accreditationValidFrom;

    @Column(name = "AKREDITACE_DO")
    private LocalDate accreditationValidTo;

    @Column(name = "AKREDITACE_ZTRACENA_OD")
    private LocalDate accreditationLostFrom;

    @Size(max = 50)
    @Column(
        name = "AKREDITACE_CISLO",
        length = 50
    )
    private String accreditationNumber;

    @Size(max = 4000)
    @Column(
        name = "NAVAZNOST_NA_DALSI_SP_CZ",
        length = 4000
    )
    private String continuationToNextProgramCz;

    @Size(max = 4000)
    @Column(
        name = "NAVAZNOST_NA_DALSI_SP_AN",
        length = 4000
    )
    private String continuationToNextProgramEn;

    @Size(max = 4000)
    @Column(
        name = "NAVAZNOST_NA_DALSI_SP_J3",
        length = 4000
    )
    private String continuationToNextProgramLang3;

    @Size(max = 4000)
    @Column(
        name = "NAVAZNOST_NA_DALSI_SP_J4",
        length = 4000
    )
    private String continuationToNextProgramLang4;

    @Size(max = 4000)
    @Column(
        name = "OBSAHOVE_ZAMERENI_SP_CZ",
        length = 4000
    )
    private String contentFocusCz;

    @Size(max = 4000)
    @Column(
        name = "OBSAHOVE_ZAMERENI_SP_AN",
        length = 4000
    )
    private String contentFocusEn;

    @Size(max = 4000)
    @Column(
        name = "OBSAHOVE_ZAMERENI_SP_J3",
        length = 4000
    )
    private String contentFocusLang3;

    @Size(max = 4000)
    @Column(
        name = "OBSAHOVE_ZAMERENI_SP_J4",
        length = 4000
    )
    private String contentFocusLang4;

    @Size(max = 4000)
    @Column(
        name = "POZADOVANA_ZDR_ZPUSOBILOST_CZ",
        length = 4000
    )
    private String requiredHealthCompetencyCz;

    @Size(max = 4000)
    @Column(
        name = "POZADOVANA_ZDR_ZPUSOBILOST_AN",
        length = 4000
    )
    private String requiredHealthCompetencyEn;

    @Size(max = 4000)
    @Column(
        name = "POZADOVANA_ZDR_ZPUSOBILOST_J3",
        length = 4000
    )
    private String requiredHealthCompetencyLang3;

    @Size(max = 4000)
    @Column(
        name = "POZADOVANA_ZDR_ZPUSOBILOST_J4",
        length = 4000
    )
    private String requiredHealthCompetencyLang4;

    @Size(max = 4000)
    @Column(
        name = "PROFIL_ABSOLVENTA_CZ",
        length = 4000
    )
    private String graduateProfileCz;

    @Size(max = 4000)
    @Column(
        name = "PROFIL_ABSOLVENTA_AN",
        length = 4000
    )
    private String graduateProfileEn;

    @Size(max = 4000)
    @Column(
        name = "PROFIL_ABSOLVENTA_J3",
        length = 4000
    )
    private String graduateProfileLang3;

    @Size(max = 4000)
    @Column(
        name = "PROFIL_ABSOLVENTA_J4",
        length = 4000
    )
    private String graduateProfileLang4;

    @Size(max = 4000)
    @Column(
        name = "POZADAVKY_ROVNY_PRISTUP_ZDR_CZ",
        length = 4000
    )
    private String equalAccessHealthRequirementsCz;

    @Size(max = 4000)
    @Column(
        name = "POZADAVKY_ROVNY_PRISTUP_ZDR_AN",
        length = 4000
    )
    private String equalAccessHealthRequirementsEn;

    @Size(max = 4000)
    @Column(
        name = "POZADAVKY_ROVNY_PRISTUP_ZDR_J3",
        length = 4000
    )
    private String equalAccessHealthRequirementsLang3;

    @Size(max = 4000)
    @Column(
        name = "POZADAVKY_ROVNY_PRISTUP_ZDR_J4",
        length = 4000
    )
    private String equalAccessHealthRequirementsLang4;

    @Column(name = "POCET_PRIJIMANYCH")
    private Short admittedCount;

    @Size(max = 255)
    @Column(name = "POCET_PRIJIMANYCH_POZNAMKA")
    private String admittedCountNote;

    @Size(max = 4000)
    @Column(
        name = "PREDPOKLAD_UPLATNITELNOSTI_CZ",
        length = 4000
    )
    private String employabilityAssumptionCz;

    @Size(max = 4000)
    @Column(
        name = "PREDPOKLAD_UPLATNITELNOSTI_AN",
        length = 4000
    )
    private String employabilityAssumptionEn;

    @Size(max = 4000)
    @Column(
        name = "PREDPOKLAD_UPLATNITELNOSTI_J3",
        length = 4000
    )
    private String employabilityAssumptionLang3;

    @Size(max = 4000)
    @Column(
        name = "PREDPOKLAD_UPLATNITELNOSTI_J4",
        length = 4000
    )
    private String employabilityAssumptionLang4;

    @Size(max = 4000)
    @Column(
        name = "REGULOVANE_POVOLANI_CZ",
        length = 4000
    )
    private String regulatedProfessionCz;

    @Size(max = 4000)
    @Column(
        name = "REGULOVANE_POVOLANI_AN",
        length = 4000
    )
    private String regulatedProfessionEn;

    @Size(max = 4000)
    @Column(
        name = "REGULOVANE_POVOLANI_J3",
        length = 4000
    )
    private String regulatedProfessionLang3;

    @Size(max = 4000)
    @Column(
        name = "REGULOVANE_POVOLANI_J4",
        length = 4000
    )
    private String regulatedProfessionLang4;

    @Size(max = 500)
    @Column(
        name = "REGULOVANE_POVOLANI_UZNAVACI_O",
        length = 500
    )
    private String regulatedProfessionRecognitionBody;

    @Size(max = 4000)
    @Column(
        name = "MOZNE_PRACOVNI_POZICE_CZ",
        length = 4000
    )
    private String possibleJobPositionsCz;

    @Size(max = 4000)
    @Column(
        name = "MOZNE_PRACOVNI_POZICE_AN",
        length = 4000
    )
    private String possibleJobPositionsEn;

    @Size(max = 4000)
    @Column(
        name = "MOZNE_PRACOVNI_POZICE_J3",
        length = 4000
    )
    private String possibleJobPositionsLang3;

    @Size(max = 4000)
    @Column(
        name = "MOZNE_PRACOVNI_POZICE_J4",
        length = 4000
    )
    private String possibleJobPositionsLang4;

    @Size(max = 4000)
    @Column(
        name = "VYUKA_ZAHRANICNI_PREDPIS",
        length = 4000
    )
    private String foreignInstructionRegulation;

    @Size(max = 4000)
    @Column(
        name = "VYUKA_AVCR_PRACOVISTE",
        length = 4000
    )
    private String avcrWorkplaceInstruction;

    @Size(max = 1)
    @Column(
        name = "AKREDITACE_OMEZENA_DUVOD",
        length = 1
    )
    private String limitedAccreditationReason;

    @Size(max = 50)
    @Column(
        name = "AKREDITACE_OMEZENA_CISLO",
        length = 50
    )
    private String limitedAccreditationNumber;

    @Column(name = "AKREDITACE_OMEZENA_OD")
    private LocalDate limitedAccreditationFrom;

    @NotNull
    @ColumnDefault("1")
    @Column(
        name = "KOEF_EKONOMICKE_NAROCNOSTI",
        nullable = false,
        precision = 4,
        scale = 2
    )
    private BigDecimal economicComplexityCoefficient;

    @Size(max = 20)
    @Column(
        name = "KOD_ISCED",
        length = 20
    )
    private String iscedCode;

    @Column(name = "REGULOVANE_POVOLANI_DATUM")
    private LocalDate regulatedProfessionDate;

    @Size(max = 4000)
    @Column(
        name = "TEMATA_VSKP",
        length = 4000
    )
    private String thesisTopics;

    @Size(max = 4000)
    @Column(
        name = "PRAKTICKA_VYUKA",
        length = 4000
    )
    private String practicalTraining;

    @Size(max = 4000)
    @Column(
        name = "SPOLUPRACE_S_PRAXI",
        length = 4000
    )
    private String cooperationWithPractice;

    @Size(max = 4000)
    @Column(
        name = "ODBORNA_PRAXE",
        length = 4000
    )
    private String professionalPractice;

    @Size(max = 1000)
    @Column(
        name = "POZNAMKA_VEREJNA",
        length = 1000
    )
    private String publicNote;

    @Size(max = 2)
    @Column(
        name = "TYP_ZAVERECNE_PRACE",
        length = 2
    )
    private String finalThesisType;

    @Column(name = "HODINOVA_DOTACE")
    private Short hourlyLoad;

    @Size(max = 1)
    @ColumnDefault("'K'")
    @Column(
        name = "STUPEN_POZAD_VZDELANI",
        length = 1
    )
    private String requiredEducationLevel;

    @Size(max = 1)
    @Column(
        name = "UCEL_PROGRAMU_CZV",
        length = 1
    )
    private String lifelongLearningProgramPurpose;

    @Size(max = 4000)
    @Column(
        name = "PODMINKY_POKRACOVANI_STUDIA",
        length = 4000
    )
    private String continuationConditions;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'1'")
    @Column(
        name = "JOINT_DEGREES",
        nullable = false,
        length = 1
    )
    private String jointDegrees;

    @NotNull
    @Column(
        name = "TYHOIDNO_CELKEM",
        nullable = false
    )
    private Long totalEvaluationTypeId;

    @NotNull
    @Column(
        name = "TYHOIDNO_CERTIFIKATY",
        nullable = false
    )
    private Long certificatesEvaluationTypeId;

    @NotNull
    @Column(
        name = "TYHOIDNO_OPONENT",
        nullable = false
    )
    private Long opponentEvaluationTypeId;

    @NotNull
    @Column(
        name = "TYHOIDNO_ZAVER_PRACE",
        nullable = false
    )
    private Long thesisEvaluationTypeId;

    @NotNull
    @Column(
        name = "TYHOIDNO_VSKP",
        nullable = false
    )
    private Long finalThesisEvaluationTypeId;

    @NotNull
    @Column(
        name = "TYHOIDNO_VEDOUCI",
        nullable = false
    )
    private Long supervisorEvaluationTypeId;

    @NotNull
    @Column(
        name = "TYHOIDNO_PRED_CELKEM",
        nullable = false
    )
    private Long subjectTotalEvaluationTypeId;

    @Size(max = 2)
    @Column(
        name = "TITUL_RIGO",
        length = 2
    )
    private String rigorosumDegree;

    @Column(name = "UCITIDNO_GARANT_ADMINISTRACE")
    private Long guarantorAdminTeacherId;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "TISK_SPECIALIZACE_DIPLOM_A_DS",
        nullable = false,
        length = 1
    )
    private String printSpecializationOnDiploma;

    @Size(max = 4000)
    @Column(
        name = "TEXT_NA_DIPLOMU",
        length = 4000
    )
    private String diplomaText;

    @Size(max = 6)
    @Column(
        name = "VYUKA_SPOLECNA_FAKULTA",
        length = 6
    )
    private String jointFacultyInstruction;

    @Size(max = 30)
    @Column(
        name = "STUDIJNI_REFERENTKA",
        length = 30
    )
    private String studyAdministrator;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "MOZNOST_RIGOROZA",
        nullable = false,
        length = 1
    )
    private String rigorosumPossibility;

}
