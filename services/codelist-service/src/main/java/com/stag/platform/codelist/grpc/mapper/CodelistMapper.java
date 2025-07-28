package com.stag.platform.codelist.grpc.mapper;

import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.codelist.repository.projection.CodelistEntryValue;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistValue;
import com.stag.platform.codelist.v1.GetPersonAddressDataRequest;
import com.stag.platform.codelist.v1.GetPersonAddressDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileDataResponse;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class CodelistMapper {

    private static final String CZECH_LANGUAGE = "cs";

    public List<CodelistEntryId> extractCodelistEntryIds(List<CodelistKey> codelistKeys) {
        return codelistKeys.stream()
                           .map(this::buildCodelistEntryId)
                           .toList();
    }

    public List<CodelistValue> mapToCodelistValues(List<CodelistEntryValue> entries, String language) {
        return entries.stream()
                      .map(entry -> toCodelistValue(entry, language))
                      .toList();
    }

    public Set<Integer> extractCountryIds(GetPersonProfileDataRequest request) {
        Set<Integer> countryIds = HashSet.newHashSet(2);

        addIfPresent(countryIds, request.hasBirthCountryId(), request::getBirthCountryId);
        addIfPresent(countryIds, request.hasCitizenshipCountryId(), request::getCitizenshipCountryId);

        return countryIds;
    }

    public Set<Integer> extractCountryIds(GetPersonAddressDataRequest request) {
        Set<Integer> countryIds = HashSet.newHashSet(2);

        addIfPresent(countryIds, request.hasPermanentCountryId(), request::getPermanentCountryId);
        addIfPresent(countryIds, request.hasTemporaryCountryId(), request::getTemporaryCountryId);

        return countryIds;
    }

    public Set<Long> extractMunicipalityPartIds(GetPersonAddressDataRequest request) {
        Set<Long> municipalityPartIds = HashSet.newHashSet(2);

        addIfPresent(municipalityPartIds, request.hasPermanentMunicipalityPartId(), request::getPermanentMunicipalityPartId);
        addIfPresent(municipalityPartIds, request.hasTemporaryMunicipalityPartId(), request::getTemporaryMunicipalityPartId);

        return municipalityPartIds;
    }

    public GetPersonProfileDataResponse buildPersonProfileDataResponse(
        GetPersonProfileDataRequest request,
        List<CodelistValue> codelistValues,
        Map<Integer, String> countryNames
    ) {

        var responseBuilder = GetPersonProfileDataResponse.newBuilder()
                                                          .addAllCodelistValues(codelistValues);

        if (!countryNames.isEmpty()) {
            setCountryNameIfPresent(countryNames, request.getBirthCountryId(), responseBuilder::setBirthCountryName);
            setCountryNameIfPresent(countryNames, request.getCitizenshipCountryId(), responseBuilder::setCitizenshipCountryName);
        }

        return responseBuilder.build();
    }

    public GetPersonAddressDataResponse buildPersonAddressDataResponse(
        GetPersonAddressDataRequest request,
        Map<Long, AddressPlaceNameProjection> addressNames,
        Map<Integer, String> countryNames
    ) {

        var responseBuilder = GetPersonAddressDataResponse.newBuilder();

        // Set address place names
        if (!addressNames.isEmpty()) {
            setAddressPlaceNames(request, addressNames, responseBuilder);
        }

        // Set country names
        if (!countryNames.isEmpty()) {
            setCountryNameIfPresent(countryNames, request.getPermanentCountryId(), responseBuilder::setPermanentCountryName);
            setCountryNameIfPresent(countryNames, request.getTemporaryCountryId(), responseBuilder::setTemporaryCountryName);
        }

        return responseBuilder.build();
    }

    private void setAddressPlaceNames(
        GetPersonAddressDataRequest request,
        Map<Long, AddressPlaceNameProjection> addressNames,
        GetPersonAddressDataResponse.Builder responseBuilder
    ) {

        // Set permanent address place names
        if (request.hasPermanentMunicipalityPartId()) {
            AddressPlaceNameProjection permanentAddress = addressNames.get(request.getPermanentMunicipalityPartId());
            if (permanentAddress != null) {
                responseBuilder.setPermanentMunicipalityName(permanentAddress.municipalityName())
                               .setPermanentMunicipalityPartName(permanentAddress.municipalityPartName())
                               .setPermanentDistrictName(permanentAddress.districtName());
            }
        }

        // Set temporary address place names
        if (request.hasTemporaryMunicipalityPartId()) {
            AddressPlaceNameProjection temporaryAddress = addressNames.get(request.getTemporaryMunicipalityPartId());
            if (temporaryAddress != null) {
                responseBuilder.setTemporaryMunicipalityName(temporaryAddress.municipalityName())
                               .setTemporaryMunicipalityPartName(temporaryAddress.municipalityPartName())
                               .setTemporaryDistrictName(temporaryAddress.districtName());
            }
        }
    }

    private void setCountryNameIfPresent(
        Map<Integer, String> countryNames,
        Integer countryId,
        Consumer<String> setter
    ) {

        if (countryId != null) {
            String countryName = countryNames.get(countryId);
            if (countryName != null) {
                setter.accept(countryName);
            }
        }
    }

    private <T> void addIfPresent(Set<T> collection, boolean hasValue, Supplier<T> valueSupplier) {
        if (hasValue) {
            collection.add(valueSupplier.get());
        }
    }

    private CodelistEntryId buildCodelistEntryId(CodelistKey key) {
        return new CodelistEntryId(key.getDomain(), key.getLowValue());
    }

    private CodelistValue toCodelistValue(CodelistEntryValue entry, String language) {
        String meaning = CZECH_LANGUAGE.equalsIgnoreCase(language)
            ? entry.getMeaningCz()
            : entry.getMeaningEn();

        return CodelistValue.newBuilder()
                            .setDomain(entry.getId().getDomain())
                            .setLowValue(entry.getId().getLowValue())
                            .setMeaning(meaning)
                            .build();
    }

}
