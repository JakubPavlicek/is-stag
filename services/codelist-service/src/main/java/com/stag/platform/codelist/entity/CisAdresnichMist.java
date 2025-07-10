package com.stag.platform.codelist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
    name = "CIS_ADRESNICH_MIST",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "ADMI_FK_ULICIDNO",
            columnList = "ULICIDNO"
        ),
        @Index(
            name = "ADMI_FK_OBECIDNO",
            columnList = "OBEC_IDNO"
        ),
        @Index(
            name = "ADMI_FK_CCOB_IDNO",
            columnList = "CCOB_IDNO"
        ),
        @Index(
            name = "ADMI_CISLO_DOMOVNI_I",
            columnList = "CISLO_DOMOVNI"
        ),
        @Index(
            name = "ADMI_CISLO_ORIENTACNI_I",
            columnList = "CISLO_ORIENTACNI"
        ),
        @Index(
            name = "ADMI_PSC_I",
            columnList = "PSC"
        )
    }
)
public class CisAdresnichMist {

    @Id
    @Column(
        name = "ADMIIDNO",
        nullable = false
    )
    private Long id;

    @Column(name = "ULICIDNO")
    private Long ulicidno;

    @Column(name = "OBEC_IDNO")
    private Long obecIdno;

    @Column(name = "CCOB_IDNO")
    private Long ccobIdno;

    @Size(max = 10)
    @Column(
        name = "TYP_SO",
        length = 10
    )
    private String typSo;

    @Size(max = 20)
    @Column(
        name = "CISLO_DOMOVNI",
        length = 20
    )
    private String cisloDomovni;

    @Size(max = 20)
    @Column(
        name = "CISLO_ORIENTACNI",
        length = 20
    )
    private String cisloOrientacni;

    @Size(max = 20)
    @Column(
        name = "CISLO_ORIENTACNI_PISMENO",
        length = 20
    )
    private String cisloOrientacniPismeno;

    @Size(max = 5)
    @NotNull
    @Column(
        name = "PSC",
        nullable = false,
        length = 5
    )
    private String psc;

    @Column(name = "VZNIK_DNE")
    private LocalDate vznikDne;

    @Column(name = "ZANIK_DNE")
    private LocalDate zanikDne;

    @Column(
        name = "GPS_X",
        precision = 18,
        scale = 15
    )
    private BigDecimal gpsX;

    @Column(
        name = "GPS_Y",
        precision = 18,
        scale = 15
    )
    private BigDecimal gpsY;

}