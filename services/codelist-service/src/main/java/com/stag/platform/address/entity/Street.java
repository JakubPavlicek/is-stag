package com.stag.platform.address.entity;

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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
    name = "CIS_ULIC",
    schema = "INSTALL2"
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

    @Size(max = 255)
    @NotNull
    @Column(
        name = "NAZEV",
        nullable = false
    )
    private String name;

    @Column(name = "VZNIK_DNE")
    private LocalDate creationDate;

    @Column(name = "ZANIK_DNE")
    private LocalDate dissolutionDate;
}
