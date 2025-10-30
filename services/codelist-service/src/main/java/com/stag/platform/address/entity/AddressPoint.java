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

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
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

    private static final String POSTAL_NUMBER = "č.p.";
    private static final String REGISTRATION_NUMBER = "ev. č. ";

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

    /// This method is a 1:1 translation of the Oracle function FN_ADRESNI_MISTO_ADRESA.
    /// It formats the address for display and full-text search indexing.
    @Transient
    public String getFullAddress() {
        String formattedNumber = getFormattedNumber();
        StringBuilder addressBuilder = new StringBuilder();

        if (street != null) {
            // Format: Street number, Municipality - MunicipalityPart
            addressBuilder.append(street.getName())
                          .append(" ")
                          .append(formattedNumber)
                          .append(", ")
                          .append(getFormattedMunicipality());
        } else {
            // Format: Municipality - MunicipalityPart number
            addressBuilder.append(getFormattedMunicipality())
                          .append(" ")
                          .append(formattedNumber);
        }

        // Add the ZIP code at the end
        addressBuilder.append(", ")
                      .append(zipCode);

        return addressBuilder.toString();
    }

    private String getFormattedNumber() {
        String prefix = POSTAL_NUMBER.equals(buildingType) ? "" : REGISTRATION_NUMBER;

        StringBuilder number = new StringBuilder(houseNumber);

        if (orientationNumber != null) {
            number.append("/").append(orientationNumber);
            if (orientationNumberLetter != null) {
                number.append(orientationNumberLetter);
            }
        }

        return prefix + number;
    }

    private String getFormattedMunicipality() {
        StringBuilder municipalityBuilder = new StringBuilder();
        municipalityBuilder.append(municipality.getName());

        // If the municipality part name differs from the municipality name, add it to the address
        if (!municipality.getName().equals(municipalityPart.getName())) {
            municipalityBuilder.append(" - ").append(municipalityPart.getName());
        }

        return municipalityBuilder.toString();
    }

}
