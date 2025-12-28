package com.stag.platform.address.entity;

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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

/// **Street Entity**
///
/// Represents a street within a municipality.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
@Setter
@Entity
@Table(
    name = "CIS_ULIC",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "CIUL_COBC_FK_I",
            columnList = "OBEC_IDNO"
        ),
        @Index(
            name = "CIUL_NAZEV_I",
            columnList = "NAZEV"
        )
    }
)
public class Street {

    @Id
    @Column(
        name = "ULICIDNO",
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
        name = "OBEC_IDNO",
        nullable = false
    )
    private Municipality municipality;

    @Size(max = 4000)
    @NotNull
    @Column(
        name = "NAZEV",
        nullable = false,
        length = 4000
    )
    private String name;

    @Column(name = "VZNIK_DNE")
    private LocalDate creationDate;

    @Column(name = "ZANIK_DNE")
    private LocalDate dissolutionDate;

}
