package com.stag.identity.person.service;

import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.model.Education;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.EducationView;
import com.stag.identity.person.service.data.EducationLookupData;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CodelistClient codelistClient;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private EducationService educationService;

    @BeforeEach
    void setUp() {
        // Mock TransactionTemplate to execute the callback immediately
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        });
    }

    @Test
    @DisplayName("Should return education when person exists")
    void getPersonEducation_PersonExists_ReturnsEducation() {
        Integer personId = 123;
        String language = "en";

        EducationView educationView = mock(EducationView.class);
        EducationLookupData lookupData = mock(EducationLookupData.class);

        when(personRepository.findEducationByPersonId(personId)).thenReturn(Optional.of(educationView));
        when(codelistClient.getPersonEducationData(educationView, language)).thenReturn(lookupData);

        Education result = educationService.getPersonEducation(personId, language);

        assertThat(result).isNotNull();

        verify(transactionTemplate).execute(any());
        verify(personRepository).findEducationByPersonId(personId);
        verify(codelistClient).getPersonEducationData(educationView, language);
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when person does not exist")
    void getPersonEducation_PersonNotFound_ThrowsException() {
        Integer personId = 999;
        String language = "cs";

        when(personRepository.findEducationByPersonId(personId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> educationService.getPersonEducation(personId, language))
            .isInstanceOf(PersonNotFoundException.class)
            .hasMessageContaining(String.valueOf(personId));

        verify(transactionTemplate).execute(any());
        verify(personRepository).findEducationByPersonId(personId);
        verifyNoInteractions(codelistClient);
    }

    @Test
    @DisplayName("Should propagate exception when repository throws exception")
    void getPersonEducation_RepositoryThrowsException_PropagatesException() {
        Integer personId = 123;
        String language = "en";

        when(personRepository.findEducationByPersonId(personId)).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> educationService.getPersonEducation(personId, language))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(transactionTemplate).execute(any());
    }

    @Test
    @DisplayName("Should propagate exception when client throws exception")
    void getPersonEducation_ClientThrowsException_PropagatesException() {
        Integer personId = 123;
        String language = "en";
        EducationView educationView = mock(EducationView.class);

        when(personRepository.findEducationByPersonId(personId)).thenReturn(Optional.of(educationView));
        when(codelistClient.getPersonEducationData(educationView, language)).thenThrow(new RuntimeException("Service unavailable"));

        assertThatThrownBy(() -> educationService.getPersonEducation(personId, language))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Service unavailable");

        verify(transactionTemplate).execute(any());
        verify(personRepository).findEducationByPersonId(personId);
        verify(codelistClient).getPersonEducationData(educationView, language);
    }

}
