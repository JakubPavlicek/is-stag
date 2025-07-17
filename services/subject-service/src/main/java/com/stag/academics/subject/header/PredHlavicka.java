package com.stag.academics.subject.header;

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
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

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
public class PredHlavicka {

    @EmbeddedId
    private PredHlavickaId id;

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
    private PredHlavicka parentPredHlavicka;

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
    @Column(
        name = "DATE_OF_UPDATE",
        insertable = false
    )
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer()
                                                                                     .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                                              .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        PredHlavicka that = (PredHlavicka) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

}