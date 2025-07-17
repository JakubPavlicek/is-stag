package com.stag.academics.subject.header;

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
public class PredHlavickaId implements Serializable {

    @Serial
    private static final long serialVersionUID = -6314439891527470641L;

    @Size(max = 6)
    @NotNull
    @Column(
        name = "PRAC_ZKR",
        nullable = false,
        length = 6
    )
    private String pracZkr;

    @Size(max = 5)
    @NotNull
    @Column(
        name = "ZKR_PREDM",
        nullable = false,
        length = 5
    )
    private String zkrPredm;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PredHlavickaId entity = (PredHlavickaId) o;
        return Objects.equals(this.zkrPredm, entity.zkrPredm) &&
            Objects.equals(this.pracZkr, entity.pracZkr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zkrPredm, pracZkr);
    }

}