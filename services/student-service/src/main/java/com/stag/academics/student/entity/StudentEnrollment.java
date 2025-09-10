package com.stag.academics.student.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
    name = "STUDENTI_NA_OBORU_V_ROCE",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "STNO_STVR_FK_I",
            columnList = "ROK_PLATNOSTI, OS_CISLO"
        ),
        @Index(
            name = "STNO_STUD_FK_I",
            columnList = "OS_CISLO"
        ),
        @Index(
            name = "STNO_STPL_FK_I",
            columnList = "STPLIDNO"
        ),
        @Index(
            name = "STNO_POMK_FK_I",
            columnList = "OBORIDNO_KOMB, KOMBIDNO_KOMB"
        ),
        @Index(
            name = "STNO_UCHAZEC_IDNO_I",
            columnList = "UCHAZEC_IDNO"
        )
    }
)
public class StudentEnrollment {

    @EmbeddedId
    private StudentEnrollmentId id;

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

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "KREDITY_DOS",
        nullable = false,
        precision = 4,
        scale = 1
    )
    private BigDecimal creditsEarned;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "KREDITY_PLAN",
        nullable = false,
        precision = 4,
        scale = 1
    )
    private BigDecimal creditsPlanned;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "PRUMER",
        nullable = false,
        precision = 4,
        scale = 2
    )
    private BigDecimal gradeAverage;

    @ColumnDefault("1")
    @Column(name = "ROK_ST")
    private Short yearOfStudy;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "KREDITY_UNBL",
        nullable = false
    )
    private Short unclassifiedCredits;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "KREDITY_ZAPS",
        nullable = false,
        precision = 4,
        scale = 1
    )
    private BigDecimal creditsRegistered;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "UZN_PRUMER",
        nullable = false,
        precision = 4,
        scale = 2
    )
    private BigDecimal recognizedGradeAverage;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "UZN_POC_PREDM",
        nullable = false
    )
    private Short recognizedSubjectCount;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "KONTROLA_ETAPY",
        nullable = false,
        length = 1
    )
    private String stageCheck;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "ZS_CIT_PR",
        nullable = false,
        precision = 12,
        scale = 6
    )
    private BigDecimal winterSemesterGradeNumerator;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "ZS_JM_PR",
        nullable = false,
        precision = 12,
        scale = 6
    )
    private BigDecimal winterSemesterGradeDenominator;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "LS_CIT_PR",
        nullable = false,
        precision = 12,
        scale = 6
    )
    private BigDecimal summerSemesterGradeNumerator;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "LS_JM_PR",
        nullable = false,
        precision = 12,
        scale = 6
    )
    private BigDecimal summerSemesterGradeDenominator;

    @NotNull
    @ColumnDefault("0")
    @Column(
        name = "KREDITY_DOS_BEZ_ZP",
        nullable = false,
        precision = 4,
        scale = 1
    )
    private BigDecimal creditsEarnedWithoutExam;

    @Column(name = "UCHAZEC_IDNO")
    private Long applicantId;

    @Size(max = 255)
    @Column(name = "IDENTIFIKATOR")
    private String identifier;

    @Column(name = "ORIDNO")
    private Long orId;

}