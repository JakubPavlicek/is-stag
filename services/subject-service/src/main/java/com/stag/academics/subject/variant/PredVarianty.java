package com.stag.academics.subject.variant;

import com.stag.academics.subject.header.PredHlavicky;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
    name = "PRED_VARIANTY",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "PRVA_TXT_NAZEV_EN",
            columnList = "AN_NAZEV"
        ),
        @Index(
            name = "PRVA_PRHL_FK_I",
            columnList = "PRAC_ZKR, ZKR_PREDM"
        ),
        @Index(
            name = "PRVA_TXT_NAZEV_CS",
            columnList = "CZ_NAZEV"
        ),
        @Index(
            name = "PRVA_CIHT_VAZK_FK_I",
            columnList = "TYHOIDNO_ZKZP"
        ),
        @Index(
            name = "PRVA_CIHT_VAZP_FK_I",
            columnList = "TYHOIDNO_ZPPZK"
        )
    }
)
public class PredVarianty {

    @EmbeddedId
    private PredVariantyId id;

    @MapsId("id")
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumns(
        {
            @JoinColumn(
                name = "PRAC_ZKR",
                referencedColumnName = "PRAC_ZKR",
                nullable = false
            ),
            @JoinColumn(
                name = "ZKR_PREDM",
                referencedColumnName = "ZKR_PREDM",
                nullable = false
            )
        }
    )
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private PredHlavicky predHlavicky;

    @NotNull
    @Column(
        name = "CRD",
        nullable = false
    )
    private Short crd;

    @Size(max = 2)
    @NotNull
    @Column(
        name = "TYP_ZK",
        nullable = false,
        length = 2
    )
    private String typZk;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "JEDNOTKA_PR",
        nullable = false,
        length = 1
    )
    private String jednotkaPr;

    @NotNull
    @Column(
        name = "JEDNOTEK_PR",
        nullable = false
    )
    private Short jednotekPr;

    @Size(max = 1)
    @Column(
        name = "TYP_PR",
        length = 1
    )
    private String typPr;

    @Size(max = 2)
    @Column(
        name = "TYP_UCEB_PR",
        length = 2
    )
    private String typUcebPr;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "JEDNOTKA_CV",
        nullable = false,
        length = 1
    )
    private String jednotkaCv;

    @NotNull
    @Column(
        name = "JEDNOTEK_CV",
        nullable = false
    )
    private Short jednotekCv;

    @NotNull
    @Column(
        name = "POC_STUD_CV",
        nullable = false
    )
    private Short pocStudCv;

    @Size(max = 2)
    @Column(
        name = "TYP_CV",
        length = 2
    )
    private String typCv;

    @Size(max = 2)
    @Column(
        name = "TYP_UCEB_CV",
        length = 2
    )
    private String typUcebCv;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "JEDNOTKA_SEM",
        nullable = false,
        length = 1
    )
    private String jednotkaSem;

    @NotNull
    @Column(
        name = "JEDNOTEK_SEM",
        nullable = false
    )
    private Short jednotekSem;

    @NotNull
    @Column(
        name = "POC_STUD_SEM",
        nullable = false
    )
    private Short pocStudSem;

    @Size(max = 2)
    @Column(
        name = "TYP_SEM",
        length = 2
    )
    private String typSem;

    @Size(max = 2)
    @Column(
        name = "TYP_UCEB_SEM",
        length = 2
    )
    private String typUcebSem;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "AKRED",
        nullable = false,
        length = 1
    )
    private String akred;

    @Column(name = "SKUP_AKRED")
    private Short skupAkred;

    @Size(max = 255)
    @Column(name = "AN_NAZEV_DLOUHY")
    private String anNazevDlouhy;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "VYUKA_LS",
        nullable = false,
        length = 1
    )
    private String vyukaLs;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "VYUKA_ZS",
        nullable = false,
        length = 1
    )
    private String vyukaZs;

    @Size(max = 1)
    @Column(
        name = "PREDZ_ZA",
        length = 1
    )
    private String predzZa;

    @Size(max = 1)
    @Column(
        name = "HODNOCENI_ZK",
        length = 1
    )
    private String hodnoceniZk;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "MA_VYUKU",
        nullable = false,
        length = 1
    )
    private String maVyuku;

    @Size(max = 1)
    @Column(
        name = "FORMA_ZK",
        length = 1
    )
    private String formaZk;

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

    @Size(max = 40)
    @Column(
        name = "AN_NAZEV",
        length = 40
    )
    private String anNazev;

    @Size(max = 40)
    @Column(
        name = "J4_NAZEV",
        length = 40
    )
    private String j4Nazev;

    @Size(max = 40)
    @Column(
        name = "J3_NAZEV",
        length = 40
    )
    private String j3Nazev;

    @Size(max = 1)
    @Column(
        name = "CZ_VYUC",
        length = 1
    )
    private String czVyuc;

    @Size(max = 1)
    @Column(
        name = "AN_VYUC",
        length = 1
    )
    private String anVyuc;

    @Size(max = 2)
    @Column(
        name = "J3_VYUC",
        length = 2
    )
    private String j3Vyuc;

    @Size(max = 2)
    @Column(
        name = "J4_VYUC",
        length = 2
    )
    private String j4Vyuc;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "VICEZAPIS",
        nullable = false,
        length = 1
    )
    private String vicezapis;

    @NotNull
    @Column(
        name = "POC_TYDNU",
        nullable = false
    )
    private Short pocTydnu;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "DUVOD_DEL",
        nullable = false,
        length = 1
    )
    private String duvodDel;

    @Size(max = 100)
    @Column(
        name = "JINY_DUVOD",
        length = 100
    )
    private String jinyDuvod;

    @Size(max = 4000)
    @Column(
        name = "J4_ANOTACE",
        length = 4000
    )
    private String j4Anotace;

    @Size(max = 4000)
    @Column(
        name = "J3_ANOTACE",
        length = 4000
    )
    private String j3Anotace;

    @Size(max = 4000)
    @Column(
        name = "AN_ANOTACE",
        length = 4000
    )
    private String anAnotace;

    @Size(max = 4000)
    @Column(
        name = "CZ_ANOTACE",
        length = 4000
    )
    private String czAnotace;

    @Size(max = 255)
    @Column(name = "J3_NAZEV_DLOUHY")
    private String j3NazevDlouhy;

    @Size(max = 255)
    @Column(name = "J4_NAZEV_DLOUHY")
    private String j4NazevDlouhy;

    @Size(max = 255)
    @NotNull
    @Column(
        name = "CZ_NAZEV_DLOUHY",
        nullable = false
    )
    private String czNazevDlouhy;

    @Size(max = 40)
    @NotNull
    @Column(
        name = "CZ_NAZEV",
        nullable = false,
        length = 40
    )
    private String czNazev;

    @Size(max = 4000)
    @Column(
        name = "POZNAMKA",
        length = 4000
    )
    private String poznamka;

    @Size(max = 1)
    @Column(
        name = "STRUKT_V",
        length = 1
    )
    private String struktV;

    @Size(max = 1)
    @Column(
        name = "STRUKT_N",
        length = 1
    )
    private String struktN;

    @Size(max = 1)
    @Column(
        name = "DELENI_V",
        length = 1
    )
    private String deleniV;

    @Size(max = 1)
    @Column(
        name = "DELENI_N",
        length = 1
    )
    private String deleniN;

    @Size(max = 1)
    @Column(
        name = "ETAPA_V",
        length = 1
    )
    private String etapaV;

    @Size(max = 1)
    @Column(
        name = "ETAPA_N",
        length = 1
    )
    private String etapaN;

    @Column(name = "INFRA_FIN")
    private Integer infraFin;

    @Column(
        name = "INFRA_V",
        precision = 9,
        scale = 4
    )
    private BigDecimal infraV;

    @Column(name = "INFRA_S")
    private Integer infraS;

    @Column(
        name = "INFRA_N",
        precision = 9,
        scale = 4
    )
    private BigDecimal infraN;

    @Column(
        name = "KOEF_V",
        precision = 5,
        scale = 2
    )
    private BigDecimal koefV;

    @Column(
        name = "KOEF_N",
        precision = 5,
        scale = 2
    )
    private BigDecimal koefN;

    @Size(max = 1)
    @NotNull
    @Column(
        name = "DO_PRUMERU",
        nullable = false,
        length = 1
    )
    private String doPrumeru;

    @Size(max = 1)
    @Column(
        name = "SPLNIT_PODM",
        length = 1
    )
    private String splnitPodm;

    @Size(max = 1)
    @ColumnDefault("'N'")
    @Column(
        name = "INFRA_A_N",
        length = 1
    )
    private String infraAN;

    @Column(name = "POCET_RA_TYPU_PR")
    private Short pocetRaTypuPr;

    @Column(name = "POCET_RA_TYPU_CV")
    private Short pocetRaTypuCv;

    @Column(name = "POCET_RA_TYPU_SEM")
    private Short pocetRaTypuSem;

    @Column(name = "DELENI_PR_PO")
    private Short deleniPrPo;

    @Column(name = "DELENI_CV_PO")
    private Short deleniCvPo;

    @Column(name = "DELENI_SEM_PO")
    private Short deleniSemPo;

    @Size(max = 5)
    @Column(
        name = "UROVEN_VYPOCTENA",
        length = 5
    )
    private String urovenVypoctena;

    @Size(max = 5)
    @Column(
        name = "UROVEN_NASTAVENA",
        length = 5
    )
    private String urovenNastavena;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "ECTS_ZOBRAZIT",
        nullable = false,
        length = 1
    )
    private String ectsZobrazit;

    @Size(max = 100)
    @Column(
        name = "CZ_JAK_CASTO_JE_NABIZEN",
        length = 100
    )
    private String czJakCastoJeNabizen;

    @Size(max = 100)
    @Column(
        name = "AN_JAK_CASTO_JE_NABIZEN",
        length = 100
    )
    private String anJakCastoJeNabizen;

    @Size(max = 100)
    @Column(
        name = "J3_JAK_CASTO_JE_NABIZEN",
        length = 100
    )
    private String j3JakCastoJeNabizen;

    @Size(max = 100)
    @Column(
        name = "J4_JAK_CASTO_JE_NABIZEN",
        length = 100
    )
    private String j4JakCastoJeNabizen;

    @Size(max = 255)
    @Column(name = "IDENTIFIKATOR")
    private String identifikator;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "ECTS_AKREDITACE",
        nullable = false,
        length = 1
    )
    private String ectsAkreditace;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "NABIZET_PRIJEZDY_ECTS",
        nullable = false,
        length = 1
    )
    private String nabizetPrijezdyEcts;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "TISK_NA_DOD_DIPL",
        nullable = false,
        length = 1
    )
    private String tiskNaDodDipl;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "VOLNE_ZAPISOVATELNY",
        nullable = false,
        length = 1
    )
    private String volneZapisovatelny;

    @Size(max = 4000)
    @Column(
        name = "POZNAMKA_VEREJNA",
        length = 4000
    )
    private String poznamkaVerejna;

    @Size(max = 4000)
    @Column(
        name = "STUDIJNI_OPORY",
        length = 4000
    )
    private String studijniOpory;

    @NotNull
    @ColumnDefault("'0'")
    @Column(
        name = "PRAXE_POCET_DNU",
        nullable = false
    )
    private Short praxePocetDnu;

    @Column(name = "HOD_ZA_SEM_KOMB_FORMA")
    private Short hodZaSemKombForma;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'N'")
    @Column(
        name = "NENI_PRO_RIZIKOVE_STUDENTY",
        nullable = false,
        length = 1
    )
    private String neniProRizikoveStudenty;

    @Size(max = 3)
    @NotNull
    @ColumnDefault("'K'")
    @Column(
        name = "JAK_CASTO_JE_NABIZEN",
        nullable = false,
        length = 3
    )
    private String jakCastoJeNabizen;

    @NotNull
    @ColumnDefault("1")
    @Column(
        name = "POCET_AKCI_ZAPIS_NAJEDNOU_CV",
        nullable = false
    )
    private Short pocetAkciZapisNajednouCv;

    @NotNull
    @ColumnDefault("1")
    @Column(
        name = "POCET_AKCI_ZAPIS_NAJEDNOU_PR",
        nullable = false
    )
    private Short pocetAkciZapisNajednouPr;

    @NotNull
    @ColumnDefault("1")
    @Column(
        name = "POCET_AKCI_ZAPIS_NAJEDNOU_SEM",
        nullable = false
    )
    private Short pocetAkciZapisNajednouSem;

    @Size(max = 1)
    @NotNull
    @ColumnDefault("'A'")
    @Column(
        name = "SOUCASTI_SHK",
        nullable = false,
        length = 1
    )
    private String soucastiShk;

    @NotNull
    @Column(
        name = "TYHOIDNO_ZKZP",
        nullable = false
    )
    private Long tyhoidnoZkzp;

    @Column(name = "TYHOIDNO_ZPPZK")
    private Long tyhoidnoZppzk;

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
        PredVarianty that = (PredVarianty) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

}