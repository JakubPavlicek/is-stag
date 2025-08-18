package com.stag.identity.person.mapper;

import com.stag.identity.dto.AddressesDTO;
import com.stag.identity.dto.BankAccountDetailsDTO;
import com.stag.identity.dto.BankAccountsDTO;
import com.stag.identity.dto.EducationDetailsDTO;
import com.stag.identity.dto.EuroBankAccountDetailsDTO;
import com.stag.identity.dto.ForeignHighSchoolDTO;
import com.stag.identity.dto.HighSchoolDTO;
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
