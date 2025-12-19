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

@Slf4j
@RequiredArgsConstructor
@Service
class CodelistGrpcAsyncService {

    private final CodelistEntryService codelistEntryService;
    private final CountryService countryService;
    private final MunicipalityPartService municipalityPartService;
    private final HighSchoolService highSchoolService;
    private final HighSchoolFieldOfStudyService highSchoolFieldOfStudyService;

    public List<CodelistMeaning> fetchCodelistMeanings(List<CodelistKey> codelistKeys, String language) {
        List<CodelistEntryId> entryIds = CodelistMapper.INSTANCE.toCodelistEntryIds(codelistKeys);
        List<CodelistEntryMeaningProjection> entries = codelistEntryService.findMeaningsByIds(entryIds, language);
        return CodelistMapper.INSTANCE.toCodelistMeanings(entries);
    }

    @Async
    public CompletableFuture<List<CodelistMeaning>> fetchCodelistMeaningsAsync(List<CodelistKey> codelistKeys, String language) {
        List<CodelistMeaning> codelistValues = fetchCodelistMeanings(codelistKeys, language);
        return CompletableFuture.completedFuture(codelistValues);
    }

    @Async
    public CompletableFuture<Map<Integer, String>> fetchCountryNamesAsync(Message request, String language) {
        Set<Integer> countryIds = CodelistMapper.INSTANCE.extractCountryIds(request);
        Map<Integer, String> countryNames = fetchCountryNames(countryIds, language);
        return CompletableFuture.completedFuture(countryNames);
    }

    @Async
    public CompletableFuture<PersonProfileLowValues> fetchCodelistLowValuesAsync(String maritalStatus, String titlePrefix, String titleSuffix) {
        PersonProfileLowValues codes = codelistEntryService.findPersonProfileLowValues(maritalStatus, titlePrefix, titleSuffix);
        return CompletableFuture.completedFuture(codes);
    }

    @Async
    public CompletableFuture<Integer> fetchCountryIdAsync(String birthCountryName) {
        Integer birthCountryId = countryService.findCountryIdByName(birthCountryName);
        return CompletableFuture.completedFuture(birthCountryId);
    }

    @Async
    public CompletableFuture<Map<Long, AddressPlaceNameProjection>> fetchAddressNamesAsync(Message request) {
        Set<Long> municipalityPartIds = CodelistMapper.INSTANCE.extractMunicipalityPartIds(request);
        Map<Long, AddressPlaceNameProjection> addressNames = fetchAddressNames(municipalityPartIds);
        return CompletableFuture.completedFuture(addressNames);
    }

    @Async
    public CompletableFuture<HighSchoolAddressProjection> fetchHighSchoolAddressAsync(boolean hasHighSchoolId, String highSchoolId) {
        HighSchoolAddressProjection highSchool = fetchHighSchoolAddress(hasHighSchoolId, highSchoolId);
        return CompletableFuture.completedFuture(highSchool);
    }

    @Async
    public CompletableFuture<String> fetchHighSchoolFieldOfStudyAsync(boolean hasFieldOfStudyNumber, String fieldOfStudyNumber) {
        String fieldOfStudyName = fetchHighSchoolFieldOfStudy(hasFieldOfStudyNumber, fieldOfStudyNumber);
        return CompletableFuture.completedFuture(fieldOfStudyName);
    }

    private Map<Integer, String> fetchCountryNames(Set<Integer> countryIds, String language) {
        return countryIds.isEmpty()
            ? Collections.emptyMap()
            : countryService.findNamesByIds(countryIds, language);
    }

    private Map<Long, AddressPlaceNameProjection> fetchAddressNames(Set<Long> municipalityPartIds) {
        return municipalityPartIds.isEmpty()
            ? Collections.emptyMap()
            : municipalityPartService.findAddressNamesByIds(municipalityPartIds);
    }

    private HighSchoolAddressProjection fetchHighSchoolAddress(boolean hasHighSchoolId, String highSchoolId) {
        return hasHighSchoolId
            ? highSchoolService.findHighSchoolAddressById(highSchoolId)
            : null;
    }

    private String fetchHighSchoolFieldOfStudy(boolean hasFieldOfStudyNumber, String fieldOfStudyNumber) {
        return hasFieldOfStudyNumber
            ? highSchoolFieldOfStudyService.findFieldOfStudyName(fieldOfStudyNumber)
            : null;
    }

}
