package com.stag.identity.person.service;

import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.model.Addresses;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.service.data.AddressLookupData;
import com.stag.identity.shared.grpc.client.CodelistClient;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CodelistClient codelistClient;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        // Mock TransactionTemplate to execute the callback immediately
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        });
    }

    @Test
    @DisplayName("Should return addresses when person exists")
    void getPersonAddresses_PersonExists_ReturnsAddresses() {
        Integer personId = 123;
        String language = "en";

        AddressView addressView = mock(AddressView.class);
        AddressLookupData lookupData = mock(AddressLookupData.class);

        when(personRepository.findAddressesByPersonId(personId)).thenReturn(Optional.of(addressView));
        when(codelistClient.getPersonAddressData(addressView, language)).thenReturn(lookupData);

        Addresses result = addressService.getPersonAddresses(personId, language);

        assertThat(result).isNotNull();

        verify(transactionTemplate).execute(any());
        verify(personRepository).findAddressesByPersonId(personId);
        verify(codelistClient).getPersonAddressData(addressView, language);
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when person does not exist")
    void getPersonAddresses_PersonNotFound_ThrowsException() {
        Integer personId = 999;
        String language = "cs";

        when(personRepository.findAddressesByPersonId(personId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.getPersonAddresses(personId, language))
            .isInstanceOf(PersonNotFoundException.class)
            .hasMessageContaining(String.valueOf(personId));

        verify(transactionTemplate).execute(any());
        verify(personRepository).findAddressesByPersonId(personId);
        verifyNoInteractions(codelistClient);
    }

    @Test
    @DisplayName("Should propagate exception when repository throws exception")
    void getPersonAddresses_RepositoryThrowsException_PropagatesException() {
        Integer personId = 123;
        String language = "en";

        when(personRepository.findAddressesByPersonId(personId)).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> addressService.getPersonAddresses(personId, language))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(transactionTemplate).execute(any());
    }

    @Test
    @DisplayName("Should propagate exception when client throws exception")
    void getPersonAddresses_ClientThrowsException_PropagatesException() {
        Integer personId = 123;
        String language = "en";
        AddressView addressView = mock(AddressView.class);

        when(personRepository.findAddressesByPersonId(personId)).thenReturn(Optional.of(addressView));
        when(codelistClient.getPersonAddressData(addressView, language)).thenThrow(new RuntimeException("Service unavailable"));

        assertThatThrownBy(() -> addressService.getPersonAddresses(personId, language))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Service unavailable");

        verify(transactionTemplate).execute(any());
        verify(personRepository).findAddressesByPersonId(personId);
        verify(codelistClient).getPersonAddressData(addressView, language);
    }

}
