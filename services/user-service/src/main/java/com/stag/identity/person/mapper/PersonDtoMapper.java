package com.stag.identity.person.mapper;

import com.stag.identity.dto.AddressesDTO;
import com.stag.identity.dto.BankAccountDetailsDTO;
import com.stag.identity.dto.BankAccountsDTO;
import com.stag.identity.dto.EducationDetailsDTO;
import com.stag.identity.dto.EducationDetailsForeignHighSchoolDTO;
import com.stag.identity.dto.EducationDetailsHighSchoolDTO;
import com.stag.identity.dto.EuroBankAccountDetailsDTO;
import com.stag.identity.dto.PersonProfileDTO;
import com.stag.identity.person.model.PersonAddresses;
import com.stag.identity.person.model.PersonBanking;
import com.stag.identity.person.model.PersonBanking.BankAccount;
import com.stag.identity.person.model.PersonBanking.EuroBankAccount;
import com.stag.identity.person.model.PersonEducation;
import com.stag.identity.person.model.PersonEducation.ForeignHighSchool;
import com.stag.identity.person.model.PersonEducation.HighSchool;
import com.stag.identity.person.model.PersonProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonDtoMapper {

    PersonDtoMapper INSTANCE = Mappers.getMapper(PersonDtoMapper.class);

    PersonProfileDTO toPersonProfileDTO(PersonProfile personProfile);

    AddressesDTO toAddressesDTO(PersonAddresses personAddresses);

    BankAccountsDTO toBankAccountsDTO(PersonBanking personBanking);

    EducationDetailsDTO toEducationDetailsDTO(PersonEducation personEducation);

    @Mapping(source = "owner", target = "holderName")
    @Mapping(source = "address", target = "holderAddress")
    @Mapping(source = "prefix", target = "bankAccountNumberPrefix")
    @Mapping(source = "suffix", target = "bankAccountNumberSuffix")
    @Mapping(source = "bankCode", target = "bankCode")
    @Mapping(source = "bankName", target = "bankName")
    @Mapping(source = "iban", target = "iban")
    @Mapping(source = "currency", target = "currency")
    BankAccountDetailsDTO toBankAccountDTO(BankAccount account);

    @Mapping(source = "owner", target = "holderName")
    @Mapping(source = "address", target = "holderAddress")
    @Mapping(source = "prefix", target = "bankAccountNumberPrefix")
    @Mapping(source = "suffix", target = "bankAccountNumberSuffix")
    @Mapping(source = "bankCode", target = "bankCode")
    @Mapping(source = "bankName", target = "bankName")
    @Mapping(source = "iban", target = "iban")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "swift", target = "swift")
    EuroBankAccountDetailsDTO toEuroBankAccountDTO(EuroBankAccount euroBankAccount);

    @Mapping(source = "name", target = "institutionName")
    @Mapping(source = "fieldOfStudy", target = "fieldOfStudy")
    @Mapping(source = "graduationDate", target = "graduationDate")
    @Mapping(source = "address", target = "address")
    EducationDetailsHighSchoolDTO toEducationDetailsHighSchoolDTO(HighSchool highSchool);

    @Mapping(source = "name", target = "institutionName")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "fieldOfStudy", target = "fieldOfStudy")
    EducationDetailsForeignHighSchoolDTO toEducationDetailsForeignHighSchoolDTO(ForeignHighSchool foreignHighSchool);

}
