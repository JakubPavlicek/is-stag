package com.stag.platform.codelist.grpc;

import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.entity.Country;
import com.stag.platform.codelist.entity.District;
import com.stag.platform.codelist.entity.Municipality;
import com.stag.platform.codelist.entity.MunicipalityPart;
import com.stag.platform.codelist.projection.CodelistEntryValue;
import com.stag.platform.codelist.service.CodelistService;
import com.stag.platform.codelist.service.CountryService;
import com.stag.platform.codelist.service.DistrictService;
import com.stag.platform.codelist.service.MunicipalityPartService;
import com.stag.platform.codelist.service.MunicipalityService;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.CodelistValue;
import com.stag.platform.codelist.v1.CountryName;
import com.stag.platform.codelist.v1.CountryValue;
import com.stag.platform.codelist.v1.DistrictValue;
import com.stag.platform.codelist.v1.GetCodelistValuesRequest;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import com.stag.platform.codelist.v1.GetPersonAddressCodelistDataRequest;
import com.stag.platform.codelist.v1.GetPersonAddressCodelistDataResponse;
import com.stag.platform.codelist.v1.GetPersonBankingCodelistDataRequest;
import com.stag.platform.codelist.v1.GetPersonBankingCodelistDataResponse;
import com.stag.platform.codelist.v1.GetPersonEducationCodelistDataRequest;
import com.stag.platform.codelist.v1.GetPersonEducationCodelistDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileCodelistDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileCodelistDataResponse;
import com.stag.platform.codelist.v1.MunicipalityPartValue;
import com.stag.platform.codelist.v1.MunicipalityValue;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
public class CodelistGrpcService extends CodelistServiceGrpc.CodelistServiceImplBase {

    private final CodelistService codelistService;
    private final CountryService countryService;
    private final MunicipalityService municipalityService;
    private final MunicipalityPartService municipalityPartService;
    private final DistrictService districtService;

    @Override
    public void getCodelistValues(
        GetCodelistValuesRequest request,
        StreamObserver<GetCodelistValuesResponse> responseObserver
    ) {
        GetCodelistValuesResponse.Builder responseBuilder = GetCodelistValuesResponse.newBuilder();

        // Process codelist entries
        if (!request.getCodelistKeysList().isEmpty()) {
            List<CodelistValue> codelistValues = getCodelistValues(request.getCodelistKeysList(), request.getLanguage());
            responseBuilder.addAllCodelistValues(codelistValues);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonProfileCodelistData(
        GetPersonProfileCodelistDataRequest request,
        StreamObserver<GetPersonProfileCodelistDataResponse> responseObserver
    ) {
        GetPersonProfileCodelistDataResponse.Builder responseBuilder = GetPersonProfileCodelistDataResponse.newBuilder();

        // Process codelist entries
        if (!request.getCodelistKeysList().isEmpty()) {
            List<CodelistValue> codelistValues = getCodelistValues(request.getCodelistKeysList(), request.getLanguage());
            responseBuilder.addAllCodelistValues(codelistValues);
        }

        // Process birth country
        if (request.hasBirthCountryId()) {
            findCountryName(request.getBirthCountryId())
                .ifPresent(name -> responseBuilder.setBirthCountryName(toCountryName(name)));
        }

        // Process citizenship country
        if (request.hasCitizenshipCountryId()) {
            findCountryName(request.getCitizenshipCountryId())
                .ifPresent(name -> responseBuilder.setCitizenshipCountryName(toCountryName(name)));
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonAddressCodelistData(
        GetPersonAddressCodelistDataRequest request,
        StreamObserver<GetPersonAddressCodelistDataResponse> responseObserver
    ) {
        // Build response
        GetPersonAddressCodelistDataResponse.Builder responseBuilder = GetPersonAddressCodelistDataResponse.newBuilder();

        // Process permanent residence data
        if (request.hasPermanentCountryId()) {
            findCountry(request.getPermanentCountryId())
                .ifPresent(country -> responseBuilder.setPermanentCountry(toCountryValue(country)));
        }

        if (request.hasPermanentMunicipalityId()) {
            findMunicipality(request.getPermanentMunicipalityId())
                .ifPresent(municipality -> responseBuilder.setPermanentMunicipality(toMunicipalityValue(municipality)));
        }

        if (request.hasPermanentMunicipalityPartId()) {
            findMunicipalityPart(request.getPermanentMunicipalityPartId())
                .ifPresent(part -> responseBuilder.setPermanentMunicipalityPart(toMunicipalityPartValue(part)));
        }

        if (request.hasPermanentDistrictId()) {
            findDistrict(request.getPermanentDistrictId())
                .ifPresent(district -> responseBuilder.setPermanentDistrict(toDistrictValue(district)));
        }

        // Process temporary residence data
        if (request.hasTemporaryCountryId()) {
            findCountry(request.getTemporaryCountryId())
                .ifPresent(country -> responseBuilder.setTemporaryCountry(toCountryValue(country)));
        }

        if (request.hasTemporaryMunicipalityId()) {
            findMunicipality(request.getTemporaryMunicipalityId())
                .ifPresent(municipality -> responseBuilder.setTemporaryMunicipality(toMunicipalityValue(municipality)));
        }

        if (request.hasTemporaryMunicipalityPartId()) {
            findMunicipalityPart(request.getTemporaryMunicipalityPartId())
                .ifPresent(part -> responseBuilder.setTemporaryMunicipalityPart(toMunicipalityPartValue(part)));
        }

        if (request.hasTemporaryDistrictId()) {
            findDistrict(request.getTemporaryDistrictId())
                .ifPresent(district -> responseBuilder.setTemporaryDistrict(toDistrictValue(district)));
        }

        // Send response
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonBankingCodelistData(
        GetPersonBankingCodelistDataRequest request,
        StreamObserver<GetPersonBankingCodelistDataResponse> responseObserver
    ) {
        // Build response
        GetPersonBankingCodelistDataResponse.Builder responseBuilder = GetPersonBankingCodelistDataResponse.newBuilder();

        // Process codelist entries
        if (!request.getCodelistKeysList().isEmpty()) {
            List<CodelistValue> codelistValues = getCodelistValues(request.getCodelistKeysList(), request.getLanguage());
            responseBuilder.addAllCodelistValues(codelistValues);
        }

        // Send response
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonEducationCodelistData(
        GetPersonEducationCodelistDataRequest request,
        StreamObserver<GetPersonEducationCodelistDataResponse> responseObserver
    ) {
        // Build response
        GetPersonEducationCodelistDataResponse.Builder responseBuilder = GetPersonEducationCodelistDataResponse.newBuilder();

        // Process codelist entries
        if (!request.getCodelistKeysList().isEmpty()) {
            List<CodelistValue> codelistValues = getCodelistValues(request.getCodelistKeysList(), request.getLanguage());
            responseBuilder.addAllCodelistValues(codelistValues);
        }

        // Process high school country
        if (request.hasHighSchoolCountryId()) {
            findCountry(request.getHighSchoolCountryId())
                .ifPresent(country -> responseBuilder.setHighSchoolCountry(toCountryValue(country)));
        }

        // Send response
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    private List<CodelistValue> getCodelistValues(List<CodelistKey> codelistKeys, String language) {
        List<CodelistEntryId> codelistEntryIds = codelistKeys.stream()
                                                             .map(key -> new CodelistEntryId(key.getDomain(), key.getLowValue()))
                                                             .toList();

        return codelistService.getCodelistEntryMeanings(codelistEntryIds)
                              .stream()
                              .map(entry -> toCodelistValue(entry, language))
                              .toList();
    }

    private CodelistValue toCodelistValue(CodelistEntryValue entry, String language) {
        String meaning = "cs".equalsIgnoreCase(language) ? entry.getMeaningCz() : entry.getMeaningEn();
        return CodelistValue.newBuilder()
                            .setDomain(entry.getId().getDomain())
                            .setLowValue(entry.getId().getLowValue())
                            .setMeaning(meaning)
                            .build();
    }

    private CountryValue toCountryValue(Country country) {
        return CountryValue.newBuilder()
                           .setId(country.getId())
                           .setName(country.getName())
                           .setEnglishName(country.getEnglishName())
                           .setAbbreviation(country.getAbbreviation())
                           .build();
    }

    private CountryName toCountryName(String name) {
        return CountryName.newBuilder()
                          .setName(name)
                          .build();
    }

    private MunicipalityValue toMunicipalityValue(Municipality municipality) {
        return MunicipalityValue.newBuilder()
                                .setId(municipality.getId())
                                .setName(municipality.getName())
                                .setAbbreviation(municipality.getAbbreviation())
                                .build();
    }

    private MunicipalityPartValue toMunicipalityPartValue(MunicipalityPart municipalityPart) {
        return MunicipalityPartValue.newBuilder()
                                    .setId(municipalityPart.getId())
                                    .setName(municipalityPart.getName())
                                    .build();
    }

    private DistrictValue toDistrictValue(District district) {
        return DistrictValue.newBuilder()
                            .setId(district.getId())
                            .setName(district.getName())
                            .setAbbreviation(district.getAbbreviation())
                            .build();
    }

    private Optional<Country> findCountry(int id) {
        return countryService.findById(id);
    }

    private Optional<String> findCountryName(int id) {
        return countryService.findNameById(id);
    }

    private Optional<Municipality> findMunicipality(long id) {
        return municipalityService.findById(id);
    }

    private Optional<MunicipalityPart> findMunicipalityPart(long id) {
        return municipalityPartService.findById(id);
    }

    private Optional<District> findDistrict(int id) {
        return districtService.findById(id);
    }
}
