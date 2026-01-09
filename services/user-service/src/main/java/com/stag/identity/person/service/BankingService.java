package com.stag.identity.person.service;

import com.stag.identity.person.entity.Person;
import com.stag.identity.person.exception.InvalidBankAccountException;
import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.mapper.BankingMapper;
import com.stag.identity.person.model.Banking;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.service.data.BankingLookupData;
import com.stag.identity.person.service.dto.PersonUpdateCommand;
import com.stag.identity.person.util.BankAccountValidator;
import com.stag.identity.shared.grpc.client.CodelistClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/// **Banking Service**
///
/// Business logic for person banking operations.
/// Handles retrieval of banking information with localized bank names,
/// and updates to bank accounts with validation of Czech account numbers and automatic IBAN generation.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class BankingService {

    /// Person Repository
    private final PersonRepository personRepository;

    /// gRPC Codelist Client
    private final CodelistClient codelistClient;

    /// Transaction Template for transaction management
    private final TransactionTemplate transactionTemplate;

    /// Retrieves person's banking information with localized bank names.
    /// Fetches banking projection then loads codelist data for bank codes and account types.
    ///
    /// @param personId the person identifier
    /// @param language the language code for codelist localization
    /// @return banking information with localized data
    /// @throws PersonNotFoundException if person not found
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

        BankingLookupData bankingData = codelistClient.getPersonBankingData(bankView, language);

        Banking banking = BankingMapper.INSTANCE.toPersonBanking(bankView, bankingData);

        log.info("Successfully fetched person banking information for personId: {}", personId);
        return banking;
    }

    /// Updates person bank account information with validation and IBAN generation.
    /// Validates Czech account number checksums, enforces required field combinations, and automatically generates IBAN for Czech accounts.
    /// Allows clearing an account by providing all null values.
    ///
    /// @param person the person entity to update
    /// @param bankAccount the new bank account data
    /// @throws InvalidBankAccountException if an account combination is invalid
    public void updatePersonBankAccount(Person person, PersonUpdateCommand.BankAccount bankAccount) {
        if (bankAccount == null) {
            log.debug("Bank account is null, no updates to perform for personId: {}", person.getId());
            return;
        }

        log.info("Updating bank account for personId: {}", person.getId());

        // Update account holder and address
        person.setAccountHolder(bankAccount.holderName());
        person.setAccountAddress(bankAccount.holderAddress());

        // User wants to remove his bank account (all fields explicitly null)
        if (bankAccount.bankCode() == null && bankAccount.prefix() == null && bankAccount.suffix() == null) {
            clearBankAccount(person);
            return;
        }

        // Validate the bank account numbers combination
        validateBankAccountCombination(bankAccount.prefix(), bankAccount.suffix(), bankAccount.bankCode());

        // Update account numbers with checksum validation
        updateAccountNumbers(person, bankAccount);

        // Update IBAN using current or new values
        updateIban(person, bankAccount);

        log.info("Successfully updated bank account for personId: {}", person.getId());
    }

    /// Clears all bank account fields for the person.
    ///
    /// @param person the person entity to update
    private void clearBankAccount(Person person) {
        log.info("Clearing bank account for personId: {}", person.getId());
        person.setBankCode(null);
        person.setAccountPrefix(null);
        person.setAccountSuffix(null);
        person.setAccountIban(null);
    }

    /// Validates required field combinations for Czech bank account numbers.
    /// Enforces: suffix requires bank code, bank code requires suffix, prefix requires suffix.
    ///
    /// @param prefix the account prefix (optional)
    /// @param suffix the account number (required if bank code present)
    /// @param bankCode the bank code (required if suffix present)
    /// @throws InvalidBankAccountException if a combination is invalid
    private void validateBankAccountCombination(String prefix, String suffix, String bankCode) {
        log.info("Validating bank account combination");

        // If the suffix (account number) is present, bank code must be present
        if (suffix != null && bankCode == null) {
            throw new InvalidBankAccountException("Bank code is mandatory when an account number (suffix) is present.");
        }
        // If bank code is present, a suffix (account number) must be present
        if (bankCode != null && suffix == null) {
            throw new InvalidBankAccountException("Account number (suffix) is mandatory when a bank code is present.");
        }
        // If the prefix is present, the suffix must be present
        if (prefix != null && suffix == null) {
            throw new InvalidBankAccountException("Account number (suffix) is mandatory when a prefix is present.");
        }

        log.debug("Bank account combination validated successfully");
    }

    /// Updates account numbers with checksum validation.
    /// Validates prefix and suffix using Czech bank account checksum algorithm.
    /// Prefix can be cleared by providing a null value.
    ///
    /// @param person the person entity to update
    /// @param bankAccount the new bank account data
    /// @throws InvalidBankAccountException if checksum validation fails
    private static void updateAccountNumbers(Person person, PersonUpdateCommand.BankAccount bankAccount) {
        log.info("Updating account numbers with checksum validation");

        person.setBankCode(bankAccount.bankCode());

        // User can remove the prefix
        if (bankAccount.prefix() == null) {
            person.setAccountPrefix(null);
        }

        // Validate and update prefix
        if (BankAccountValidator.isValidChecksum(bankAccount.prefix())) {
            log.debug("Prefix checksum validation passed");
            person.setAccountPrefix(bankAccount.prefix());
        }
        // Validate and update suffix
        if (BankAccountValidator.isValidChecksum(bankAccount.suffix())) {
            log.debug("Suffix checksum validation passed");
            person.setAccountSuffix(bankAccount.suffix());
        }
    }

    /// Updates IBAN using current or new account values.
    /// Generates Czech IBAN (CZ) from prefix, suffix, and bank code with proper padding.
    /// Clears IBAN if bank code is not present.
    ///
    /// @param person the person entity to update
    /// @param bankAccount the new bank account data
    private void updateIban(Person person, PersonUpdateCommand.BankAccount bankAccount) {
        log.info("Updating IBAN for personId: {}", person.getId());

        String prefix = bankAccount.prefix() != null ? bankAccount.prefix() : person.getAccountPrefix();
        String suffix = bankAccount.suffix() != null ? bankAccount.suffix() : person.getAccountSuffix();
        String bankCode = bankAccount.bankCode() != null ? bankAccount.bankCode() : person.getBankCode();

        Iban iban = generateIban(prefix, suffix, bankCode);

        if (iban == null) {
            log.debug("Clearing IBAN for personId: {}", person.getId());
            person.setAccountIban(null);
            return;
        }

        log.debug("IBAN generated successfully for personId: {}", person.getId());
        person.setAccountIban(iban.toString());
    }

    /// Generates Czech IBAN from account components.
    /// Pads prefix to 6 digits and suffix to 10 digits with leading zeros.
    ///
    /// @param prefix the account prefix (optional, defaults to 0)
    /// @param suffix the account number (optional, defaults to 0)
    /// @param bankCode the bank code (required for IBAN generation)
    /// @return generated IBAN or null if bank code is null
    private Iban generateIban(String prefix, String suffix, String bankCode) {
        if (bankCode == null) {
            log.debug("Bank code is null, cannot generate IBAN");
            return null;
        }

        log.info("Generating IBAN");

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
