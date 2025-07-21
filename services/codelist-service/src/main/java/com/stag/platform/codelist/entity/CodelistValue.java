package com.stag.platform.codelist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "CG_REF_CODES",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "X_CG_REF_CODES_1",
            columnList = "RV_DOMAIN, RV_LOW_VALUE",
            unique = true
        ),
        @Index(
            name = "CRC1_CISD_FK_I",
            columnList = "RV_DOMAIN"
        )
    }
)
public class CodelistValue {

    @EmbeddedId
    private CodelistValueId id;

    @MapsId("domain")
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
        name = "RV_DOMAIN",
        nullable = false
    )
    private Domain domain;

    @Size(max = 240)
    @Column(
        name = "RV_HIGH_VALUE",
        length = 240
    )
    private String highValue;

    @Size(max = 240)
    @Column(
        name = "RV_ABBREVIATION",
        length = 240
    )
    private String abbreviation;

    @Size(max = 240)
    @Column(
        name = "RV_MEANING",
        length = 240
    )
    private String meaningCz;

    @Size(max = 10)
    @ColumnDefault("'CG'")
    @Column(
        name = "RV_TYPE",
        length = 10
    )
    private String type;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "DELETE_IND",
        nullable = false,
        length = 1
    )
    private String isDeleted;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "UPDATE_IND",
        nullable = false,
        length = 1
    )
    private String isUpdated;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "GLOBAL",
        nullable = false,
        length = 1
    )
    private String isGlobal;

    @Size(max = 240)
    @Column(
        name = "POM_HODNOTA",
        length = 240
    )
    private String tempValue;

    @ColumnDefault("NULL")
    @Column(name = "PORADI")
    private Short order;

    @Size(max = 240)
    @Column(
        name = "ENGLISH",
        length = 240
    )
    private String meaningEn;

    @Size(max = 240)
    @Column(
        name = "POM_HODNOTA2",
        length = 240
    )
    private String tempValue2;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "PLATNA",
        nullable = false,
        length = 1
    )
    private String isValid;

    @Size(max = 240)
    @Column(
        name = "DALSI_JAZYK",
        length = 240
    )
    private String anotherLanguage;

    @Size(max = 30)
    @NotNull
    @Column(
        name = "OWNER",
        nullable = false,
        length = 30
    )
    private String owner;

    @Size(max = 30)
    @Column(
        name = "UPDATOR",
        length = 30
    )
    private String updator;

    @NotNull
    @Column(
        name = "DATE_OF_INSERT",
        nullable = false
    )
    private LocalDate insertedAt;

    @Column(name = "DATE_OF_UPDATE")
    private LocalDate updatedAt;

    @Size(max = 1000)
    @Column(
        name = "POM_HODNOTA3",
        length = 1000
    )
    private String tempValue3;

    @Size(max = 1000)
    @Column(
        name = "POM_HODNOTA4",
        length = 1000
    )
    private String tempValue4;

}