package com.stag.identity.person.service;

import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.mapper.BankingMapper;
import com.stag.identity.person.model.Banking;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.service.data.BankingLookupData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class BankingService {

    private final PersonRepository personRepository;
    private final CodelistLookupService codelistLookupService;

    @Cacheable(value = "person-banking", key = "#personId + ':' + #language")
    @PreAuthorize("""
        hasAnyRole('AD', 'DE', 'PR', 'SR', 'SP', 'VY', 'VK')
        || @authorizationService.isStudentAndOwner(hasRole('ST'), principal.claims['studentId'], #personId)
    """)
    public Banking getPersonBanking(Integer personId, String language) {
        log.info("Fetching person banking information for personId: {} with language: {}", personId, language);

        BankView bankView =
            personRepository.findBankingByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId));

        log.debug("Person banking information found, fetching additional data for personId: {}", personId);

        CompletableFuture<BankingLookupData> bankingDataFuture =
            codelistLookupService.getPersonBankingData(bankView, language);

        BankingLookupData bankingData = bankingDataFuture.join();

        log.debug("Additional data fetched, mapping to PersonBanking for personId: {}", personId);
        Banking banking = BankingMapper.INSTANCE.toPersonBanking(
            bankView, bankingData
        );

        log.info("Successfully fetched person banking information for personId: {}", personId);
        return banking;
    }

}
