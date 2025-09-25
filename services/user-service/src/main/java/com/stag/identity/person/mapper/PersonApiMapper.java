package com.stag.identity.person.mapper;

import com.stag.identity.api.dto.AddressesResponse;
import com.stag.identity.api.dto.BankAccount;
import com.stag.identity.api.dto.BankAccountsResponse;
import com.stag.identity.api.dto.EducationResponse;
import com.stag.identity.api.dto.EuroBankAccount;
import com.stag.identity.api.dto.ForeignHighSchool;
import com.stag.identity.api.dto.HighSchool;
import com.stag.identity.api.dto.PersonResponse;
import com.stag.identity.api.dto.UpdatePersonRequest;
import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.model.Banking;
import com.stag.identity.person.model.Education;
import com.stag.identity.person.model.Profile;
import com.stag.identity.person.service.dto.PersonUpdateCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonApiMapper {

    PersonApiMapper INSTANCE = Mappers.getMapper(PersonApiMapper.class);

    PersonResponse toPersonResponse(Profile profile);

    AddressesResponse toAddressesResponse(Addresses addresses);

    BankAccountsResponse toBankAccountsResponse(Banking banking);

    EducationResponse toEducationResponse(Education education);

    @Mapping(target = "holderName", source = "owner")
    @Mapping(target = "holderAddress", source = "address")
    @Mapping(target = "accountNumberPrefix", source = "prefix")
    @Mapping(target = "accountNumberSuffix", source = "suffix")
    BankAccount toBankAccount(Banking.BankAccount account);

    @Mapping(target = "holderName", source = "owner")
    @Mapping(target = "holderAddress", source = "address")
    @Mapping(target = "accountNumberPrefix", source = "prefix")
    @Mapping(target = "accountNumberSuffix", source = "suffix")
    EuroBankAccount toEuroBankAccountDTO(Banking.EuroBankAccount euroBankAccount);

    @Mapping(target = "schoolName", source = "name")
    HighSchool toHighSchoolDTO(Education.HighSchool highSchool);

    @Mapping(target = "schoolName", source = "name")
    ForeignHighSchool toForeignHighSchoolDTO(Education.ForeignHighSchool foreignHighSchool);

    @Mapping(target = "bankAccount.prefix", source = "bankAccount.accountNumberPrefix")
    @Mapping(target = "bankAccount.suffix", source = "bankAccount.accountNumberSuffix")
    PersonUpdateCommand toPersonUpdateCommand(UpdatePersonRequest updatePersonRequest);

}
