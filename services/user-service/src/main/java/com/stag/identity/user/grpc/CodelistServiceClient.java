package com.stag.identity.user.grpc;

import com.stag.identity.user.model.CodelistEntryId;
import com.stag.identity.user.service.data.PersonProfileData;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.CodelistValue;
import com.stag.platform.codelist.v1.GetPersonProfileDataRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CodelistServiceClient {

    @GrpcClient("codelist-service")
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    /**
     * Get codelist data specifically for person profile (GET /persons/{id})
     */
    public PersonProfileData getPersonProfileData(PersonProfileProjection personProfile) {
        // Build codelist keys for profile fields
        List<CodelistKey> codelistKeys = new ArrayList<>();

        // Add title prefix if exists
        if (personProfile.titlePrefix() != null) {
            codelistKeys.add(createCodelistKey("TITUL_PRED", personProfile.titlePrefix()));
        }

        // Add title suffix if exists
        if (personProfile.titleSuffix() != null) {
            codelistKeys.add(createCodelistKey("TITUL_ZA", personProfile.titleSuffix()));
        }

        // Add gender if exists
        if (personProfile.gender() != null) {
            codelistKeys.add(createCodelistKey("POHLAVI", personProfile.gender()));
        }

        // Add marital status if exists
        if (personProfile.maritalStatus() != null) {
            codelistKeys.add(createCodelistKey("STAV", personProfile.maritalStatus()));
        }

        // Add citizenship qualification if exists
        if (personProfile.citizenshipQualification() != null) {
            codelistKeys.add(createCodelistKey("KVANT_OBCAN", personProfile.citizenshipQualification()));
        }

        // Build request
        var requestBuilder = GetPersonProfileDataRequest.newBuilder()
                                                        .addAllCodelistKeys(codelistKeys)
                                                        .setLanguage("cs");

        // Add birth country if exists
        if (personProfile.birthCountryId() != null) {
            requestBuilder.setBirthCountryId(personProfile.birthCountryId());
        }

        // Add citizenship country if exists
        if (personProfile.residenceCountryId() != null) {
            requestBuilder.setCitizenshipCountryId(personProfile.residenceCountryId());
        }

        // Make the call
        var response = codelistServiceStub.getPersonProfileData(requestBuilder.build());

        // Convert response to map
        Map<CodelistEntryId, String> codelistMeanings = response.getCodelistValuesList()
                                                                .stream()
                                                                .collect(Collectors.toMap(
                                                                    this::toCodelistEntryId,
                                                                    CodelistValue::getMeaning
                                                                ));

        // Return the data
        return PersonProfileData.builder()
                                .codelistMeanings(codelistMeanings)
                                .birthCountryName(response.hasBirthCountryName()
                                            ? response.getBirthCountryName().getName()
                                            : null)
                                .citizenshipCountryName(response.hasCitizenshipCountryName()
                                            ? response.getCitizenshipCountryName().getName()
                                            : null)
                                .build();
    }

    private CodelistKey createCodelistKey(String domain, String lowValue) {
        return CodelistKey.newBuilder()
                          .setDomain(domain)
                          .setLowValue(lowValue)
                          .build();
    }

    private CodelistEntryId toCodelistEntryId(CodelistValue codelistValue) {
        return new CodelistEntryId(codelistValue.getDomain(), codelistValue.getLowValue());
    }

//    /**
//     * Get codelist data specifically for person addresses (GET /persons/{id}/addresses)
//     */
//    public PersonAddressCodelistData getPersonAddressCodelistData(Person person) {
//        GetPersonAddressCodelistDataRequest.Builder requestBuilder = GetPersonAddressCodelistDataRequest.newBuilder()
//                                                                                                        .setLanguage("cs");
//
//        // Only add the IDs needed for addresses
//        if (person.getDomicileCountryId() != null) {
//            requestBuilder.setPermanentCountryId(person.getDomicileCountryId());
//        }
//        if (person.getDomicileMunicipalityId() != null) {
//            requestBuilder.setPermanentMunicipalityId(person.getDomicileMunicipalityId());
//        }
//        if (person.getDomicileMunicipalityPartId() != null) {
//            requestBuilder.setPermanentMunicipalityPartId(person.getDomicileMunicipalityPartId());
//        }
//        if (person.getDomicileDistrictId() != null) {
//            requestBuilder.setPermanentDistrictId(person.getDomicileDistrictId());
//        }
//
//        // Temporary residence
//        if (person.getTemporaryCountryId() != null) {
//            requestBuilder.setTemporaryCountryId(person.getTemporaryCountryId());
//        }
//        if (person.getTemporaryMunicipalityId() != null) {
//            requestBuilder.setTemporaryMunicipalityId(person.getTemporaryMunicipalityId());
//        }
//        if (person.getTemporaryMunicipalityPartId() != null) {
//            requestBuilder.setTemporaryMunicipalityPartId(person.getTemporaryMunicipalityPartId());
//        }
//        if (person.getTemporaryDistrictId() != null) {
//            requestBuilder.setTemporaryDistrictId(person.getTemporaryDistrictId());
//        }
//
//        GetPersonAddressCodelistDataResponse response =
//            codelistServiceStub.getPersonAddressCodelistData(requestBuilder.build());
//
//        return PersonAddressCodelistData.builder()
//                                        .permanentCountryName(response.hasPermanentCountry() ? response.getPermanentCountry()
//                                                                                                       .getName() : null)
//                                        .permanentMunicipalityName(response.hasPermanentMunicipality() ?
//                                            response.getPermanentMunicipality()
//                                                    .getName() : null)
//                                        .permanentMunicipalityPartName(response.hasPermanentMunicipalityPart() ?
//                                            response.getPermanentMunicipalityPart()
//                                                    .getName() : null)
//                                        .permanentDistrictName(response.hasPermanentDistrict() ? response.getPermanentDistrict()
//                                                                                                         .getName() : null)
//                                        .temporaryCountryName(response.hasTemporaryCountry() ? response.getTemporaryCountry()
//                                                                                                       .getName() : null)
//                                        .temporaryMunicipalityName(response.hasTemporaryMunicipality() ?
//                                            response.getTemporaryMunicipality()
//                                                    .getName() : null)
//                                        .temporaryMunicipalityPartName(response.hasTemporaryMunicipalityPart() ?
//                                            response.getTemporaryMunicipalityPart()
//                                                    .getName() : null)
//                                        .temporaryDistrictName(response.hasTemporaryDistrict() ? response.getTemporaryDistrict()
//                                                                                                         .getName() : null)
//                                        .build();
//    }
//
//    /**
//     * Get codelist data specifically for person banking (GET /persons/{id}/banking)
//     */
//    public PersonBankingCodelistData getPersonBankingCodelistData(Person person) {
//        // Get banking-specific codelist entries (bank codes, currency codes)
//        Collection<CodelistEntryId> bankingCodelistIds = Arrays.stream(PersonFieldExtractor.values())
//                                                               .filter(extractor -> extractor.getTargetType() == TargetType.CODELIST_ENTRY)
//                                                               .filter(extractor -> {
//                                                                   // TODO: Add banking-specific codelist fields when needed
//                                                                   return false; // No banking fields yet
//                                                               })
//                                                               .map(config -> config.createEntryId(person))
//                                                               .filter(Objects::nonNull)
//                                                               .toList();
//
//        GetPersonBankingCodelistDataRequest request = GetPersonBankingCodelistDataRequest.newBuilder()
//                                                                                         .addAllCodelistKeys(bankingCodelistIds.stream()
//                                                                                                                               .map(this::toGrpcKey)
//                                                                                                                               .toList())
//                                                                                         .setLanguage("cs")
//                                                                                         .build();
//
//        GetPersonBankingCodelistDataResponse response =
//            codelistServiceStub.getPersonBankingCodelistData(request);
//
//        return PersonBankingCodelistData.builder()
//                                        .codelistMeanings(response.getCodelistValuesList()
//                                                                  .stream()
//                                                                  .collect(Collectors.toMap(
//                                                                      this::toInternalKey,
//                                                                      CodelistValue::getMeaning
//                                                                  )))
//                                        .build();
//    }
//
//    /**
//     * Get codelist data specifically for person education (GET /persons/{id}/education)
//     */
//    public PersonEducationCodelistData getPersonEducationCodelistData(Person person) {
//        // Get education-specific codelist entries
//        Collection<CodelistEntryId> educationCodelistIds = Arrays.stream(PersonFieldExtractor.values())
//                                                                 .filter(extractor -> extractor.getTargetType() == TargetType.CODELIST_ENTRY)
//                                                                 .filter(extractor -> {
//                                                                     // TODO: Add education-specific codelist fields when needed
//                                                                     return false; // No education fields yet
//                                                                 })
//                                                                 .map(config -> config.createEntryId(person))
//                                                                 .filter(Objects::nonNull)
//                                                                 .toList();
//
//        GetPersonEducationCodelistDataRequest.Builder requestBuilder = GetPersonEducationCodelistDataRequest.newBuilder()
//                                                                                                            .addAllCodelistKeys(educationCodelistIds.stream()
//                                                                                                                                                    .map(this::toGrpcKey)
//                                                                                                                                                    .toList())
//                                                                                                            .setLanguage("cs");
//
//        // Add high school country if available
//        if (person.getHighSchoolCountryId() != null) {
//            requestBuilder.setHighSchoolCountryId(person.getHighSchoolCountryId());
//        }
//
//        GetPersonEducationCodelistDataResponse response =
//            codelistServiceStub.getPersonEducationCodelistData(requestBuilder.build());
//
//        return PersonEducationCodelistData.builder()
//                                          .codelistMeanings(response.getCodelistValuesList()
//                                                                    .stream()
//                                                                    .collect(Collectors.toMap(
//                                                                        this::toInternalKey,
//                                                                        CodelistValue::getMeaning
//                                                                    )))
//                                          .highSchoolCountryName(response.hasHighSchoolCountry() ?
//                                              response.getHighSchoolCountry()
//                                                      .getName() : null)
//                                          .build();
//    }

}
