package com.stag.academics.student.entity;

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
public class StudentEnrollmentId implements Serializable {

    @Serial
    private static final long serialVersionUID = -8009986051580341960L;

    @Size(max = 10)
    @NotNull
    @Column(
        name = "OS_CISLO",
        nullable = false,
        length = 10
    )
    private String studentId;

    @Size(max = 4)
    @NotNull
    @Column(
        name = "ROK_PLATNOSTI",
        nullable = false,
        length = 4
    )
    private String yearOfValidity;

    @NotNull
    @Column(
        name = "STPLIDNO",
        nullable = false
    )
    private Long studyPlanId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        StudentEnrollmentId entity = (StudentEnrollmentId) o;
        return Objects.equals(this.studyPlanId, entity.studyPlanId) &&
            Objects.equals(this.yearOfValidity, entity.yearOfValidity) &&
            Objects.equals(this.studentId, entity.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studyPlanId, yearOfValidity, studentId);
    }

}