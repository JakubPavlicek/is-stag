package com.stag.academics.studyplan.entity;

import com.stag.academics.fieldofstudy.entity.FieldOfStudy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

/// **Study Plan Entity**
///
/// Represents a study plan for a specific academic year and field of study.
/// Contains details about credit requirements, semester structure, language, and specialization options.
/// Supports plan versioning and copying.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
@Setter
@Entity
@Table(
    name = "STUDIJNI_PLANY",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "STPL_OBOR_FK_I",
            columnList = "OBORIDNO"
        ),
        @Index(
            name = "STPL_STPL_FK_I",
            columnList = "STPLIDNO_ZKOPIROVANO"
        )
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "STPL_UK",
            columnNames = { "ROK_PLATNOSTI", "ETAPA", "OBORIDNO", "VERZE" }
        )
    }
)
public class StudyPlan {

    @Id
    @Column(
        name = "STPLIDNO",
        nullable = false
    )
    private Long id;

    @NotNull
    @Column(
        name = "DATE_OF_INSERT",
        nullable = false
    )
    private LocalDate dateOfInsert;

    @Column(name = "DATE_OF_UPDATE")
    private LocalDate dateOfUpdate;

    @Size(max = 30)
    @Column(
        name = "UPDATOR",
        length = 30
    )
    private String updator;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "KREDITNE",
        nullable = false,
        length = 1
    )
    private String isCreditBased;

    @Size(max = 4)
    @NotNull
    @Column(
        name = "ROK_PLATNOSTI",
        nullable = false,
        length = 4
    )
    private String academicYear;

    @NotNull
    @Column(
        name = "LIMIT_CRD",
        nullable = false
    )
    private Short creditLimit;

    @Size(max = 30)
    @NotNull
    @Column(
        name = "OWNER",
        nullable = false,
        length = 30
    )
    private String owner;

    @NotNull
    @Column(
        name = "POCET_SEMESTRU",
        nullable = false
    )
    private Short numberOfSemesters;

    @Size(max = 10)
    @NotNull
    @Column(
        name = "VERZE",
        nullable = false,
        length = 10
    )
    private String version;

    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "OBORIDNO",
        nullable = false
    )
    @ToString.Exclude
    private FieldOfStudy fieldOfStudy;

    @Size(max = 10)
    @Column(
        name = "BROZURA",
        length = 10
    )
    private String brochure;

    @Size(max = 80)
    @Column(
        name = "NAZEV",
        length = 80
    )
    private String name;

    @Column(name = "PORADI")
    private Short sequence;

    @Size(max = 4000)
    @Column(
        name = "POZNAMKA",
        length = 4000
    )
    private String note;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "ETAPA",
        nullable = false,
        length = 1
    )
    private String stage;

    @Size(max = 2)
    @NotNull
    @ColumnDefault("'CZ'")
    @Column(
        name = "VYUC_JAZYK",
        nullable = false,
        length = 2
    )
    private String language;

    @Size(max = 80)
    @Column(
        name = "AN_NAZEV",
        length = 80
    )
    private String nameEn;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "SPECIALIZACE",
        nullable = false,
        length = 1
    )
    private String isSpecialization;

    @Column(name = "PREAMBULE")
    private byte[] preamble;

    @Size(max = 50)
    @Column(
        name = "PREAM_NAZEV",
        length = 50
    )
    private String preambleName;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "ECTS_ZOBRAZIT",
        nullable = false,
        length = 1
    )
    private String displayEcts;

    @Size(max = 255)
    @Column(name = "IDENTIFIKATOR")
    private String identifier;

    @Size(max = 4000)
    @Column(
        name = "POZNAMKA_VEREJNA_CZ",
        length = 4000
    )
    private String publicNoteCz;

    @Size(max = 4000)
    @Column(
        name = "POZNAMKA_VEREJNA_AN",
        length = 4000
    )
    private String publicNoteEn;

    @Size(max = 255)
    @Column(name = "POPIS_PLANU_URL")
    private String descriptionUrl;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'L'")
    @Column(
        name = "OMEZENI_ZAPISU",
        nullable = false,
        length = 1
    )
    private String enrollmentLimitation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STPLIDNO_ZKOPIROVANO")
    @ToString.Exclude
    private StudyPlan copiedFromStudyPlan;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "PRO_PRVAKY",
        nullable = false,
        length = 1
    )
    private String isForFirstYearStudents;

}
