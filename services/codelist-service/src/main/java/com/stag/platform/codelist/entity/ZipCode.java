package com.stag.platform.codelist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "CIS_PSC",
    schema = "INSTALL2"
)
public class ZipCode {

    @Id
    @Size(max = 5)
    @Column(
        name = "PSC",
        nullable = false,
        length = 5
    )
    private String zipCodeId;

    @Size(max = 50)
    @NotNull
    @Column(
        name = "POSTA",
        nullable = false,
        length = 50
    )
    private String postOffice;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "AKTUALNI",
        nullable = false,
        length = 1
    )
    private String isCurrent;

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
    private Boolean signOfExistence;

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