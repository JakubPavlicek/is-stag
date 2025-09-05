package com.stag.academics.studyprogram.mapper;

import com.stag.academics.shared.grpc.model.CodelistDomain;
import com.stag.academics.shared.grpc.model.CodelistEntryId;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import org.mapstruct.Context;
import org.mapstruct.Named;

import java.util.Map;
import java.util.Optional;

class CodelistValueResolver {

    @Named("lookupForm")
    String lookupForm(String value, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.FORMA_OBORU_NEW, value, data.codelistMeanings());
    }

    @Named("lookupType")
    String lookupType(String value, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TYP_OBORU, value, data.codelistMeanings());
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
