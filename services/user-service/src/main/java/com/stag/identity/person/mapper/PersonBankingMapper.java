package com.stag.identity.person.mapper;

import com.stag.identity.person.model.PersonBanking;
import com.stag.identity.person.model.PersonBanking.BankAccount;
import com.stag.identity.person.model.PersonBanking.EuroBankAccount;
import com.stag.identity.person.repository.projection.PersonBankProjection;
import com.stag.identity.person.service.data.PersonBankingData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { Qualifiers.class })
public interface PersonBankingMapper {

    PersonBankingMapper INSTANCE = Mappers.getMapper(PersonBankingMapper.class);

    @Mapping(target = "account", source = "personBank")
    @Mapping(target = "euroAccount", source = "personBank")
    PersonBanking toPersonBanking(
        PersonBankProjection personBank,
        @Context PersonBankingData data
    );

    @Mapping(target = "owner", source = "personBank.accountOwner")
    @Mapping(target = "address", source = "personBank.accountAddress")
    @Mapping(target = "prefix", source = "personBank.accountPrefix")
    @Mapping(target = "suffix", source = "personBank.accountSuffix")
    @Mapping(target = "bankCode", source = "personBank.accountBank")
    @Mapping(target = "bankName", source = "personBank.accountBank", qualifiedByName = "lookupBankName")
    @Mapping(target = "iban", source = "personBank.accountIban")
    @Mapping(target = "currency", source = "personBank.accountCurrency")
    BankAccount toBankAccount(
        PersonBankProjection personBank,
        @Context PersonBankingData data
    );

    @Mapping(target = "owner", source = "personBank.euroAccountOwner")
    @Mapping(target = "address", source = "personBank.euroAccountAddress")
    @Mapping(target = "prefix", source = "personBank.euroAccountPrefix")
    @Mapping(target = "suffix", source = "personBank.euroAccountSuffix")
    @Mapping(target = "bankCode", source = "personBank.euroAccountBank")
    @Mapping(target = "bankName", source = "personBank.euroAccountBank", qualifiedByName = "lookupEuroBankName")
    @Mapping(target = "iban", source = "personBank.euroAccountIban")
    @Mapping(target = "currency", source = "personBank.euroAccountCurrency")
    @Mapping(target = "country", expression = "java(data.euroAccountCountryName())")
    @Mapping(target = "swift", source = "personBank.euroAccountSwift")
    EuroBankAccount mapEuroBankAccount(
        PersonBankProjection personBank,
        @Context PersonBankingData data
    );

}
