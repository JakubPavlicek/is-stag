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

class CodelistValueResolver {

    @Named("lookupGender")
    String lookupGender(String gender, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.POHLAVI, gender, data.codelistMeanings());
    }

    @Named("lookupCodelistGender")
    String lookupCodelistGender(String gender, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.POHLAVI, gender, data.codelistMeanings());
    }

    @Named("lookupMaritalStatus")
    String lookupMaritalStatus(String maritalStatus, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.STAV, maritalStatus, data.codelistMeanings());
    }

    @Named("lookupTitlePrefix")
    String lookupTitlePrefix(String titlePrefix, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TITUL_PRED, titlePrefix, data.codelistMeanings());
    }

    @Named("lookupCodelistTitlePrefix")
    String lookupCodelistTitlePrefix(String titlePrefix, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TITUL_PRED, titlePrefix, data.codelistMeanings());
    }

    @Named("lookupTitleSuffix")
    String lookupTitleSuffix(String titleSuffix, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TITUL_ZA, titleSuffix, data.codelistMeanings());
    }

    @Named("lookupCodelistTitleSuffix")
    String lookupCodelistTitleSuffix(String titleSuffix, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TITUL_ZA, titleSuffix, data.codelistMeanings());
    }

    @Named("lookupCitizenshipQualifier")
    String lookupCitizenshipQualifier(String qualifier, @Context ProfileLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.KVANT_OBCAN, qualifier, data.codelistMeanings());
    }

    @Named("lookupBankName")
    String lookupBankName(String bankCode, @Context BankingLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.CIS_BANK, bankCode, data.codelistMeanings());
    }

    @Named("lookupEuroBankName")
    String lookupEuroBankName(String bankCode, @Context BankingLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.CIS_BANK_EURO, bankCode, data.codelistMeanings());
    }

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
