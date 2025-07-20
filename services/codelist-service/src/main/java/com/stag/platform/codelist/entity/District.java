package com.stag.platform.codelist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
    name = "CIS_OKRESU",
    schema = "INSTALL2"
)
public class District {

    @Id
    @Column(
        name = "OKRESIDNO",
        nullable = false
    )
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(
        name = "OKRES",
        nullable = false,
        length = 100
    )
    private String name;

    @Size(max = 6)
    @NotNull
    @Column(
        name = "NUTS4",
        nullable = false,
        length = 6
    )
    private String nuts4;

    @Size(max = 16)
    @NotNull
    @Column(
        name = "ZKRATKA",
        nullable = false,
        length = 16
    )
    private String abbreviation;

    @NotNull
    @ColumnDefault("1")
    @Column(
        name = "STAV",
        nullable = false
    )
    private Boolean status = false;

    @Column(name = "VZNIK_DNE")
    private LocalDate creationDate;

    @Size(max = 254)
    @Column(
        name = "VZNIK_INFO",
        length = 254
    )
    private String creationInfo;

    @Column(name = "ZANIK_DNE")
    private LocalDate dissolutionDate;

    @Size(max = 254)
    @Column(
        name = "ZANIK_INFO",
        length = 254
    )
    private String dissolutionInfo;

    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
        name = "KRAJ_KOD",
        nullable = false
    )
    private Region regionCode;

}