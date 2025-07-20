package com.stag.identity.user.mapper;

import com.stag.identity.user.dto.BirthPlaceInternal;
import com.stag.identity.user.dto.CitizenshipInternal;
import com.stag.identity.user.dto.ContactInternal;
import com.stag.identity.user.dto.PersonProfile;
import com.stag.identity.user.dto.PersonProfileInternal;
import com.stag.identity.user.dto.TitlesInternal;
import com.stag.identity.user.entity.Osoba;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    // --- API Mapping Methods ---

    @Mapping(source = "personId", target = "personId")
    @Mapping(source = "personalNumbers", target = "personalNumbers")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "birthSurname", target = "birthSurname")
    @Mapping(source = "contact", target = "contact")
    @Mapping(source = "titles", target = "titles")
    @Mapping(source = "birthNumber", target = "birthNumber")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "birthPlace", target = "birthPlace")
    @Mapping(source = "citizenship", target = "citizenship")
    @Mapping(source = "passportNumber", target = "passportNumber")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "maritalStatus", target = "maritalStatus")
    PersonProfile toPersonProfile(PersonProfileInternal internal);

    // --- Main Mapping Method ---

    @Mapping(source = "osoba.id", target = "personId")
    @Mapping(source = "personalNumbers", target = "personalNumbers")
    @Mapping(source = "osoba.jmeno", target = "firstName")
    @Mapping(source = "osoba.prijmeni", target = "lastName")
    @Mapping(source = "osoba.rodnePrijmeni", target = "birthSurname")
    @Mapping(source = "osoba", target = "contact")
    @Mapping(source = "osoba", target = "titles")
    @Mapping(source = "osoba.rodCislo", target = "birthNumber")
    @Mapping(source = "osoba.datumNaroz", target = "birthDate")
    @Mapping(source = "osoba", target = "birthPlace")
    @Mapping(source = "osoba", target = "citizenship")
    @Mapping(source = "osoba.cisloPasu", target = "passportNumber")
    @Mapping(source = "osoba.pohlavi", target = "gender")
    @Mapping(source = "osoba.stav", target = "maritalStatus")
    PersonProfileInternal toPersonProfileInternal(Osoba osoba, List<String> personalNumbers);

    // --- Delegate Mappers for Nested Objects ---

    @Mapping(source = "email", target = "email")
    @Mapping(source = "telefon", target = "phone")
    @Mapping(source = "mobil", target = "mobile")
    ContactInternal toContactInternal(Osoba osoba);

    @Mapping(source = "titulPred", target = "prefix")
    @Mapping(source = "titulZa", target = "suffix")
    TitlesInternal toTitlesInternal(Osoba osoba);

//    @Mapping(source = "mistoNar", target = "city")
//    @Mapping(source = "statNar", target = "country")
//    BirthPlaceInternal toBirthPlaceInternal(Osoba osoba);
//
//    @Mapping(source = "statniPrislusnost", target = "country")
//    @Mapping(source = "kvalifikator", target = "qualifier")
//    CitizenshipInternal toCitizenshipInternal(Osoba osoba);

}
