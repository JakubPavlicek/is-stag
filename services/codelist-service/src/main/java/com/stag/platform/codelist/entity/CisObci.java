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
    name = "CIS_OBCI",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "COBC_COKR_FK_I",
            columnList = "OKRESIDNO"
        )
    }
)
public class CisObci {

    @Id
    @Column(
        name = "OBEC_IDNO",
        nullable = false
    )
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(
        name = "NAZEV",
        nullable = false
    )
    private String nazev;

    @Size(max = 12)
    @NotNull
    @Column(
        name = "NUTS5",
        nullable = false,
        length = 12
    )
    private String nuts5;

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

    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
        name = "OKRESIDNO",
        nullable = false
    )
    private CisOkresu okresidno;

}