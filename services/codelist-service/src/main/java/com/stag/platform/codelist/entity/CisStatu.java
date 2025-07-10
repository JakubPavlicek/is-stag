package com.stag.platform.codelist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    name = "CIS_STATU",
    schema = "INSTALL2"
)
public class CisStatu {

    @Id
    @Column(
        name = "STATIDNO",
        nullable = false
    )
    private Integer id;

    @Size(max = 70)
    @NotNull
    @Column(
        name = "STAT",
        nullable = false,
        length = 70
    )
    private String stat;

    @NotNull
    @Column(
        name = "PLATNOST_OD",
        nullable = false
    )
    private LocalDate platnostOd;

    @Column(name = "PLATNOST_DO")
    private LocalDate platnostDo;

    @Size(max = 240)
    @Column(
        name = "ENGLISH",
        length = 240
    )
    private String english;

    @Size(max = 1)
    @ColumnDefault("'A'")
    @Column(
        name = "VIZOVA_POVINNOST",
        length = 1
    )
    private String vizovaPovinnost;

    @Size(max = 2)
    @Column(
        name = "ZKRATKA",
        length = 2
    )
    private String zkratka;

    @Size(max = 3)
    @Column(
        name = "ZKRATKA3",
        length = 3
    )
    private String zkratka3;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "OBVYKLY",
        nullable = false,
        length = 1
    )
    private String obvykly;

    @Size(max = 70)
    @Column(
        name = "OBECNY_NAZEV_CZ",
        length = 70
    )
    private String obecnyNazevCz;

    @Size(max = 70)
    @Column(
        name = "OBECNY_NAZEV_AN",
        length = 70
    )
    private String obecnyNazevAn;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "RIZIKOVY",
        nullable = false,
        length = 1
    )
    private String rizikovy;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "JE_V_EU",
        nullable = false,
        length = 1
    )
    private String jeVEu;

}