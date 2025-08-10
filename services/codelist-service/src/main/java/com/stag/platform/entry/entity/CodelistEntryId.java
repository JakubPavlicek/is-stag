package com.stag.platform.entry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class CodelistEntryId implements Serializable {

    @Serial
    private static final long serialVersionUID = -4080125746614352067L;

    @Size(max = 100)
    @NotNull
    @Column(
        name = "RV_DOMAIN",
        nullable = false,
        length = 100
    )
    private String domain;

    @Size(max = 240)
    @NotNull
    @Column(
        name = "RV_LOW_VALUE",
        nullable = false,
        length = 240
    )
    private String lowValue;

    @Size(max = 3)
    @NotNull
    @Builder.Default
    @ColumnDefault("'STA'")
    @Column(
        name = "SUBSYSTEM_CD",
        nullable = false,
        length = 3
    )
    private String subsystemCode = "STA";

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        CodelistEntryId entity = (CodelistEntryId) o;
        return Objects.equals(this.domain, entity.domain) &&
            Objects.equals(this.lowValue, entity.lowValue) &&
            Objects.equals(this.subsystemCode, entity.subsystemCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, lowValue, subsystemCode);
    }

}