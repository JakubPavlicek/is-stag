package com.stag.platform.education.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(
    name = "CIS_HODNOCENI_TYP",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "CIHT_CIHO_FK_I",
            columnList = "HODNIDNO_NEVYPLNENO"
        )
    }
)
public class EvaluationType {

    @Id
    @Column(
        name = "TYHOIDNO",
        nullable = false
    )
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(
        name = "CZ_ZKRATKA",
        nullable = false,
        length = 20
    )
    private String abbreviationCz;

    @Size(max = 100)
    @NotNull
    @Column(
        name = "CZ_NAZEV",
        nullable = false,
        length = 100
    )
    private String nameCz;

    @Size(max = 10)
    @Column(
        name = "AN_ZKRATKA",
        length = 10
    )
    private String abbreviationEn;

    @Size(max = 100)
    @Column(
        name = "AN_NAZEV",
        length = 100
    )
    private String nameEn;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "JEDNA_SE_O_ECTS",
        nullable = false,
        length = 1
    )
    private String isEcts;

    @Size(max = 30)
    @NotNull
    @Column(
        name = "OWNER",
        nullable = false,
        length = 30
    )
    private String owner;

    @NotNull
    @CreationTimestamp
    @Column(
        name = "DATE_OF_INSERT",
        nullable = false,
        updatable = false
    )
    private LocalDate dateOfInsert;

    @Size(max = 30)
    @Column(
        name = "UPDATOR",
        length = 30
    )
    private String updator;

    @UpdateTimestamp
    @Column(
        name = "DATE_OF_UPDATE",
        insertable = false
    )
    private LocalDate dateOfUpdate;

    @Size(max = 4)
    @NotNull
    @ColumnDefault("'1970'")
    @Column(
        name = "PLATNY_OD",
        nullable = false,
        length = 4
    )
    private String validFrom;

    @Size(max = 4)
    @NotNull
    @ColumnDefault("'2099'")
    @Column(
        name = "NEPLATNY_OD",
        nullable = false,
        length = 4
    )
    private String invalidFrom;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'ACJVZ'")
    @Column(
        name = "URCENO_PRO",
        nullable = false,
        length = 20
    )
    private String designatedFor;

    @Column(
        name = "NEVYPLNENO_HODNOTA_DO_PRUMERU",
        precision = 5,
        scale = 2
    )
    private BigDecimal unfilledValueForAverage;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
        name = "HODNIDNO_NEVYPLNENO",
        referencedColumnName = "HODNIDNO"
    )
    private Evaluation unfilledEvaluation;

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
        EvaluationType that = (EvaluationType) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }

}