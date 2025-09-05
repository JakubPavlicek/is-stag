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

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodelistMapper {

    CodelistMapper INSTANCE = Mappers.getMapper(CodelistMapper.class);

    @Mapping(target = "codelistKeys", ignore = true)
    GetCodelistValuesRequest toCodelistValuesRequest(StudyProgramView studyProgram, String language);

    @Mapping(target = "codelistMeanings", source = "codelistMeanings", qualifiedByName = "toMeaningMap")
    CodelistMeaningsLookupData toCodelistMeaningsData(GetCodelistValuesResponse response);

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

    @Named("toMeaningMap")
    default Map<CodelistEntryId, String> toMeaningMap(List<CodelistMeaning> codelistMeanings) {
        return codelistMeanings.stream()
                               .collect(Collectors.toMap(
                                   cm -> new CodelistEntryId(cm.getDomain(), cm.getLowValue()),
                                   CodelistMeaning::getMeaning
                               ));
    }

    private List<CodelistKey> buildStudyProgramCodelistKeys(StudyProgramView studyProgram) {
        List<CodelistKey> codelistKeys = new ArrayList<>(2);

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.FORMA_OBORU_NEW, studyProgram.form());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TYP_OBORU, studyProgram.type());

        return codelistKeys;
    }

    private void addCodelistKeyIfPresent(List<CodelistKey> codelistKeys, CodelistDomain domain, String lowValue) {
        if (lowValue != null) {
            codelistKeys.add(createCodelistKey(domain, lowValue));
        }
    }

    private CodelistKey createCodelistKey(CodelistDomain domain, String lowValue) {
        return CodelistKey.newBuilder()
                          .setDomain(domain.name())
                          .setLowValue(lowValue)
                          .build();
    }

}
