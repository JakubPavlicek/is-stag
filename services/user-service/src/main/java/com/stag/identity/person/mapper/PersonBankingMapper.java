package com.stag.identity.person.mapper;

import com.stag.identity.person.model.PersonBanking;
import com.stag.identity.person.model.PersonBanking.BankAccount;
import com.stag.identity.person.model.PersonBanking.EuroBankAccount;
import com.stag.identity.person.repository.projection.PersonBankProjection;
import com.stag.identity.person.service.data.PersonBankingData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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

    @Mapping(target = "owner", source = "accountOwner")
    @Mapping(target = "address", source = "accountAddress")
    @Mapping(target = "prefix", source = "accountPrefix")
    @Mapping(target = "suffix", source = "accountSuffix")
    @Mapping(target = "bankCode", source = "accountBank")
    @Mapping(target = "bankName", source = "accountBank", qualifiedByName = "lookupBankName")
    @Mapping(target = "iban", source = "accountIban")
    @Mapping(target = "currency", source = "accountCurrency")
    BankAccount toBankAccount(
        PersonBankProjection personBank,
        @Context PersonBankingData data
    );

    @Mapping(target = "owner", source = "euroAccountOwner")
    @Mapping(target = "address", source = "euroAccountAddress")
    @Mapping(target = "prefix", source = "euroAccountPrefix")
    @Mapping(target = "suffix", source = "euroAccountSuffix")
    @Mapping(target = "bankCode", source = "euroAccountBank")
    @Mapping(target = "bankName", source = "euroAccountBank", qualifiedByName = "lookupEuroBankName")
    @Mapping(target = "iban", source = "euroAccountIban")
    @Mapping(target = "currency", source = "euroAccountCurrency")
    @Mapping(target = "country", source = "personBank", qualifiedByName = "euroAccountCountryName")
    @Mapping(target = "swift", source = "euroAccountSwift")
    EuroBankAccount toEuroBankAccount(
        PersonBankProjection personBank,
        @Context PersonBankingData data
    );

    @Named("euroAccountCountryName")
    default String euroAccountCountryName(
        PersonBankProjection personBank,
        @Context PersonBankingData data
    ) {
        return data.euroAccountCountryName();
    }

}
