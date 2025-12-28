package com.stag.identity.person.mapper;

import com.stag.identity.person.model.Banking;
import com.stag.identity.person.model.Banking.BankAccount;
import com.stag.identity.person.model.Banking.EuroBankAccount;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.service.data.BankingLookupData;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/// **Banking Mapper**
///
/// MapStruct mapper for transforming banking projections to domain models.
/// Enriches accounts with localized bank names from codelist service.
/// Handles both Czech and Euro bank accounts with IBAN and SWIFT codes.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(uses = { CodelistValueResolver.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BankingMapper {

    /// BankingMapper Instance
    BankingMapper INSTANCE = Mappers.getMapper(BankingMapper.class);

    /// Maps banking projection to complete banking model with account information.
    ///
    /// @param personBank the banking projection
    /// @param data the banking lookup data with localized bank names
    /// @return banking model with Czech and Euro accounts
    @Mapping(target = "account", source = "personBank")
    @Mapping(target = "euroAccount", source = "personBank")
    Banking toPersonBanking(
        BankView personBank,
        @Context BankingLookupData data
    );

    /// Maps Czech bank account with localized bank name.
    ///
    /// @param personBank the banking projection
    /// @param data the banking lookup data
    /// @return Czech bank account model
    @Mapping(target = "owner", source = "accountOwner")
    @Mapping(target = "address", source = "accountAddress")
    @Mapping(target = "prefix", source = "accountPrefix")
    @Mapping(target = "suffix", source = "accountSuffix")
    @Mapping(target = "bankCode", source = "accountBank")
    @Mapping(target = "bankName", source = "accountBank", qualifiedByName = "lookupBankName")
    @Mapping(target = "iban", source = "accountIban")
    @Mapping(target = "currency", source = "accountCurrency")
    BankAccount toBankAccount(
        BankView personBank,
        @Context BankingLookupData data
    );

    /// Maps Euro bank account with localized bank name and country.
    ///
    /// @param personBank the banking projection
    /// @param data the banking lookup data
    /// @return Euro bank account model with IBAN and SWIFT
    @Mapping(target = "owner", source = "euroAccountOwner")
    @Mapping(target = "address", source = "euroAccountAddress")
    @Mapping(target = "prefix", source = "euroAccountPrefix")
    @Mapping(target = "suffix", source = "euroAccountSuffix")
    @Mapping(target = "bankCode", source = "euroAccountBank")
    @Mapping(target = "bankName", source = "euroAccountBank", qualifiedByName = "lookupEuroBankName")
    @Mapping(target = "iban", source = "euroAccountIban")
    @Mapping(target = "currency", source = "euroAccountCurrency")
    @Mapping(target = "country", source = "personBank", qualifiedByName = "euroAccountCountryName")
    @Mapping(target = "swiftCode", source = "euroAccountSwiftCode")
    EuroBankAccount toEuroBankAccount(
        BankView personBank,
        @Context BankingLookupData data
    );

    /// Resolves Euro account country name from lookup data.
    @Named("euroAccountCountryName")
    default String euroAccountCountryName(
        BankView personBank,
        @Context BankingLookupData data
    ) {
        if (data == null) {
            return null;
        }

        return data.euroAccountCountryName();
    }

}
