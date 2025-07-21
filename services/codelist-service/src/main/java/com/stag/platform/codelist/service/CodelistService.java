package com.stag.platform.codelist.service;

import com.stag.platform.codelist.entity.CodelistValueId;
import com.stag.platform.codelist.projection.CodelistValueMeaning;
import com.stag.platform.codelist.repository.CodelistValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodelistService {

    private final CodelistValueRepository codelistValueRepository;

    public List<CodelistValueMeaning> getCodelistValues(List<CodelistValueId> codelistValueIds) {
        return codelistValueRepository.findAllByIdIn(codelistValueIds);
    }

}
