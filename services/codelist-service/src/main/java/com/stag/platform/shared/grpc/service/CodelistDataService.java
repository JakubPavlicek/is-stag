package com.stag.platform.shared.grpc.service;

import com.google.protobuf.Message;
import com.stag.platform.address.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.address.service.CountryService;
import com.stag.platform.address.service.MunicipalityPartService;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import com.stag.platform.education.service.HighSchoolFieldOfStudyService;
import com.stag.platform.education.service.HighSchoolService;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
import com.stag.platform.entry.service.CodelistEntryService;
import com.stag.platform.entry.service.dto.PersonProfileLowValues;
import com.stag.platform.shared.grpc.mapper.CodelistMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/// **Codelist Data Service**
///
/// Service layer for data fetching operations.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
class CodelistDataService {

    /// Codelist entry service
    private final CodelistEntryService codelistEntryService;
    /// Country service
    private final CountryService countryService;
    /// Municipality part service
    private final MunicipalityPartService municipalityPartService;
    /// High school service
    private final HighSchoolService highSchoolService;
    /// High school field of study service
    private final HighSchoolFieldOfStudyService highSchoolFieldOfStudyService;

    /// Fetches codelist meanings.
    ///
    /// @param codelistKeys List of codelist keys
    /// @param language Language code
    /// @return List of codelist meanings
    public List<CodelistMeaning> fetchCodelistMeanings(List<CodelistKey> codelistKeys, String language) {
        log.info("Fetching codelist meanings for {} keys in language: {}", codelistKeys.size(), language);

        List<CodelistEntryId> entryIds = CodelistMapper.INSTANCE.toCodelistEntryIds(codelistKeys);
        List<CodelistEntryMeaningProjection> entries = codelistEntryService.findMeaningsByIds(entryIds, language);

        return CodelistMapper.INSTANCE.toCodelistMeanings(entries);
    }

    /// Fetches country names by extracting IDs from the request.
    ///
    /// @param request gRPC request message
    /// @param language Language code
    /// @return Country ID to a name map
    public Map<Integer, String> fetchCountryNames(Message request, String language) {
        Set<Integer> countryIds = CodelistMapper.INSTANCE.extractCountryIds(request);

        if (countryIds.isEmpty()) {
            log.debug("No country IDs to fetch");
            return Collections.emptyMap();
        }

        return countryService.findNamesByIds(countryIds, language);
    }

    /// Fetches codelist low values for person profile update.
    ///
    /// @param maritalStatus Marital status meaning
    /// @param titlePrefix Title prefix meaning
    /// @param titleSuffix Title suffix meaning
    /// @return Person profile low values
    public PersonProfileLowValues fetchCodelistLowValues(String maritalStatus, String titlePrefix, String titleSuffix) {
        return codelistEntryService.findPersonProfileLowValues(maritalStatus, titlePrefix, titleSuffix);
    }

    /// Fetches country ID by country name.
    ///
    /// @param birthCountryName Country name
    /// @return Country ID
    public Integer fetchCountryId(String birthCountryName) {
        return countryService.findCountryIdByName(birthCountryName);
    }

    /// Fetches address names by extracting municipality part IDs.
    ///
    /// @param request gRPC request message
    /// @return Municipality part ID to an address name map
    public Map<Long, AddressPlaceNameProjection> fetchAddressNames(Message request) {
        Set<Long> municipalityPartIds = CodelistMapper.INSTANCE.extractMunicipalityPartIds(request);

        if (municipalityPartIds.isEmpty()) {
            log.debug("No municipality part IDs to fetch");
            return Collections.emptyMap();
        }

        return municipalityPartService.findAddressNamesByIds(municipalityPartIds);
    }

    /// Fetches high school address if high school ID is present.
    ///
    /// @param hasHighSchoolId Whether high school ID is present in request
    /// @param highSchoolId High school identifier
    /// @return High school address or null
    public HighSchoolAddressProjection fetchHighSchoolAddress(boolean hasHighSchoolId, String highSchoolId) {
        if (!hasHighSchoolId) {
            log.debug("No high school ID provided");
            return null;
        }

        return highSchoolService.findHighSchoolAddressById(highSchoolId);
    }

    /// Fetches high school field of study if a field of study number is present.
    ///
    /// @param hasFieldOfStudyNumber Whether a field of study number is present in the request
    /// @param fieldOfStudyNumber Field of study identifier
    /// @return Ffield of study name or null
    public String fetchHighSchoolFieldOfStudy(boolean hasFieldOfStudyNumber, String fieldOfStudyNumber) {
        if (!hasFieldOfStudyNumber) {
            log.debug("No field of study number provided");
            return null;
        }

        return highSchoolFieldOfStudyService.findFieldOfStudyName(fieldOfStudyNumber);
    }

}
