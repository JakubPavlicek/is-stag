package com.stag.identity.person.mapper;

import com.stag.identity.person.model.CodelistDomain;
import com.stag.identity.person.model.CodelistEntryId;
import com.stag.identity.person.service.data.PersonBankingData;
import com.stag.identity.person.service.data.PersonProfileData;
import org.mapstruct.Context;
import org.mapstruct.Named;

import java.util.Map;
import java.util.Optional;

class Qualifiers {

    @Named("lookupGender")
    String lookupGender(String gender, @Context PersonProfileData codelistData) {
        return lookupCodelistValue(CodelistDomain.POHLAVI, gender, codelistData.codelistMeanings());
    }

    @Named("lookupMaritalStatus")
    String lookupMaritalStatus(String maritalStatus, @Context PersonProfileData data) {
        return lookupCodelistValue(CodelistDomain.STAV, maritalStatus, data.codelistMeanings());
    }

    @Named("lookupTitlePrefix")
    String lookupTitlePrefix(String titlePrefix, @Context PersonProfileData data) {
        return lookupCodelistValue(CodelistDomain.TITUL_PRED, titlePrefix, data.codelistMeanings());
    }

    @Named("lookupTitleSuffix")
    String lookupTitleSuffix(String titleSuffix, @Context PersonProfileData data) {
        return lookupCodelistValue(CodelistDomain.TITUL_ZA, titleSuffix, data.codelistMeanings());
    }

    @Named("lookupCitizenshipQualifier")
    String lookupCitizenshipQualifier(String qualifier, @Context PersonProfileData data) {
        return lookupCodelistValue(CodelistDomain.KVANT_OBCAN, qualifier, data.codelistMeanings());
    }

    @Named("lookupBankName")
    String lookupBankName(String bankCode, @Context PersonBankingData data) {
        return lookupCodelistValue(CodelistDomain.CIS_BANK, bankCode, data.codelistMeanings());
    }

    @Named("lookupEuroBankName")
    String lookupEuroBankName(String bankCode, @Context PersonBankingData data) {
        return lookupCodelistValue(CodelistDomain.CIS_BANK_EURO, bankCode, data.codelistMeanings());
    }

    private String lookupCodelistValue(
        CodelistDomain domain,
        String lowValue,
        Map<CodelistEntryId, String> meanings
    ) {
        return Optional.ofNullable(lowValue)
                       .map(value -> meanings.get(new CodelistEntryId(domain.name(), value)))
                       .orElse(null);
    }

}
