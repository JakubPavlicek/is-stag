package com.stag.platform.codelist.entity;

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

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
    name = "CIS_SS",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "CISS_COBC_FK_I",
            columnList = "OBEC_IDNO"
        )
    }
)
public class CisSs {

    @Id
    @Size(max = 10)
    @Column(
        name = "IZO",
        nullable = false,
        length = 10
    )
    private String izo;

    @Size(max = 150)
    @Column(
        name = "NAZEV",
        length = 150
    )
    private String nazev;

    @Size(max = 3)
    @Column(
        name = "TYP_SS",
        length = 3
    )
    private String typSs;

    @Size(max = 255)
    @Column(name = "MESTO")
    private String mesto;

    @Size(max = 48)
    @Column(
        name = "ULICE",
        length = 48
    )
    private String ulice;

    @Size(max = 9)
    @Column(
        name = "CISLO_ULICE",
        length = 9
    )
    private String cisloUlice;

    @Size(max = 5)
    @Column(
        name = "PSC",
        length = 5
    )
    private String psc;

    @Size(max = 1)
    @Column(
        name = "TYP_PR",
        length = 1
    )
    private String typPr;

    @NotNull
    @Column(
        name = "PLATNOST_OD",
        nullable = false
    )
    private LocalDate platnostOd;

    @Column(name = "PLATNOST_DO")
    private LocalDate platnostDo;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("null")
    @Column(
        name = "OWNER",
        nullable = false,
        length = 30
    )
    private String owner;

    @Size(max = 9)
    @Column(
        name = "RED_IZO",
        length = 9
    )
    private String redIzo;

    @NotNull
    @ColumnDefault("null")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OBEC_IDNO")
    private CisObci obecIdno;

}