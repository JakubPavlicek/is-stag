package com.stag.academics.studyprogram.mapper;

import com.stag.academics.shared.grpc.model.CodelistDomain;
import com.stag.academics.shared.grpc.model.CodelistEntryId;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import org.mapstruct.Context;
import org.mapstruct.Named;

import java.util.Map;
import java.util.Optional;

/// **Codelist Value Resolver**
///
/// Helper class for MapStruct mappers to resolve codelist code values
/// to their localized meanings. Used by StudyProgramMapper.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
class CodelistValueResolver {

    /// Resolves study form code to localized meaning.
    ///
    /// @param value the form code
    /// @param data the codelist meanings lookup data
    /// @return localized form meaning or null
    @Named("lookupForm")
    String lookupForm(String value, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.FORMA_OBORU_NEW, value, data.codelistMeanings());
    }

    /// Resolves study type code to localized meaning.
    ///
    /// @param value the type code
    /// @param data the codelist meanings lookup data
    /// @return localized type meaning or null
    @Named("lookupType")
    String lookupType(String value, @Context CodelistMeaningsLookupData data) {
        if (data == null) {
            return null;
        }

        return lookupCodelistValue(CodelistDomain.TYP_OBORU, value, data.codelistMeanings());
    }

    /// Looks up a codelist value meaning from the meanings map.
    ///
    /// @param domain the codelist domain
    /// @param lowValue the low value (code)
    /// @param meanings the map of codelist meanings
    /// @return localized meaning or null if not found
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
