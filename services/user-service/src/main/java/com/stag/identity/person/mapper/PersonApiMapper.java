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

/// **Person API Mapper**
///
/// MapStruct mapper for transforming domain models to API DTOs and vice versa.
/// Bridges internal domain models with external REST API representations, handling field name mappings and structure transformations.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper
public interface PersonApiMapper {

    /// PersonApiMapper Instance
    PersonApiMapper INSTANCE = Mappers.getMapper(PersonApiMapper.class);

    /// Maps profile model to API response DTO.
    PersonResponse toPersonResponse(Profile profile);

    /// Maps addresses model to API response DTO.
    AddressesResponse toAddressesResponse(Addresses addresses);

    /// Maps banking model to API response DTO.
    BankAccountsResponse toBankAccountsResponse(Banking banking);

    /// Maps education model to API response DTO.
    EducationResponse toEducationResponse(Education education);

    /// Maps a bank account model to API DTO with field name transformations.
    @Mapping(target = "holderName", source = "owner")
    @Mapping(target = "holderAddress", source = "address")
    @Mapping(target = "accountNumberPrefix", source = "prefix")
    @Mapping(target = "accountNumberSuffix", source = "suffix")
    BankAccount toBankAccount(Banking.BankAccount account);

    /// Maps a Euro bank account model to API DTO with field name transformations.
    @Mapping(target = "holderName", source = "owner")
    @Mapping(target = "holderAddress", source = "address")
    @Mapping(target = "accountNumberPrefix", source = "prefix")
    @Mapping(target = "accountNumberSuffix", source = "suffix")
    EuroBankAccount toEuroBankAccountDTO(Banking.EuroBankAccount euroBankAccount);

    /// Maps a high school model to API DTO.
    @Mapping(target = "schoolName", source = "name")
    HighSchool toHighSchoolDTO(Education.HighSchool highSchool);

    /// Maps a foreign high school model to API DTO.
    @Mapping(target = "schoolName", source = "name")
    ForeignHighSchool toForeignHighSchoolDTO(Education.ForeignHighSchool foreignHighSchool);

    /// Maps update person API request to internal command with field name transformations.
    @Mapping(target = "bankAccount.prefix", source = "bankAccount.accountNumberPrefix")
    @Mapping(target = "bankAccount.suffix", source = "bankAccount.accountNumberSuffix")
    PersonUpdateCommand toPersonUpdateCommand(UpdatePersonRequest updatePersonRequest);

}
