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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

/// **Municipality Part Entity**
///
/// Represents a part/district of a municipality (část obce).
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "CIS_CASTI_OBCE",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "CCOB_COBC_FK_I",
            columnList = "OBEC_IDNO"
        )
    }
)
public class MunicipalityPart {

    @Id
    @Column(
        name = "CCOBIDNO",
        nullable = false
    )
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(
        name = "NAZEV",
        nullable = false
    )
    private String name;

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
    private Boolean status;

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

}