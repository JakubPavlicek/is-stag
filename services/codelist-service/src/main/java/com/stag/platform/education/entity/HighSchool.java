package com.stag.platform.education.entity;

import com.stag.platform.address.entity.Municipality;
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

import java.time.LocalDate;

/// **High School Entity**
///
/// Represents a secondary school (střední škola) with address and validity information.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
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
public class HighSchool {

    @Id
    @Size(max = 10)
    @Column(
        name = "IZO",
        nullable = false,
        length = 10
    )
    private String id;

    @Size(max = 150)
    @Column(
        name = "NAZEV",
        length = 150
    )
    private String name;

    @Size(max = 3)
    @Column(
        name = "TYP_SS",
        length = 3
    )
    private String type;

    @Size(max = 255)
    @Column(name = "MESTO")
    private String city;

    @Size(max = 48)
    @Column(
        name = "ULICE",
        length = 48
    )
    private String street;

    @Size(max = 9)
    @Column(
        name = "CISLO_ULICE",
        length = 9
    )
    private String streetNumber;

    @Size(max = 5)
    @Column(
        name = "PSC",
        length = 5
    )
    private String zipCode;

    @Size(max = 1)
    @Column(
        name = "TYP_PR",
        length = 1
    )
    private String typeForAdmission;

    @NotNull
    @Column(
        name = "PLATNOST_OD",
        nullable = false
    )
    private LocalDate validFrom;

    @Column(name = "PLATNOST_DO")
    private LocalDate validTo;

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
    private String schoolAdministrationIzo;

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
    @JoinColumn(name = "OBEC_IDNO")
    private Municipality municipality;

}