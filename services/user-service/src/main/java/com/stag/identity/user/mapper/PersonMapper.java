package com.stag.identity.user.mapper;

import com.stag.identity.user.dto.ContactInternal;
import com.stag.identity.user.dto.PersonProfile;
import com.stag.identity.user.dto.PersonProfileInternal;
import com.stag.identity.user.dto.TitlesInternal;
import com.stag.identity.user.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

    @Mapping(source = "person.id", target = "personId")
    @Mapping(source = "personalNumbers", target = "personalNumbers")
    @Mapping(source = "person.name", target = "firstName")
    @Mapping(source = "person.surname", target = "lastName")
    @Mapping(source = "person.birthSurname", target = "birthSurname")
    @Mapping(source = "person", target = "contact")
    @Mapping(source = "person", target = "titles")
    @Mapping(source = "person.birthNumber", target = "birthNumber")
    @Mapping(source = "person.birthDate", target = "birthDate")
    @Mapping(source = "person", target = "birthPlace")
    @Mapping(source = "person", target = "citizenship")
    @Mapping(source = "person.passportNumber", target = "passportNumber")
    @Mapping(source = "person.gender", target = "gender")
    @Mapping(source = "person.maritalStatus", target = "maritalStatus")
    PersonProfileInternal toPersonProfileInternal(Person person, List<String> personalNumbers);

    // --- Delegate Mappers for Nested Objects ---

    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "mobile", target = "mobile")
    ContactInternal toContactInternal(Person person);

    @Mapping(source = "titlePrefix", target = "prefix")
    @Mapping(source = "titleSuffix", target = "suffix")
    TitlesInternal toTitlesInternal(Person person);

//    @Mapping(source = "mistoNar", target = "city")
//    @Mapping(source = "statNar", target = "country")
//    BirthPlaceInternal toBirthPlaceInternal(Person person);
//
//    @Mapping(source = "statniPrislusnost", target = "country")
//    @Mapping(source = "kvalifikator", target = "qualifier")
//    CitizenshipInternal toCitizenshipInternal(Person person);

}
