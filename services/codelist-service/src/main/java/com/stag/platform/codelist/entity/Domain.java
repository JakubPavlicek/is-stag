package com.stag.platform.codelist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "CIS_DOMEN",
    schema = "INSTALL2"
)
public class Domain {

    @Id
    @Size(max = 100)
    @Column(
        name = "RV_DOMAIN",
        nullable = false,
        length = 100
    )
    private String domainId;

    @Size(max = 4000)
    @Column(
        name = "POPIS",
        length = 4000
    )
    private String description;

}