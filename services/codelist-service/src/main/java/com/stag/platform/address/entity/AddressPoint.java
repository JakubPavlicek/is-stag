package com.stag.platform.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ObjectPath;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyValue;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Indexed(index = "idx_address_point")
@Entity
@Table(
    name = "CIS_ADRESNICH_MIST",
    schema = "INSTALL2",
    indexes = {
        @Index(
            name = "ADMI_FK_ULICIDNO",
            columnList = "ULICIDNO"
        ),
        @Index(
            name = "ADMI_FK_OBECIDNO",
            columnList = "OBEC_IDNO"
        ),
        @Index(
            name = "ADMI_FK_CCOB_IDNO",
            columnList = "CCOB_IDNO"
        ),
        @Index(
            name = "ADMI_CISLO_DOMOVNI_I",
            columnList = "CISLO_DOMOVNI"
        ),
        @Index(
            name = "ADMI_CISLO_ORIENTACNI_I",
            columnList = "CISLO_ORIENTACNI"
        ),
        @Index(
            name = "ADMI_PSC_I",
            columnList = "PSC"
        )
    }
)
public class AddressPoint {

    @Id
    @Column(
        name = "ADMIIDNO",
        nullable = false
    )
    private Long id;

    @Column(
        name = "ULICIDNO",
        insertable = false,
        updatable = false
    )
    private Long streetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ULICIDNO")
    private Street street;

    @Column(
        name = "OBEC_IDNO",
        insertable = false,
        updatable = false
    )
    private Long municipalityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OBEC_IDNO")
    private Municipality municipality;

    @Column(
        name = "CCOB_IDNO",
        insertable = false,
        updatable = false
    )
    private Long municipalityPartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CCOB_IDNO")
    private MunicipalityPart municipalityPart;

    @Size(max = 10)
    @Column(
        name = "TYP_SO",
        length = 10
    )
    private String buildingType;

    @Size(max = 20)
    @Column(
        name = "CISLO_DOMOVNI",
        length = 20
    )
    private String houseNumber;

    @Size(max = 20)
    @Column(
        name = "CISLO_ORIENTACNI",
        length = 20
    )
    private String orientationNumber;

    @Size(max = 20)
    @Column(
        name = "CISLO_ORIENTACNI_PISMENO",
        length = 20
    )
    private String orientationNumberLetter;

    @Size(max = 5)
    @NotNull
    @Column(
        name = "PSC",
        nullable = false,
        length = 5
    )
    private String zipCode;

    @Column(name = "VZNIK_DNE")
    private LocalDate creationDate;

    @Column(name = "ZANIK_DNE")
    private LocalDate dissolutionDate;

    @Column(
        name = "GPS_X",
        precision = 18,
        scale = 15
    )
    private BigDecimal gpsX;

    @Column(
        name = "GPS_Y",
        precision = 18,
        scale = 15
    )
    private BigDecimal gpsY;

    @Transient
    @FullTextField(analyzer = "czech")
    @IndexingDependency(derivedFrom = @ObjectPath({
        @PropertyValue(propertyName = AddressPoint_.STREET),
        @PropertyValue(propertyName = AddressPoint_.HOUSE_NUMBER),
        @PropertyValue(propertyName = AddressPoint_.ORIENTATION_NUMBER),
        @PropertyValue(propertyName = AddressPoint_.ORIENTATION_NUMBER_LETTER),
        @PropertyValue(propertyName = AddressPoint_.MUNICIPALITY_PART),
        @PropertyValue(propertyName = AddressPoint_.MUNICIPALITY),
        @PropertyValue(propertyName = AddressPoint_.ZIP_CODE)
    }))
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();

        // Street name (if available)
        if (street != null && street.getName() != null) {
            sb.append(street.getName()).append(" ");
        }

        // House number
        if (houseNumber != null) {
            sb.append(houseNumber);
        }

        // Orientation number (if available)
        if (orientationNumber != null) {
            sb.append("/").append(orientationNumber);
            if (orientationNumberLetter != null) {
                sb.append(orientationNumberLetter);
            }
        }

        // Comma + municipality part (optional)
        if (municipalityPart.getName() != null) {
            sb.append(", ").append(municipalityPart.getName());
        }

        // Comma + municipality
        if (municipality.getName() != null) {
            sb.append(", ").append(municipality.getName());
        }

        // ZIP code at the end
        if (zipCode != null) {
            sb.append(", ").append(zipCode);
        }

        return sb.toString().trim();
    }

}
