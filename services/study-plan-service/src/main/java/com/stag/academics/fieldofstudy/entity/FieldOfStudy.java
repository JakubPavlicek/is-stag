package com.stag.academics.fieldofstudy.entity;

import com.stag.academics.studyprogram.entity.StudyProgram;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

/// **Field of Study Entity**
///
/// Represents a field of study within a study program.
/// Contains detailed information including multilingual names, descriptions,
/// accreditation details, admission requirements, learning outcomes, and graduate profiles.
/// Supports teaching qualifications, specializations, and micro-certifications.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
@Setter
@Entity
@Table(
    name = "OBORY",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "OBOR_STPR_FK_I",
            columnList = "STPRIDNO"
        ),
        @Index(
            name = "OBOR_OBOR_FK_I",
            columnList = "OBORIDNO_PODM"
        ),
        @Index(
            name = "OBOR_PRAC_FK_I",
            columnList = "PRAC_ZKR"
        ),
        @Index(
            name = "OBOR_UCIT_UDCP_FK_I",
            columnList = "UCITIDNO_DC_PODPIS"
        ),
        @Index(
            name = "OBOR_UCIT_FK_I",
            columnList = "UCITIDNO_DC_ADMINISTRACE"
        ),
        @Index(
            name = "UCITIDNO_GARANT_FK_I",
            columnList = "UCITIDNO_GARANT"
        ),
        @Index(
            name = "UCITIDNO_GARANT_2_FK_I",
            columnList = "UCITIDNO_GARANT_2"
        ),
        @Index(
            name = "OBOR_AKOR_FK_I",
            columnList = "AKORIDNO"
        )
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "OBOR_UK",
            columnNames = { "CISOBORU", "CISSPEC", "FORMA_I", "TYP_I", "CISLO_A", "FAKULTA_OBORU", "STPRIDNO" }
        )
    }
)
public class FieldOfStudy {

    @Id
    @Column(
        name = "OBORIDNO",
        nullable = false
    )
    private Long id;

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

    @Size(max = 4000)
    @Column(
        name = "AN_ANOTACE",
        length = 4000
    )
    private String annotationEn;

    @Size(max = 4000)
    @Column(
        name = "J4_ANOTACE",
        length = 4000
    )
    private String annotationLang4;

    @Size(max = 4000)
    @Column(
        name = "J3_ANOTACE",
        length = 4000
    )
    private String annotationLang3;

    @Size(max = 30)
    @NotNull
    @Column(
        name = "OWNER",
        nullable = false,
        length = 30
    )
    private String owner;

    @Size(max = 15)
    @NotNull
    @Column(
        name = "CISOBORU",
        nullable = false,
        length = 15
    )
    private String fieldNumber;

    @Size(max = 2)
    @NotNull
    @Column(
        name = "CISSPEC",
        nullable = false,
        length = 2
    )
    private String specializationNumber;

    @Size(max = 4000)
    @Column(
        name = "CZ_ANOTACE",
        length = 4000
    )
    private String annotationCz;

    @NotNull
    @Column(
        name = "LIMIT_CRD_OBOR",
        nullable = false
    )
    private Short creditLimit;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "VYKAZOVAN",
        nullable = false,
        length = 1
    )
    private String reportedStatus;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "JE_APROBACE",
        nullable = false,
        length = 1
    )
    private String isTeachingQualification;

    @Size(max = 4)
    @NotNull
    @Column(
        name = "FORMA_I",
        nullable = false,
        length = 4
    )
    private String form;

    @Size(max = 4)
    @NotNull
    @Column(
        name = "TYP_I",
        nullable = false,
        length = 4
    )
    private String type;

    @Size(max = 2)
    @Column(
        name = "CISLO_A",
        length = 2
    )
    private String numberA;

    @Size(max = 6)
    @NotNull
    @Column(
        name = "FAKULTA_OBORU",
        nullable = false,
        length = 6
    )
    private String faculty;

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

    @NotNull
    @Column(
        name = "POCET_ETAP",
        nullable = false
    )
    private Boolean numberOfStages = false;

    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "STPRIDNO",
        nullable = false
    )
    private StudyProgram studyProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OBORIDNO_PODM")
    private FieldOfStudy prerequisiteFieldOfStudy;

    @Size(max = 200)
    @NotNull
    @ColumnDefault("'neuveden'")
    @Column(
        name = "CZ_NAZEV",
        nullable = false,
        length = 200
    )
    private String nameCz;

    @Size(max = 10)
    @Column(
        name = "ZKRATKA",
        length = 10
    )
    private String abbreviation;

    @Column(
        name = "STAND_DELKA",
        precision = 3,
        scale = 1
    )
    private BigDecimal standardLength;

    @Column(
        name = "MAX_DELKA",
        precision = 4,
        scale = 2
    )
    private BigDecimal maxLength;

    @Size(max = 10)
    @Column(
        name = "TYP_CERTIFIKATU",
        length = 10
    )
    private String certificateType;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "TISK",
        nullable = false,
        length = 1
    )
    private String printStatus;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "ROZPRAVA",
        nullable = false,
        length = 1
    )
    private String hasDissertation;

    @Size(max = 10)
    @Column(
        name = "CIS_RADA_CERTIF",
        length = 10
    )
    private String certificateSeriesNumber;

    @Size(max = 10)
    @Column(
        name = "CIS_RADA_ARCERTIF",
        length = 10
    )
    private String archiveCertificateSeriesNumber;

    @Column(name = "PRAC_ZKR")
    private String department;

    @Size(max = 3990)
    @Column(
        name = "PROFIL",
        length = 3990
    )
    private String profile;

    @Size(max = 3990)
    @Column(
        name = "PROFIL_CZ",
        length = 3990
    )
    private String profileCz;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "TISK_SPECIALIZACE",
        nullable = false,
        length = 1
    )
    private String printSpecialization;

    @Size(max = 1000)
    @Column(
        name = "TEXT_NA_DIPLOMU",
        length = 1000
    )
    private String diplomaText;

    @Size(max = 255)
    @Column(name = "IDENTIFIKATOR")
    private String identifier;

    @Size(max = 20)
    @Column(
        name = "ECTS_TELEFON",
        length = 20
    )
    private String ectsPhone;

    @Size(max = 20)
    @Column(
        name = "ECTS_FAX",
        length = 20
    )
    private String ectsFax;

    @Size(max = 100)
    @Column(
        name = "ECTS_MAIL",
        length = 100
    )
    private String ectsEmail;

    @Column(name = "UCITIDNO_DC_PODPIS")
    private Long deanerySignatoryTeacherId;

    @Column(name = "UCITIDNO_DC_ADMINISTRACE")
    private Long deaneryAdminTeacherId;

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
    @Column(
        name = "POZADAVKY_PRO_PRIJETI",
        length = 2
    )
    private String admissionRequirements;

    @Size(max = 255)
    @Column(name = "CZ_UZNAVANI_PREDCH_VZDELANI")
    private String previousEducationRecognitionCz;

    @Size(max = 255)
    @Column(name = "AN_UZNAVANI_PREDCH_VZDELANI")
    private String previousEducationRecognitionEn;

    @Size(max = 255)
    @Column(name = "CZ_KVALIFIKACNI_POZADAVKY")
    private String qualificationRequirementsCz;

    @Size(max = 255)
    @Column(name = "AN_KVALIFIKACNI_POZADAVKY")
    private String qualificationRequirementsEn;

    @Size(max = 2)
    @Column(
        name = "PROFIL_PROGRAMU",
        length = 2
    )
    private String programProfile;

    @Size(max = 1000)
    @Column(
        name = "CZ_PROFESNI_PROFILY_ABSOLVENTU",
        length = 1000
    )
    private String graduateProfessionalProfileCz;

    @Size(max = 1000)
    @Column(
        name = "AN_PROFESNI_PROFILY_ABSOLVENTU",
        length = 1000
    )
    private String graduateProfessionalProfileEn;

    @Column(name = "UCITIDNO_GARANT")
    private Long guarantorTeacherId;

    @Size(max = 50)
    @Column(
        name = "AKREDITOVANO_PODLE_ZAKONA",
        length = 50
    )
    private String accreditedByLaw;

    @Size(max = 255)
    @Column(name = "TEXT_NA_OSVEDCENI")
    private String certificateText;

    @Column(name = "AKREDITACE_OD")
    private LocalDate accreditationValidFrom;

    @Column(name = "AKREDITACE_DO")
    private LocalDate accreditationValidTo;

    @Size(max = 50)
    @Column(
        name = "AKREDITACE_CISLO",
        length = 50
    )
    private String accreditationNumber;

    @Size(max = 50)
    @Column(
        name = "ROZSAH_HODIN",
        length = 50
    )
    private String hoursRange;

    @Size(max = 255)
    @Column(name = "TEXT_NA_DOKUMENT")
    private String documentText;

    @Column(name = "UCITIDNO_GARANT_2")
    private Long guarantorTeacherId2;

    @Size(max = 50)
    @Column(
        name = "CISLO_ROZHODNUTI_RCV",
        length = 50
    )
    private String rcvDecisionNumber;

    @Size(max = 1000)
    @Column(
        name = "PROFIL_PROGRAMU_CZ",
        length = 1000
    )
    private String programProfileCz;

    @Size(max = 1000)
    @Column(
        name = "PROFIL_PROGRAMU_AN",
        length = 1000
    )
    private String programProfileEn;

    @Size(max = 4000)
    @Column(
        name = "AN_PREDPOKLADY_ZNALOSTI",
        length = 4000
    )
    private String knowledgePrerequisitesEn;

    @Size(max = 4000)
    @Column(
        name = "CZ_PREDPOKLADY_ZPUSOBILOSTI",
        length = 4000
    )
    private String competencyPrerequisitesCz;

    @Size(max = 4000)
    @Column(
        name = "CZ_PREDPOKLADY_ZNALOSTI",
        length = 4000
    )
    private String knowledgePrerequisitesCz;

    @Size(max = 4000)
    @Column(
        name = "CZ_PREDPOKLADY_DOVEDNOSTI",
        length = 4000
    )
    private String skillPrerequisitesCz;

    @Size(max = 4000)
    @Column(
        name = "AN_PREDPOKLADY_DOVEDNOSTI",
        length = 4000
    )
    private String skillPrerequisitesEn;

    @Size(max = 4000)
    @Column(
        name = "AN_PREDPOKLADY_ZPUSOBILOSTI",
        length = 4000
    )
    private String competencyPrerequisitesEn;

    @Size(max = 4000)
    @Column(
        name = "CZ_VYSTUPY_ZNALOSTI",
        length = 4000
    )
    private String knowledgeOutcomesCz;

    @Size(max = 4000)
    @Column(
        name = "AN_VYSTUPY_ZNALOSTI",
        length = 4000
    )
    private String knowledgeOutcomesEn;

    @Size(max = 4000)
    @Column(
        name = "CZ_VYSTUPY_DOVEDNOSTI",
        length = 4000
    )
    private String skillOutcomesCz;

    @Size(max = 4000)
    @Column(
        name = "AN_VYSTUPY_DOVEDNOSTI",
        length = 4000
    )
    private String skillOutcomesEn;

    @Size(max = 4000)
    @Column(
        name = "CZ_VYSTUPY_ZPUSOBILOSTI",
        length = 4000
    )
    private String competencyOutcomesCz;

    @Size(max = 4000)
    @Column(
        name = "AN_VYSTUPY_ZPUSOBILOSTI",
        length = 4000
    )
    private String competencyOutcomesEn;

    @Size(max = 3)
    @Column(
        name = "OBLAST_VZDELAVANI",
        length = 3
    )
    private String educationArea;

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

    @Size(max = 20)
    @Column(
        name = "KOD_ISCED",
        length = 20
    )
    private String iscedCode;

    @Size(max = 50)
    @Column(
        name = "FUNKCE_DC",
        length = 50
    )
    private String deaneryFunction;

    @Column(name = "POCET_PRIJIMANYCH")
    private Short admittedCount;

    @Size(max = 255)
    @Column(name = "POCET_PRIJIMANYCH_POZNAMKA")
    private String admittedCountNote;

    @Size(max = 4000)
    @Column(
        name = "POZADOVANA_ZDR_ZPUSOBILOST_AN",
        length = 4000
    )
    private String requiredHealthCompetencyEn;

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

    @Size(max = 4000)
    @Column(
        name = "POZADOVANA_ZDR_ZPUSOBILOST_CZ",
        length = 4000
    )
    private String requiredHealthCompetencyCz;

    @Size(max = 4000)
    @Column(
        name = "PROFIL_ABSOLVENTA_CZ",
        length = 4000
    )
    private String graduateProfileCz;

    @Size(max = 4000)
    @Column(
        name = "PREDPOKLAD_UPLATNITELNOSTI_CZ",
        length = 4000
    )
    private String employabilityAssumptionCz;

    @Size(max = 4000)
    @Column(
        name = "PROFIL_ABSOLVENTA_AN",
        length = 4000
    )
    private String graduateProfileEn;

    @Size(max = 4000)
    @Column(
        name = "PREDPOKLAD_UPLATNITELNOSTI_AN",
        length = 4000
    )
    private String employabilityAssumptionEn;

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

    @Size(max = 500)
    @Column(
        name = "REGULOVANE_POVOLANI_UZNAVACI_O",
        length = 500
    )
    private String regulatedProfessionRecognitionBody;

    @Size(max = 1000)
    @Column(
        name = "POZNAMKA_VEREJNA",
        length = 1000
    )
    private String publicNote;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "HLASIT_NA_REGISTR_ZDR_PRAC",
        nullable = false,
        length = 1
    )
    private String reportToHealthRegistry;

    @Size(max = 100)
    @Column(
        name = "ZDRAVOTNICKY_OBOR",
        length = 100
    )
    private String healthcareField;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "SV_VYDAVAME_DIPLOM_A_DS",
        nullable = false,
        length = 1
    )
    private String issueDiplomaAndSupplement;

    @Column(name = "SV_LOGO_PROGRAMU")
    private byte[] programLogo;

    @Size(max = 255)
    @Column(name = "LOGO_PROGRAMU_NAZEV")
    private String programLogoName;

    @Size(max = 100)
    @Column(
        name = "ZDRAVOTNICKY_OBOR_2",
        length = 100
    )
    private String healthcareField2;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'1'")
    @Column(
        name = "JOINT_DEGREES",
        nullable = false,
        length = 1
    )
    private String jointDegrees;

    @Size(max = 1)
    @ColumnDefault("'N'")
    @Column(
        name = "TISK_SPECIALIZACE_DIPLOM_A_DS",
        length = 1
    )
    private String printSpecializationOnDiploma;

    @Size(max = 4000)
    @Column(
        name = "VZTAH_K_JINYM_MCERTIF",
        length = 4000
    )
    private String relationToOtherMicroCertificates;

    @Size(max = 255)
    @Column(name = "NAZEV_MCERTIF_AN")
    private String microCertificateNameEn;

    @Column(name = "AKORIDNO")
    private Long accreditationBodyId;

    @Size(max = 1)
    @Column(
        name = "TYP_ZAJISTENI_KVALITY",
        length = 1
    )
    private String qualityAssuranceType;

    @Size(max = 255)
    @Column(name = "NAZEV_MCERTIF_CZ")
    private String microCertificateNameCz;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'1'")
    @Column(
        name = "FORMA_UCASTI",
        nullable = false,
        length = 1
    )
    private String participationForm;

    @Size(max = 4000)
    @Column(
        name = "ZAJISTENI_KVALITY_CZ",
        length = 4000
    )
    private String qualityAssuranceCz;

    @Size(max = 4000)
    @Column(
        name = "ZAJISTENI_KVALITY_AN",
        length = 4000
    )
    private String qualityAssuranceEn;

    @Size(max = 1)
    @Column(
        name = "TYP_HODNOCENI",
        length = 1
    )
    private String evaluationType;

    @Size(max = 1)
    @Column(
        name = "TYP_DOHLEDU_A_OVERENI",
        length = 1
    )
    private String supervisionAndVerificationType;

    @Column(name = "PLATNOST_CERTIFIKATU_LET")
    private Short certificateValidityYears;

    @Column(name = "DOBA_KURZU")
    private Short courseDuration;

    @Size(max = 1)
    @Column(
        name = "DOBA_KURZU_JEDNOTKA",
        length = 1
    )
    private String courseDurationUnit;

    @Size(max = 4000)
    @Column(
        name = "DOBA_KURZU_POZNAMKA",
        length = 4000
    )
    private String courseDurationNote;

    @Size(max = 4000)
    @Column(
        name = "LIMIT_CRD_POZNAMKA_CZ",
        length = 4000
    )
    private String creditLimitNoteCz;

    @Size(max = 4000)
    @Column(
        name = "LIMIT_CRD_POZNAMKA_AN",
        length = 4000
    )
    private String creditLimitNoteEn;

    @Size(max = 1)
    @ColumnDefault("'N'")
    @Column(
        name = "KATALOG_STAV",
        length = 1
    )
    private String catalogStatus;

    @Lob
    @Column(name = "KATALOG_STAV_LOG")
    private String catalogStatusLog;

    @Size(max = 1)
    @Column(
        name = "MC_EQF",
        length = 1
    )
    private String microCertificateEqf;

}
