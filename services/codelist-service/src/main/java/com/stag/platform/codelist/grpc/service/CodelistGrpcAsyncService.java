package com.stag.platform.codelist.grpc.service;

import com.google.protobuf.Message;
import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.grpc.mapper.CodelistMapper;
import com.stag.platform.codelist.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.codelist.repository.projection.CodelistEntryValue;
import com.stag.platform.codelist.repository.projection.HighSchoolAddressProjection;
import com.stag.platform.codelist.service.CodelistService;
import com.stag.platform.codelist.service.CountryService;
import com.stag.platform.codelist.service.HighSchoolFieldOfStudyService;
import com.stag.platform.codelist.service.HighSchoolService;
import com.stag.platform.codelist.service.MunicipalityPartService;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistValue;
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
public class CodelistGrpcAsyncService {

    private final CodelistService codelistService;
    private final CountryService countryService;
    private final MunicipalityPartService municipalityPartService;
    private final HighSchoolService highSchoolService;
    private final HighSchoolFieldOfStudyService highSchoolFieldOfStudyService;

    private final CodelistMapper codelistMapper;

    @Async("grpcExecutor")
    public CompletableFuture<List<CodelistValue>> fetchCodelistValuesAsync(List<CodelistKey> codelistKeys, String language) {
        List<CodelistValue> codelistValues = fetchCodelistValues(codelistKeys, language);
        return CompletableFuture.completedFuture(codelistValues);
    }

    @Async("grpcExecutor")
    public CompletableFuture<Map<Integer, String>> fetchCountryNamesAsync(Message request, String language) {
        Set<Integer> countryIds = codelistMapper.extractCountryIds(request);
        Map<Integer, String> countryNames = fetchCountryNames(countryIds, language);
        return CompletableFuture.completedFuture(countryNames);
    }

    @Async("grpcExecutor")
    public CompletableFuture<Map<Long, AddressPlaceNameProjection>> fetchAddressNamesAsync(Message request) {
        Set<Long> municipalityPartIds = codelistMapper.extractMunicipalityPartIds(request);
        Map<Long, AddressPlaceNameProjection> addressNames = fetchAddressNames(municipalityPartIds);
        return CompletableFuture.completedFuture(addressNames);
    }

    @Async("grpcExecutor")
    public CompletableFuture<HighSchoolAddressProjection> fetchHighSchoolAsync(boolean hasHighSchoolId, String highSchoolId) {
        HighSchoolAddressProjection highSchool = fetchHighSchool(hasHighSchoolId, highSchoolId);
        return CompletableFuture.completedFuture(highSchool);
    }

    @Async("grpcExecutor")
    public CompletableFuture<String> fetchHighSchoolFieldOfStudyAsync(boolean hasFieldOfStudyNumber, String fieldOfStudyNumber) {
        String fieldOfStudyName = fetchHighSchoolFieldOfStudy(hasFieldOfStudyNumber, fieldOfStudyNumber);
        return CompletableFuture.completedFuture(fieldOfStudyName);
    }

    private List<CodelistValue> fetchCodelistValues(List<CodelistKey> codelistKeys, String language) {
        List<CodelistEntryId> entryIds = codelistMapper.extractCodelistEntryIds(codelistKeys);
        List<CodelistEntryValue> entries = codelistService.findCodelistEntriesByIds(entryIds, language);
        return codelistMapper.mapToCodelistValues(entries);
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

    private HighSchoolAddressProjection fetchHighSchool(boolean hasHighSchoolId, String highSchoolId) {
        return hasHighSchoolId
            ? highSchoolService.getHighSchoolName(highSchoolId)
            : null;
    }

    private String fetchHighSchoolFieldOfStudy(boolean hasFieldOfStudyNumber, String fieldOfStudyNumber) {
        return hasFieldOfStudyNumber
            ? highSchoolFieldOfStudyService.findFieldOfStudyName(fieldOfStudyNumber)
            : null;
    }

}
