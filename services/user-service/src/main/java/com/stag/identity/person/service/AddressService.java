package com.stag.identity.person.service;

import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.mapper.AddressMapper;
import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.service.data.AddressLookupData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressService {

    private final PersonRepository personRepository;
    private final CodelistLookupService codelistLookupService;

    private final TransactionTemplate transactionTemplate;

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

        log.debug("Person addresses found, fetching additional data for personId: {}", personId);

        CompletableFuture<AddressLookupData> addressDataFuture =
            codelistLookupService.getPersonAddressData(addressView, language);

        AddressLookupData addressData = addressDataFuture.join();

        log.debug("Additional data fetched, mapping to PersonAddresses for personId: {}", personId);
        Addresses addresses = AddressMapper.INSTANCE.toPersonAddresses(
            addressView, addressData
        );

        log.info("Successfully fetched person addresses for personId: {}", personId);
        return addresses;
    }

}
