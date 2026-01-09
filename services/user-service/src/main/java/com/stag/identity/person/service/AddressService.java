package com.stag.identity.person.service;

import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.mapper.AddressMapper;
import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.service.data.AddressLookupData;
import com.stag.identity.shared.grpc.client.CodelistClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/// **Address Service**
///
/// Business logic for person address operations. Retrieves permanent, temporary,
/// and foreign addresses with localized country/state data from codelist service.
/// Results are cached per person ID and language for performance.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class AddressService {

    /// Person repository
    private final PersonRepository personRepository;

    /// gRPC Codelist Client
    private final CodelistClient codelistClient;

    /// Transaction Template for transaction management
    private final TransactionTemplate transactionTemplate;

    /// Retrieves all person addresses with localized country and state names.
    /// Fetches address projection then asynchronously loads codelist data
    /// for all address types (permanent, temporary, foreign).
    ///
    /// @param personId the person identifier
    /// @param language the language code for codelist localization
    /// @return addresses with localized geographic data
    /// @throws PersonNotFoundException if person not found
    @Cacheable(value = "person-addresses", key = "{#personId, #language}")
    @PreAuthorize("""
        hasAnyRole('AD', 'DE', 'PR', 'SR', 'SP', 'VY', 'VK')
        || @authorizationService.isStudentAndOwner(hasRole('ST'), principal.claims['studentId'], #personId)
    """)
    public Addresses getPersonAddresses(Integer personId, String language) {
        log.info("Fetching person addresses for personId: {} with language: {}", personId, language);

        AddressView addressView = transactionTemplate.execute(status ->
            personRepository.findAddressesByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId))
        );

        AddressLookupData addressData = codelistClient.getPersonAddressData(addressView, language);

        Addresses addresses = AddressMapper.INSTANCE.toPersonAddresses(addressView, addressData);

        log.info("Successfully fetched person addresses for personId: {}", personId);
        return addresses;
    }

}
