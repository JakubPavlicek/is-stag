package com.stag.platform.education.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "CIS_OBORY_SS",
    schema = "INSTALL2"
)
public class HighSchoolFieldOfStudy {

    @Id
    @Size(max = 10)
    @Column(
        name = "CIS_OBORU",
        nullable = false,
        length = 10
    )
    private String id;

    @Size(max = 240)
    @NotNull
    @Column(
        name = "NAZEV",
        nullable = false,
        length = 240
    )
    private String name;

}
