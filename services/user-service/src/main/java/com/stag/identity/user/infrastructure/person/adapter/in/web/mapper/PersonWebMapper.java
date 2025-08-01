package com.stag.identity.user.infrastructure.person.adapter.in.web.mapper;

import com.stag.identity.user.application.person.dto.PersonAddressesResult;
import com.stag.identity.user.application.person.dto.PersonBankingResult;
import com.stag.identity.user.application.person.dto.PersonBankingResult.BankAccount;
import com.stag.identity.user.application.person.dto.PersonBankingResult.EuroBankAccount;
import com.stag.identity.user.application.person.dto.PersonEducationResult;
import com.stag.identity.user.application.person.dto.PersonEducationResult.ForeignHighSchool;
import com.stag.identity.user.application.person.dto.PersonEducationResult.HighSchool;
import com.stag.identity.user.application.person.dto.PersonProfileResult;
import com.stag.identity.user.infrastructure.dto.AddressesDTO;
import com.stag.identity.user.infrastructure.dto.BankAccountDetailsDTO;
import com.stag.identity.user.infrastructure.dto.BankAccountsDTO;
import com.stag.identity.user.infrastructure.dto.EducationDetailsDTO;
import com.stag.identity.user.infrastructure.dto.EducationDetailsForeignHighSchoolDTO;
import com.stag.identity.user.infrastructure.dto.EducationDetailsHighSchoolDTO;
import com.stag.identity.user.infrastructure.dto.EuroBankAccountDetailsDTO;
import com.stag.identity.user.infrastructure.dto.PersonProfileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class PersonWebMapper {

    public abstract PersonProfileDTO toPersonProfileDTO(PersonProfileResult personProfileResult);

    public abstract AddressesDTO toAddressesDTO(PersonAddressesResult personAddressesResult);

    public BankAccountsDTO toBankAccountsDTO(PersonBankingResult personBanking) {
        return BankAccountsDTO.builder()
                              .account(toBankAccountDTO(personBanking.account()))
                              .euroAccount(toEuroBankAccountDTO(personBanking.euroAccount()))
                              .build();
    }

    private EuroBankAccountDetailsDTO toEuroBankAccountDTO(EuroBankAccount euroBankAccount) {
        return EuroBankAccountDetailsDTO.builder()
                                        .holderName(euroBankAccount.owner())
                                        .holderAddress(euroBankAccount.address())
                                        .bankAccountNumberPrefix(euroBankAccount.prefix())
                                        .bankAccountNumberSuffix(euroBankAccount.suffix())
                                        .bankCode(euroBankAccount.bankCode())
                                        .bankName(euroBankAccount.bankName())
                                        .iban(euroBankAccount.iban())
                                        .currency(euroBankAccount.currency())
                                        .country(euroBankAccount.country())
                                        .swift(euroBankAccount.swift())
                                        .build();
    }

    private BankAccountDetailsDTO toBankAccountDTO(BankAccount account) {
        return BankAccountDetailsDTO.builder()
                                    .holderName(account.owner())
                                    .holderAddress(account.address())
                                    .bankAccountNumberPrefix(account.prefix())
                                    .bankAccountNumberSuffix(account.suffix())
                                    .bankCode(account.bankCode())
                                    .bankName(account.bankName())
                                    .iban(account.iban())
                                    .currency(account.currency())
                                    .build();
    }

    public EducationDetailsDTO toEducationDetailsDTO(PersonEducationResult personEducation) {
        return EducationDetailsDTO.builder()
                                  .highSchool(toEducationDetailsHighSchoolDTO(personEducation.highSchool()))
                                  .foreignHighSchool(toEducationDetailsForeignHighSchoolDTO(personEducation.foreignHighSchool()))
                                  .build();
    }

    @Mapping(source = "name", target = "institutionName")
    @Mapping(source = "fieldOfStudy", target = "fieldOfStudy")
    @Mapping(source = "graduationDate", target = "graduationDate")
    @Mapping(source = "address", target = "address")
    abstract EducationDetailsHighSchoolDTO toEducationDetailsHighSchoolDTO(HighSchool highSchool);

    @Mapping(source = "name", target = "institutionName")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "fieldOfStudy", target = "fieldOfStudy")
    abstract EducationDetailsForeignHighSchoolDTO toEducationDetailsForeignHighSchoolDTO(ForeignHighSchool foreignHighSchool);

}
