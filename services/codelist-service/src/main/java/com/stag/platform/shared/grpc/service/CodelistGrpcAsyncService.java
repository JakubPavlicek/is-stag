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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/// **Codelist gRPC Async Service**
///
/// Asynchronous service layer for concurrent data fetching operations.
/// Executes service calls in parallel using Spring's @Async annotation.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
class CodelistGrpcAsyncService {

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

    /// Fetches codelist meanings synchronously.
    ///
    /// @param codelistKeys List of codelist keys
    /// @param language Language code
    /// @return List of codelist meanings
    public List<CodelistMeaning> fetchCodelistMeanings(List<CodelistKey> codelistKeys, String language) {
        List<CodelistEntryId> entryIds = CodelistMapper.INSTANCE.toCodelistEntryIds(codelistKeys);
        List<CodelistEntryMeaningProjection> entries = codelistEntryService.findMeaningsByIds(entryIds, language);
        return CodelistMapper.INSTANCE.toCodelistMeanings(entries);
    }

    /// Fetches codelist meanings asynchronously.
    ///
    /// @param codelistKeys List of codelist keys
    /// @param language Language code
    /// @return CompletableFuture with codelist meanings
    @Async
    public CompletableFuture<List<CodelistMeaning>> fetchCodelistMeaningsAsync(List<CodelistKey> codelistKeys, String language) {
        List<CodelistMeaning> codelistValues = fetchCodelistMeanings(codelistKeys, language);
        return CompletableFuture.completedFuture(codelistValues);
    }

    /// Fetches country names asynchronously by extracting IDs from the request.
    ///
    /// @param request gRPC request message
    /// @param language Language code
    /// @return CompletableFuture with country ID to name map
    @Async
    public CompletableFuture<Map<Integer, String>> fetchCountryNamesAsync(Message request, String language) {
        Set<Integer> countryIds = CodelistMapper.INSTANCE.extractCountryIds(request);
        Map<Integer, String> countryNames = fetchCountryNames(countryIds, language);
        return CompletableFuture.completedFuture(countryNames);
    }

    /// Fetches codelist low values asynchronously for person profile update.
    ///
    /// @param maritalStatus Marital status meaning
    /// @param titlePrefix Title prefix meaning
    /// @param titleSuffix Title suffix meaning
    /// @return CompletableFuture with person profile low values
    @Async
    public CompletableFuture<PersonProfileLowValues> fetchCodelistLowValuesAsync(String maritalStatus, String titlePrefix, String titleSuffix) {
        PersonProfileLowValues codes = codelistEntryService.findPersonProfileLowValues(maritalStatus, titlePrefix, titleSuffix);
        return CompletableFuture.completedFuture(codes);
    }

    /// Fetches country ID asynchronously by country name.
    ///
    /// @param birthCountryName Country name
    /// @return CompletableFuture with country ID
    @Async
    public CompletableFuture<Integer> fetchCountryIdAsync(String birthCountryName) {
        Integer birthCountryId = countryService.findCountryIdByName(birthCountryName);
        return CompletableFuture.completedFuture(birthCountryId);
    }

    /// Fetches address names asynchronously by extracting municipality part IDs.
    ///
    /// @param request gRPC request message
    /// @return CompletableFuture with municipality part ID to address name map
    @Async
    public CompletableFuture<Map<Long, AddressPlaceNameProjection>> fetchAddressNamesAsync(Message request) {
        Set<Long> municipalityPartIds = CodelistMapper.INSTANCE.extractMunicipalityPartIds(request);
        Map<Long, AddressPlaceNameProjection> addressNames = fetchAddressNames(municipalityPartIds);
        return CompletableFuture.completedFuture(addressNames);
    }

    /// Fetches high school address asynchronously if high school ID is present.
    ///
    /// @param hasHighSchoolId Whether high school ID is present in request
    /// @param highSchoolId High school identifier
    /// @return CompletableFuture with high school address or null
    @Async
    public CompletableFuture<HighSchoolAddressProjection> fetchHighSchoolAddressAsync(boolean hasHighSchoolId, String highSchoolId) {
        HighSchoolAddressProjection highSchool = fetchHighSchoolAddress(hasHighSchoolId, highSchoolId);
        return CompletableFuture.completedFuture(highSchool);
    }

    /// Fetches high school field of study asynchronously if field of study number is present.
    ///
    /// @param hasFieldOfStudyNumber Whether field of study number is present in request
    /// @param fieldOfStudyNumber Field of study identifier
    /// @return CompletableFuture with field of study name or null
    @Async
    public CompletableFuture<String> fetchHighSchoolFieldOfStudyAsync(boolean hasFieldOfStudyNumber, String fieldOfStudyNumber) {
        String fieldOfStudyName = fetchHighSchoolFieldOfStudy(hasFieldOfStudyNumber, fieldOfStudyNumber);
        return CompletableFuture.completedFuture(fieldOfStudyName);
    }

    /// Fetches country names by IDs. Returns empty map if no IDs provided.
    ///
    /// @param countryIds Set of country IDs
    /// @param language Language code
    /// @return Map of country ID to name
    private Map<Integer, String> fetchCountryNames(Set<Integer> countryIds, String language) {
        return countryIds.isEmpty()
            ? Collections.emptyMap()
            : countryService.findNamesByIds(countryIds, language);
    }

    /// Fetches address names by municipality part IDs. Returns empty map if no IDs provided.
    ///
    /// @param municipalityPartIds Set of municipality part IDs
    /// @return Map of municipality part ID to address name projection
    private Map<Long, AddressPlaceNameProjection> fetchAddressNames(Set<Long> municipalityPartIds) {
        return municipalityPartIds.isEmpty()
            ? Collections.emptyMap()
            : municipalityPartService.findAddressNamesByIds(municipalityPartIds);
    }

    /// Fetches high school address if ID is present.
    ///
    /// @param hasHighSchoolId Whether high school ID is present
    /// @param highSchoolId High school identifier
    /// @return High school address or null
    private HighSchoolAddressProjection fetchHighSchoolAddress(boolean hasHighSchoolId, String highSchoolId) {
        return hasHighSchoolId
            ? highSchoolService.findHighSchoolAddressById(highSchoolId)
            : null;
    }

    /// Fetches high school field of study name if number is present.
    ///
    /// @param hasFieldOfStudyNumber Whether field of study number is present
    /// @param fieldOfStudyNumber Field of study identifier
    /// @return Field of study name or null
    private String fetchHighSchoolFieldOfStudy(boolean hasFieldOfStudyNumber, String fieldOfStudyNumber) {
        return hasFieldOfStudyNumber
            ? highSchoolFieldOfStudyService.findFieldOfStudyName(fieldOfStudyNumber)
            : null;
    }

}
