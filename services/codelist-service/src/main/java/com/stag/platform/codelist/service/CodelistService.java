package com.stag.platform.codelist.service;

import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.projection.CodelistEntryValue;
import com.stag.platform.codelist.repository.CodelistEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodelistService {

    private final CodelistEntryRepository codelistEntryRepository;

    @Transactional(readOnly = true)
    public List<CodelistEntryValue> getCodelistEntryMeanings(List<CodelistEntryId> codelistEntryIds) {
        return codelistEntryRepository.findAllByIdIn(codelistEntryIds);
    }

}
