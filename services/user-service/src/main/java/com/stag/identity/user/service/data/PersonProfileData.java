package com.stag.identity.user.service.data;

import com.stag.identity.user.model.CodelistEntryId;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PersonProfileData {
    private Map<CodelistEntryId, String> codelistMeanings;
    private String birthCountryName;
    private String citizenshipCountryName;
}
