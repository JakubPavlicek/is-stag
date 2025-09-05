package com.stag.identity.person.mapper;

import com.stag.identity.dto.AddressesDTO;
import com.stag.identity.dto.BankAccountDetailsDTO;
import com.stag.identity.dto.BankAccountsDTO;
import com.stag.identity.dto.EducationDetailsDTO;
import com.stag.identity.dto.EuroBankAccountDetailsDTO;
import com.stag.identity.dto.ForeignHighSchoolDTO;
import com.stag.identity.dto.HighSchoolDTO;
import com.stag.identity.dto.PersonProfileDTO;
import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.model.Banking;
import com.stag.identity.person.model.Banking.BankAccount;
import com.stag.identity.person.model.Banking.EuroBankAccount;
import com.stag.identity.person.model.Education;
import com.stag.identity.person.model.Education.ForeignHighSchool;
import com.stag.identity.person.model.Education.HighSchool;
import com.stag.identity.person.model.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonApiMapper {

    PersonApiMapper INSTANCE = Mappers.getMapper(PersonApiMapper.class);

    PersonProfileDTO toPersonProfileDTO(Profile profile);

    AddressesDTO toAddressesDTO(Addresses addresses);

    BankAccountsDTO toBankAccountsDTO(Banking banking);

    EducationDetailsDTO toEducationDetailsDTO(Education education);

    @Mapping(target = "holderName", source = "owner")
    @Mapping(target = "holderAddress", source = "address")
    @Mapping(target = "accountNumberPrefix", source = "prefix")
    @Mapping(target = "accountNumberSuffix", source = "suffix")
    BankAccountDetailsDTO toBankAccountDTO(BankAccount account);

    @Mapping(target = "holderName", source = "owner")
    @Mapping(target = "holderAddress", source = "address")
    @Mapping(target = "accountNumberPrefix", source = "prefix")
    @Mapping(target = "accountNumberSuffix", source = "suffix")
    EuroBankAccountDetailsDTO toEuroBankAccountDTO(EuroBankAccount euroBankAccount);

    @Mapping(target = "schoolName", source = "name")
    HighSchoolDTO toHighSchoolDTO(HighSchool highSchool);

    @Mapping(target = "schoolName", source = "name")
    ForeignHighSchoolDTO toForeignHighSchoolDTO(ForeignHighSchool foreignHighSchool);

}
