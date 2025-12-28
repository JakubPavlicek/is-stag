package com.stag.platform.education.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

/// **Evaluation Entity**
///
/// Represents a specific evaluation/grade within an evaluation type.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
@Setter
@Entity
@Table(
    name = "CIS_HODNOCENI",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "CIHO_CIHT_FK_I",
            columnList = "TYHOIDNO"
        ),
        @Index(
            name = "CIHO_CIHO_CZZKR",
            columnList = "CZ_ZKRATKA"
        )
    }
)
public class Evaluation {

    @Id
    @Column(
        name = "HODNIDNO",
        nullable = false
    )
    private Long id;

    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
        name = "TYHOIDNO",
        nullable = false,
        referencedColumnName = "TYHOIDNO"
    )
    private EvaluationType evaluationType;

    @Size(max = 3)
    @NotNull
    @Column(
        name = "CZ_ZKRATKA",
        nullable = false,
        length = 3
    )
    private String abbreviationCz;

    @Size(max = 100)
    @NotNull
    @Column(
        name = "CZ_NAZEV",
        nullable = false,
        length = 100
    )
    private String nameCz;

    @Size(max = 10)
    @Column(
        name = "AN_ZKRATKA",
        length = 10
    )
    private String abbreviationEn;

    @Size(max = 100)
    @Column(
        name = "AN_NAZEV",
        length = 100
    )
    private String nameEn;

    @Size(max = 255)
    @Column(name = "CZ_UROVEN_ZNALOSTI")
    private String knowledgeLevelCz;

    @Size(max = 255)
    @Column(name = "AN_UROVEN_ZNALOSTI")
    private String knowledgeLevelEn;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "JE_TO_USPECH",
        nullable = false,
        length = 1
    )
    private String isSuccess;

    @NotNull
    @ColumnDefault("99")
    @Column(
        name = "PORADI",
        nullable = false
    )
    private Short order;

    @NotNull
    @ColumnDefault("99")
    @Column(
        name = "HODNOTA_DO_PRUMERU",
        nullable = false,
        precision = 5,
        scale = 2
    )
    private BigDecimal valueForAverage;

    @Size(max = 2)
    @Column(
        name = "ECTS_EKVIVALENT",
        length = 2
    )
    private String ectsEquivalent;

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
        name = "DATE_OF_INSERT",
        nullable = false
    )
    private LocalDate dateOfInsert;

    @Size(max = 30)
    @Column(
        name = "UPDATOR",
        length = 30
    )
    private String updator;

    @Column(name = "DATE_OF_UPDATE")
    private LocalDate dateOfUpdate;

    @Size(max = 4)
    @NotNull
    @ColumnDefault("'1970'")
    @Column(
        name = "PLATNY_OD",
        nullable = false,
        length = 4
    )
    private String validFrom;

    @Size(max = 4)
    @NotNull
    @ColumnDefault("'2099'")
    @Column(
        name = "NEPLATNY_OD",
        nullable = false,
        length = 4
    )
    private String invalidFrom;

    @Column(name = "BODU_OD")
    private Short pointsFrom;

    @Column(name = "BODU_DO")
    private Short pointsTo;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "DO_PRUMERU",
        nullable = false,
        length = 1
    )
    private String forAverage;

    @Size(max = 1000)
    @Column(
        name = "AN_NAZEV_DOKUMENTY",
        length = 1000
    )
    private String documentNameEn;

}