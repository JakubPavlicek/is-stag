package com.stag.identity.person.service;

import com.stag.identity.person.entity.Person;
import com.stag.identity.person.exception.InvalidBankAccountException;
import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.model.Banking;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.service.data.BankingLookupData;
import com.stag.identity.person.service.dto.PersonUpdateCommand;
import com.stag.identity.shared.grpc.client.CodelistClient;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankingServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CodelistClient codelistClient;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private BankingService bankingService;

    @BeforeEach
    void setUp() {
        // Lenient stubbing for TransactionTemplate as it might not be used in all tests
        lenient().when(transactionTemplate.execute(any()))
                 .thenAnswer(invocation -> {
                     TransactionCallback<?> callback = invocation.getArgument(0);
                     return callback.doInTransaction(mock(TransactionStatus.class));
                 });
    }

    @Test
    @DisplayName("Should return banking info when person exists")
    void getPersonBanking_PersonExists_ReturnsBanking() {
        Integer personId = 123;
        String language = "en";

        BankView bankView = mock(BankView.class);
        BankingLookupData lookupData = mock(BankingLookupData.class);

        when(personRepository.findBankingByPersonId(personId)).thenReturn(Optional.of(bankView));
        when(codelistClient.getPersonBankingData(bankView, language)).thenReturn(lookupData);

        Banking result = bankingService.getPersonBanking(personId, language);

        assertThat(result).isNotNull();
        verify(personRepository).findBankingByPersonId(personId);
        verify(codelistClient).getPersonBankingData(bankView, language);
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when person does not exist")
    void getPersonBanking_PersonNotFound_ThrowsException() {
        Integer personId = 999;
        String language = "cs";

        when(personRepository.findBankingByPersonId(personId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankingService.getPersonBanking(personId, language))
            .isInstanceOf(PersonNotFoundException.class)
            .hasMessageContaining(String.valueOf(personId));
    }

    @Test
    @DisplayName("Should do nothing when bank account input is null")
    void updatePersonBankAccount_NullInput_DoesNothing() {
        Person person = new Person();
        person.setId(1);

        bankingService.updatePersonBankAccount(person, null);

        assertThat(person.getAccountHolder()).isNull();
    }

    @Test
    @DisplayName("Should clear bank account when all fields are null")
    void updatePersonBankAccount_AllNulls_ClearsAccount() {
        Person person = new Person();
        person.setId(1);
        person.setBankCode("0100");
        person.setAccountPrefix("123");
        person.setAccountSuffix("456");
        person.setAccountIban("CZ...");

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            null, null, null, "Holder", "Address"
        );

        bankingService.updatePersonBankAccount(person, input);

        assertThat(person.getBankCode()).isNull();
        assertThat(person.getAccountPrefix()).isNull();
        assertThat(person.getAccountSuffix()).isNull();
        assertThat(person.getAccountIban()).isNull();
        assertThat(person.getAccountHolder()).isEqualTo("Holder");
        assertThat(person.getAccountAddress()).isEqualTo("Address");
    }

    @Test
    @DisplayName("Should throw exception when suffix is present but bank code is missing")
    void updatePersonBankAccount_SuffixWithoutBankCode_ThrowsException() {
        Person person = new Person();

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            null, "123456", null, null, null
        );

        assertThatThrownBy(() -> bankingService.updatePersonBankAccount(person, input))
            .isInstanceOf(InvalidBankAccountException.class)
            .hasMessageContaining("Bank code is mandatory");
    }

    @Test
    @DisplayName("Should throw exception when bank code is present but suffix is missing")
    void updatePersonBankAccount_BankCodeWithoutSuffix_ThrowsException() {
        Person person = new Person();

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            null, null, "0100", null, null
        );

        assertThatThrownBy(() -> bankingService.updatePersonBankAccount(person, input))
            .isInstanceOf(InvalidBankAccountException.class)
            .hasMessageContaining("Account number (suffix) is mandatory");
    }

    @Test
    @DisplayName("Should throw exception when prefix is present but suffix is missing")
    void updatePersonBankAccount_PrefixWithoutSuffix_ThrowsException() {
        Person person = new Person();

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            "123", null, "0100", null, null
        );

        assertThatThrownBy(() -> bankingService.updatePersonBankAccount(person, input))
            .isInstanceOf(InvalidBankAccountException.class)
            .hasMessageContaining("Account number (suffix) is mandatory");
    }

    @Test
    @DisplayName("Should update account and generate IBAN when valid data is provided")
    void updatePersonBankAccount_ValidData_UpdatesAndGeneratesIban() {
        Person person = new Person();
        person.setId(1);

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            "0", "0", "0100", "New Holder", "New Address"
        );

        bankingService.updatePersonBankAccount(person, input);

        assertThat(person.getAccountHolder()).isEqualTo("New Holder");
        assertThat(person.getAccountAddress()).isEqualTo("New Address");
        assertThat(person.getBankCode()).isEqualTo("0100");
        assertThat(person.getAccountPrefix()).isEqualTo("0");
        assertThat(person.getAccountSuffix()).isEqualTo("0");

        Iban expectedIban = new Iban.Builder()
            .countryCode(CountryCode.CZ)
            .bankCode("0100")
            .accountNumber("0000000000000000")
            .build();
        assertThat(person.getAccountIban()).isEqualTo(expectedIban.toString());
    }

    @Test
    @DisplayName("Should remove prefix when input prefix is null but other fields present")
    void updatePersonBankAccount_NullPrefix_RemovesPrefix() {
        Person person = new Person();
        person.setAccountPrefix("0");
        person.setAccountSuffix("0");
        person.setBankCode("0100");

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            null, "0", "0800", "Holder", "Address"
        );

        bankingService.updatePersonBankAccount(person, input);

        assertThat(person.getAccountPrefix()).isNull();
        assertThat(person.getAccountSuffix()).isEqualTo("0");
        assertThat(person.getBankCode()).isEqualTo("0800");
    }

    @Test
    @DisplayName("Should not clear bank account if prefix is provided but others are null")
    void updatePersonBankAccount_OnlyPrefixProvided_DoesNotHitClearBranch() {
        Person person = new Person();
        person.setBankCode("0100");

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            "0", null, null, null, null
        );

        assertThatThrownBy(() -> bankingService.updatePersonBankAccount(person, input))
            .isInstanceOf(InvalidBankAccountException.class)
            .hasMessageContaining("Account number (suffix) is mandatory");

        assertThat(person.getBankCode()).isEqualTo("0100");
    }

    @Test
    @DisplayName("Should use existing person values for IBAN generation when input fields are null")
    void updatePersonBankAccount_UsesExistingValuesForIban() {
        Person person = new Person();
        person.setBankCode("0100");
        person.setAccountPrefix("123");
        person.setAccountSuffix("123");

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            "123", "123", "0800", null, null
        );

        bankingService.updatePersonBankAccount(person, input);

        assertThat(person.getAccountIban()).contains("0001230000000123");
    }

    @Test
    @DisplayName("Should clear IBAN if bank code becomes null during update")
    void updatePersonBankAccount_NoBankCode_ClearsIban() {
        Person person = new Person();
        person.setAccountIban("CZ123...");

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            null, null, null, null, null
        );

        bankingService.updatePersonBankAccount(person, input);

        assertThat(person.getAccountIban()).isNull();
    }

    @Test
    @DisplayName("Should update prefix even if suffix validation fails later (in-memory state)")
    void updatePersonBankAccount_ValidPrefixInvalidSuffix_UpdatesPrefixThenThrows() {
        Person person = new Person();
        person.setAccountPrefix("999");

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            "0", "1", "0100", null, null
        );

        assertThatThrownBy(() -> bankingService.updatePersonBankAccount(person, input))
            .isInstanceOf(RuntimeException.class);

        assertThat(person.getAccountPrefix()).isEqualTo("0");
        assertThat(person.getAccountSuffix()).isNull();
    }

    @Test
    @DisplayName("Should throw exception when suffix checksum is invalid")
    void updatePersonBankAccount_InvalidSuffixChecksum_ThrowsException() {
        Person person = new Person();

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            "0", "1", "0100", null, null
        );

        assertThatThrownBy(() -> bankingService.updatePersonBankAccount(person, input))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("1");
    }

    @Test
    @DisplayName("Should throw exception when prefix checksum is invalid")
    void updatePersonBankAccount_InvalidPrefixChecksum_ThrowsException() {
        Person person = new Person();

        PersonUpdateCommand.BankAccount input = new PersonUpdateCommand.BankAccount(
            "1", "0", "0100", null, null
        );

        assertThatThrownBy(() -> bankingService.updatePersonBankAccount(person, input))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("1");
    }

}
