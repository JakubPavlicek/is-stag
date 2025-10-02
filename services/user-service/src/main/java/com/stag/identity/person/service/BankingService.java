package com.stag.identity.person.service;

import com.stag.identity.person.entity.Person;
import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.mapper.BankingMapper;
import com.stag.identity.person.model.Banking;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.service.data.BankingLookupData;
import com.stag.identity.person.service.dto.PersonUpdateCommand;
import com.stag.identity.person.util.BankAccountValidator;
import com.stag.identity.shared.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class BankingService {

    private final PersonRepository personRepository;
    private final CodelistLookupService codelistLookupService;

    private final TransactionTemplate transactionTemplate;

    @Cacheable(value = "person-banking", key = "{#personId, #language}")
    @PreAuthorize("""
        hasAnyRole('AD', 'DE', 'PR', 'SR', 'SP', 'VY', 'VK')
        || @authorizationService.isStudentAndOwner(hasRole('ST'), principal.claims['studentId'], #personId)
    """)
    public Banking getPersonBanking(Integer personId, String language) {
        log.info("Fetching person banking information for personId: {} with language: {}", personId, language);

        BankView bankView = transactionTemplate.execute(status ->
            personRepository.findBankingByPersonId(personId)
                            .orElseThrow(() -> new PersonNotFoundException(personId))
        );

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

    public void updatePersonBankAccount(Person person, PersonUpdateCommand.BankAccount bankAccount) {
        if (bankAccount == null) {
            log.debug("Bank account is null, no updates to perform for personId: {}", person.getId());
            return;
        }

        // Update basic fields using an Optional-like pattern
        ObjectUtils.updateIfNotNull(bankAccount.bankCode(), person::setBankCode);
        ObjectUtils.updateIfNotNull(bankAccount.holderName(), person::setAccountHolder);
        ObjectUtils.updateIfNotNull(bankAccount.holderAddress(), person::setAccountAddress);

        // Update account number components with validation
        updateAccountNumber(person, bankAccount);

        // Update IBAN using current or new values
        updateIban(person, bankAccount);

        log.info("Successfully updated bank account for personId: {}", person.getId());
    }

    private void updateIban(Person person, PersonUpdateCommand.BankAccount bankAccount) {
        String prefix = ObjectUtils.getValueOrDefault(bankAccount.prefix(), person.getAccountPrefix());
        String suffix = ObjectUtils.getValueOrDefault(bankAccount.suffix(), person.getAccountSuffix());
        String bankCode = ObjectUtils.getValueOrDefault(bankAccount.bankCode(), person.getBankCode());

        Iban iban = generateIban(prefix, suffix, bankCode);

        if (iban != null) {
            person.setAccountIban(iban.toString());
        }
    }

    private static void updateAccountNumber(Person person, PersonUpdateCommand.BankAccount bankAccount) {
        if (BankAccountValidator.isValidChecksum(bankAccount.prefix())) {
            person.setAccountPrefix(bankAccount.prefix());
        }
        if (BankAccountValidator.isValidChecksum(bankAccount.suffix())) {
            person.setAccountSuffix(bankAccount.suffix());
        }
    }

    private Iban generateIban(String prefix, String suffix, String bankCode) {
        if (bankCode == null) {
            return null;
        }

        long prefixNum = prefix == null ? 0 : Long.parseLong(prefix);
        long mainNum = suffix == null ? 0 : Long.parseLong(suffix);

        // Pad with leading zeros to the required length
        String paddedPrefix = String.format("%06d", prefixNum);
        String paddedMainNumber = String.format("%010d", mainNum);

        // The national account number for iban4j is the combination of the padded parts
        String nationalAccountNumber = paddedPrefix + paddedMainNumber;

        return new Iban.Builder()
            .countryCode(CountryCode.CZ)
            .accountNumber(nationalAccountNumber)
            .bankCode(bankCode)
            .build(true);
    }

}
