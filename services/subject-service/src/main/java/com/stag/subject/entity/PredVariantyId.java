package com.stag.subject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PredVariantyId implements Serializable {

    @Serial
    private static final long serialVersionUID = 3701775921980234098L;
    @Size(max = 5)
    @NotNull
    @Column(
        name = "ZKR_PREDM",
        nullable = false,
        length = 5
    )
    private String zkrPredm;

    @Size(max = 6)
    @NotNull
    @Column(
        name = "PRAC_ZKR",
        nullable = false,
        length = 6
    )
    private String pracZkr;

    @Size(max = 4)
    @NotNull
    @Column(
        name = "ROK_VARIANTY",
        nullable = false,
        length = 4
    )
    private String rokVarianty;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PredVariantyId entity = (PredVariantyId) o;
        return Objects.equals(this.zkrPredm, entity.zkrPredm) &&
            Objects.equals(this.pracZkr, entity.pracZkr) &&
            Objects.equals(this.rokVarianty, entity.rokVarianty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zkrPredm, pracZkr, rokVarianty);
    }

}