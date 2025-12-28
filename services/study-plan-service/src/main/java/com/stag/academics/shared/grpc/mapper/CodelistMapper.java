package com.stag.academics.shared.grpc.mapper;

import com.stag.academics.shared.grpc.model.CodelistDomain;
import com.stag.academics.shared.grpc.model.CodelistEntryId;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.data.CodelistMeaningsLookupData;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.GetCodelistValuesRequest;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/// **Codelist Mapper**
///
/// MapStruct mapper for building gRPC requests to codelist service and
/// transforming responses into lookup data. Handles extraction of codelist
/// keys from study program data.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodelistMapper {

    /// CodelistMapper Instance
    CodelistMapper INSTANCE = Mappers.getMapper(CodelistMapper.class);

    /// Converts study program view to codelist values request.
    ///
    /// @param studyProgram the study program view
    /// @param language the language code for localization
    /// @return gRPC request for codelist values
    @Mapping(target = "codelistKeys", ignore = true)
    GetCodelistValuesRequest toCodelistValuesRequest(StudyProgramView studyProgram, String language);

    /// Converts codelist values response to lookup data.
    ///
    /// @param response the gRPC response from codelist service
    /// @return codelist meanings lookup data
    @Mapping(target = "codelistMeanings", source = "codelistMeanings", qualifiedByName = "toMeaningMap")
    CodelistMeaningsLookupData toCodelistMeaningsData(GetCodelistValuesResponse response);

    /// Adds codelist keys extracted from study program after mapping.
    ///
    /// @param studyProgram the study program view
    /// @param builder the request builder to populate
    @AfterMapping
    default void addSimpleProfileCodelistKeys(
        StudyProgramView studyProgram,
        @MappingTarget GetCodelistValuesRequest.Builder builder
    ) {
        if (studyProgram == null) {
            return;
        }

        List<CodelistKey> keys = buildStudyProgramCodelistKeys(studyProgram);
        builder.addAllCodelistKeys(keys);
    }

    /// Converts list of codelist meanings to map indexed by entry ID.
    ///
    /// @param codelistMeanings the list of codelist meanings
    /// @return map of entry IDs to meanings
    @Named("toMeaningMap")
    default Map<CodelistEntryId, String> toMeaningMap(List<CodelistMeaning> codelistMeanings) {
        return codelistMeanings.stream()
                               .collect(Collectors.toMap(
                                   cm -> new CodelistEntryId(cm.getDomain(), cm.getLowValue()),
                                   CodelistMeaning::getMeaning
                               ));
    }

    /// Builds codelist keys list from study program data.
    ///
    /// @param studyProgram the study program view
    /// @return list of codelist keys for form and type
    private List<CodelistKey> buildStudyProgramCodelistKeys(StudyProgramView studyProgram) {
        List<CodelistKey> codelistKeys = new ArrayList<>(2);

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.FORMA_OBORU_NEW, studyProgram.form());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TYP_OBORU, studyProgram.type());

        return codelistKeys;
    }

    /// Adds codelist key to list if value is present.
    ///
    /// @param codelistKeys the list to add to
    /// @param domain the codelist domain
    /// @param lowValue the low value (code)
    private void addCodelistKeyIfPresent(List<CodelistKey> codelistKeys, CodelistDomain domain, String lowValue) {
        if (lowValue != null) {
            codelistKeys.add(createCodelistKey(domain, lowValue));
        }
    }

    /// Creates a codelist key for gRPC request.
    ///
    /// @param domain the codelist domain
    /// @param lowValue the low value (code)
    /// @return codelist key protobuf object
    private CodelistKey createCodelistKey(CodelistDomain domain, String lowValue) {
        return CodelistKey.newBuilder()
                          .setDomain(domain.name())
                          .setLowValue(lowValue)
                          .build();
    }

}
