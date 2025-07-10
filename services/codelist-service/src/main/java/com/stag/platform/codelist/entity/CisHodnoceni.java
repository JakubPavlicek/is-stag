package com.stag.platform.codelist.entity;

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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(
    name = "CIS_HODNOCENI",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "CIHO_CIHT_FK_I",
            columnList = "TYHOIDNO"
        ),
        @Index(
            name = "CIHO_CIHO_CZZKR",
            columnList = "CZ_ZKRATKA"
        )
    }
)
public class CisHodnoceni {

    @Id
    @Column(
        name = "HODNIDNO",
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
        name = "TYHOIDNO",
        nullable = false,
        referencedColumnName = "TYHOIDNO"
    )
    private CisHodnoceniTyp tyhoidno;

    @Size(max = 3)
    @NotNull
    @Column(
        name = "CZ_ZKRATKA",
        nullable = false,
        length = 3
    )
    private String czZkratka;

    @Size(max = 100)
    @NotNull
    @Column(
        name = "CZ_NAZEV",
        nullable = false,
        length = 100
    )
    private String czNazev;

    @Size(max = 10)
    @Column(
        name = "AN_ZKRATKA",
        length = 10
    )
    private String anZkratka;

    @Size(max = 100)
    @Column(
        name = "AN_NAZEV",
        length = 100
    )
    private String anNazev;

    @Size(max = 255)
    @Column(name = "CZ_UROVEN_ZNALOSTI")
    private String czUrovenZnalosti;

    @Size(max = 255)
    @Column(name = "AN_UROVEN_ZNALOSTI")
    private String anUrovenZnalosti;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "JE_TO_USPECH",
        nullable = false,
        length = 1
    )
    private String jeToUspech;

    @NotNull
    @ColumnDefault("99")
    @Column(
        name = "PORADI",
        nullable = false
    )
    private Short poradi;

    @NotNull
    @ColumnDefault("99")
    @Column(
        name = "HODNOTA_DO_PRUMERU",
        nullable = false,
        precision = 5,
        scale = 2
    )
    private BigDecimal hodnotaDoPrumeru;

    @Size(max = 2)
    @Column(
        name = "ECTS_EKVIVALENT",
        length = 2
    )
    private String ectsEkvivalent;

    @Size(max = 30)
    @NotNull
    @Column(
        name = "OWNER",
        nullable = false,
        length = 30
    )
    private String owner;

    @NotNull
    @Column(
        name = "DATE_OF_INSERT",
        nullable = false
    )
    private LocalDate dateOfInsert;

    @Size(max = 30)
    @Column(
        name = "UPDATOR",
        length = 30
    )
    private String updator;

    @Column(name = "DATE_OF_UPDATE")
    private LocalDate dateOfUpdate;

    @Size(max = 4)
    @NotNull
    @ColumnDefault("'1970'")
    @Column(
        name = "PLATNY_OD",
        nullable = false,
        length = 4
    )
    private String platnyOd;

    @Size(max = 4)
    @NotNull
    @ColumnDefault("'2099'")
    @Column(
        name = "NEPLATNY_OD",
        nullable = false,
        length = 4
    )
    private String neplatnyOd;

    @Column(name = "BODU_OD")
    private Short boduOd;

    @Column(name = "BODU_DO")
    private Short boduDo;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "DO_PRUMERU",
        nullable = false,
        length = 1
    )
    private String doPrumeru;

    @Size(max = 1000)
    @Column(
        name = "AN_NAZEV_DOKUMENTY",
        length = 1000
    )
    private String anNazevDokumenty;

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
        CisHodnoceni that = (CisHodnoceni) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }

}