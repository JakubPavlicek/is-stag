package com.stag.identity.person.mapper;

import com.stag.identity.shared.grpc.model.CodelistDomain;
import com.stag.identity.shared.grpc.model.CodelistEntryId;
import com.stag.identity.person.service.data.BankingLookupData;
import com.stag.identity.person.service.data.CodelistMeaningsLookupData;
import com.stag.identity.person.service.data.ProfileLookupData;
import org.mapstruct.Context;
import org.mapstruct.Named;

import java.util.Map;
import java.util.Optional;

/// **Codelist Value Resolver**
///
/// Helper class for MapStruct mappers to resolve localized codelist meanings.
/// Provides named methods for looking up gender, marital status, titles, citizenship,
/// and bank names from codelist lookup data. Used via @Named qualifiers in mappers.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
class CodelistValueResolver {

    /// Looks up gender meaning from profile lookup data.
    @Named("lookupGender")
    String lookupGender(String gender, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.POHLAVI, gender, data.codelistMeanings());
    }

    /// Looks up gender meaning from simple profile lookup data.
    @Named("lookupCodelistGender")
    String lookupCodelistGender(String gender, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.POHLAVI, gender, data.codelistMeanings());
    }

    /// Looks up marital status meaning from profile lookup data.
    @Named("lookupMaritalStatus")
    String lookupMaritalStatus(String maritalStatus, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.STAV, maritalStatus, data.codelistMeanings());
    }

    /// Looks up title prefix meaning from profile lookup data.
    @Named("lookupTitlePrefix")
    String lookupTitlePrefix(String titlePrefix, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TITUL_PRED, titlePrefix, data.codelistMeanings());
    }

    /// Looks up title prefix meaning from simple profile lookup data.
    @Named("lookupCodelistTitlePrefix")
    String lookupCodelistTitlePrefix(String titlePrefix, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TITUL_PRED, titlePrefix, data.codelistMeanings());
    }

    /// Looks up title suffix meaning from profile lookup data.
    @Named("lookupTitleSuffix")
    String lookupTitleSuffix(String titleSuffix, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TITUL_ZA, titleSuffix, data.codelistMeanings());
    }

    /// Looks up title suffix meaning from simple profile lookup data.
    @Named("lookupCodelistTitleSuffix")
    String lookupCodelistTitleSuffix(String titleSuffix, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TITUL_ZA, titleSuffix, data.codelistMeanings());
    }

    /// Looks up citizenship qualification meaning from profile lookup data.
    @Named("lookupCitizenshipQualifier")
    String lookupCitizenshipQualifier(String qualifier, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.KVANT_OBCAN, qualifier, data.codelistMeanings());
    }

    /// Looks up Czech bank name from banking lookup data.
    @Named("lookupBankName")
    String lookupBankName(String bankCode, @Context BankingLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.CIS_BANK, bankCode, data.codelistMeanings());
    }

    /// Looks up Euro bank name from banking lookup data.
    @Named("lookupEuroBankName")
    String lookupEuroBankName(String bankCode, @Context BankingLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.CIS_BANK_EURO, bankCode, data.codelistMeanings());
    }

    /// Generic codelist value lookup by domain and low value.
    /// Returns localized meaning from codelist meanings map.
    ///
    /// @param domain the codelist domain
    /// @param lowValue the codelist low value (database code)
    /// @param meanings the codelist meanings map
    /// @return localized codelist meaning or null if not found
    private String lookupCodelistValue(
        CodelistDomain domain,
        String lowValue,
        Map<CodelistEntryId, String> meanings
    ) {
        return Optional.ofNullable(meanings)
                       .map(m -> m.get(new CodelistEntryId(domain.name(), lowValue)))
                       .orElse(null);
    }

}
