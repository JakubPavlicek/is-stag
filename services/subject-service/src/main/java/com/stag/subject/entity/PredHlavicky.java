package com.stag.subject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
    name = "PRED_HLAVICKY",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "PRHL_PRAC_FK_I",
            columnList = "PRAC_ZKR"
        ),
        @Index(
            name = "PRHL_PRHL_FK_I",
            columnList = "PRAC_ZKR_PRED, ZKR_PREDM_PRED"
        )
    }
)
public class PredHlavicky {

    @EmbeddedId
    private PredHlavickyId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        {
            @JoinColumn(
                name = "PRAC_ZKR_PRED",
                referencedColumnName = "PRAC_ZKR"
            ),
            @JoinColumn(
                name = "ZKR_PREDM_PRED",
                referencedColumnName = "ZKR_PREDM"
            )
        }
    )
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private PredHlavicky parentPredHlavicky;

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
    @CreationTimestamp
    @Column(
        name = "DATE_OF_INSERT",
        nullable = false,
        updatable = false
    )
    private LocalDate dateOfInsert;

    @UpdateTimestamp
    @Column(name = "DATE_OF_UPDATE")
    private LocalDate dateOfUpdate;

    @Size(max = 3)
    @Column(
        name = "CIS_RADA_SDZ",
        length = 3
    )
    private String cisRadaSdz;

    @Size(max = 255)
    @Column(name = "IDENTIFIKATOR")
    private String identifikator;

}