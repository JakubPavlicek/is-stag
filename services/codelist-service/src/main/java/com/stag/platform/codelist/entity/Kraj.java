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
    name = "KRAJE",
    schema = "INSTALL2"
)
public class Kraj {

    @Id
    @Column(
        name = "KRAJ_KOD",
        nullable = false
    )
    private Short id;

    @Size(max = 5)
    @NotNull
    @Column(
        name = "NUTS3",
        nullable = false,
        length = 5
    )
    private String nuts3;

    @Size(max = 32)
    @NotNull
    @Column(
        name = "NAZEV",
        nullable = false,
        length = 32
    )
    private String nazev;

    @Size(max = 16)
    @NotNull
    @Column(
        name = "ZKRATKA",
        nullable = false,
        length = 16
    )
    private String zkratka;

    @NotNull
    @ColumnDefault("1")
    @Column(
        name = "STAV",
        nullable = false
    )
    private Boolean stav = false;

    @Column(name = "VZNIK_DNE")
    private LocalDate vznikDne;

    @Size(max = 254)
    @Column(
        name = "VZNIK_INFO",
        length = 254
    )
    private String vznikInfo;

    @Column(name = "ZANIK_DNE")
    private LocalDate zanikDne;

    @Size(max = 254)
    @Column(
        name = "ZANIK_INFO",
        length = 254
    )
    private String zanikInfo;

}