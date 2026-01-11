package com.stag.identity.person.service;

import com.stag.identity.person.entity.Person;
import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.exception.PersonProfileFetchException;
import com.stag.identity.person.model.Profile;
import com.stag.identity.person.model.SimpleProfile;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.ProfileView;
import com.stag.identity.person.repository.projection.SimpleProfileView;
import com.stag.identity.person.service.data.CodelistMeaningsLookupData;
import com.stag.identity.person.service.data.ProfileLookupData;
import com.stag.identity.person.service.data.ProfileUpdateLookupData;
import com.stag.identity.person.service.dto.PersonUpdateCommand;
import com.stag.identity.shared.grpc.client.CodelistClient;
import com.stag.identity.shared.grpc.client.StudentClient;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private BankingService bankingService;

    @Mock
    private StudentClient studentClient;

    @Mock
    private CodelistClient codelistClient;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        // Lenient stubbing for TransactionTemplate
        lenient().when(transactionTemplate.execute(any()))
                 .thenAnswer(invocation -> {
                     TransactionCallback<?> callback = invocation.getArgument(0);
                     return callback.doInTransaction(mock(TransactionStatus.class));
                 });
    }

    @Test
    @DisplayName("Should return full profile when person exists and tasks succeed")
    void getPersonProfile_Success_ReturnsProfile() {
        Integer personId = 123;
        String language = "en";
        ProfileView profileView = mock(ProfileView.class);
        List<String> studentIds = List.of("ST123");
        ProfileLookupData profileLookupData = mock(ProfileLookupData.class);

        when(personRepository.findById(personId, ProfileView.class)).thenReturn(Optional.of(profileView));
        when(studentClient.getStudentIds(personId)).thenReturn(studentIds);
        when(codelistClient.getPersonProfileData(profileView, language)).thenReturn(profileLookupData);

        Profile result = profileService.getPersonProfile(personId, language);

        assertThat(result).isNotNull();

        verify(transactionTemplate).execute(any());
        verify(studentClient).getStudentIds(personId);
        verify(codelistClient).getPersonProfileData(profileView, language);
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when person not found (full profile)")
    void getPersonProfile_PersonNotFound_ThrowsException() {
        Integer personId = 999;
        String language = "en";

        when(personRepository.findById(personId, ProfileView.class)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getPersonProfile(personId, language))
            .isInstanceOf(PersonNotFoundException.class)
            .hasMessageContaining(String.valueOf(personId));

        verifyNoInteractions(studentClient, codelistClient);
    }

    @Test
    @DisplayName("Should propagate RuntimeException from tasks as-is")
    void getPersonProfile_TaskThrowsRuntimeException_PropagatesIt() {
        Integer personId = 123;
        String language = "en";
        ProfileView profileView = mock(ProfileView.class);

        when(personRepository.findById(personId, ProfileView.class)).thenReturn(Optional.of(profileView));
        when(studentClient.getStudentIds(personId)).thenThrow(new RuntimeException("Service failure"));

        assertThatThrownBy(() -> profileService.getPersonProfile(personId, language))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Service failure");
    }

    @Test
    @DisplayName("Should wrap unknown checked exceptions in PersonProfileFetchException")
    void getPersonProfile_TaskThrowsCheckedException_WrapsInException() {
        Integer personId = 123;
        String language = "en";
        ProfileView profileView = mock(ProfileView.class);

        when(personRepository.findById(personId, ProfileView.class)).thenReturn(Optional.of(profileView));
        when(studentClient.getStudentIds(personId)).thenAnswer(_ -> {
            throw new Exception("Checked exception");
        });

        assertThatThrownBy(() -> profileService.getPersonProfile(personId, language))
            .isInstanceOf(PersonProfileFetchException.class)
            .hasCauseInstanceOf(Exception.class)
            .hasMessageContaining(String.valueOf(personId));
    }

    @Test
    @DisplayName("Should handle InterruptedException and re-interrupt thread")
    void getPersonProfile_Interrupted_ThrowsExceptionAndSetsInterruptFlag() {
        Integer personId = 123;
        String language = "en";
        ProfileView profileView = mock(ProfileView.class);

        when(personRepository.findById(personId, ProfileView.class)).thenReturn(Optional.of(profileView));

        // Interrupt the thread before calling method to trigger InterruptedException in scope.join()
        Thread.currentThread().interrupt();

        assertThatThrownBy(() -> profileService.getPersonProfile(personId, language))
            .isInstanceOf(PersonProfileFetchException.class)
            .hasCauseInstanceOf(InterruptedException.class);

        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    @DisplayName("Should return simple profile when person exists")
    void getPersonSimpleProfile_Success_ReturnsSimpleProfile() {
        Integer personId = 123;
        String language = "en";
        SimpleProfileView simpleView = mock(SimpleProfileView.class);
        CodelistMeaningsLookupData lookupData = mock(CodelistMeaningsLookupData.class);

        when(personRepository.findById(personId, SimpleProfileView.class)).thenReturn(Optional.of(simpleView));
        when(codelistClient.getSimpleProfileData(simpleView, language)).thenReturn(lookupData);

        SimpleProfile result = profileService.getPersonSimpleProfile(personId, language);

        assertThat(result).isNotNull();
        verify(personRepository).findById(personId, SimpleProfileView.class);
        verify(codelistClient).getSimpleProfileData(simpleView, language);
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when person not found (simple profile)")
    void getPersonSimpleProfile_PersonNotFound_ThrowsException() {
        Integer personId = 999;
        String language = "cs";

        when(personRepository.findById(personId, SimpleProfileView.class)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getPersonSimpleProfile(personId, language))
            .isInstanceOf(PersonNotFoundException.class);
    }

    @Test
    @DisplayName("Should update profile with valid data")
    void updatePersonProfile_ValidData_UpdatesPerson() {
        Integer personId = 123;
        Person person = new Person();
        person.setId(personId);

        Profile.Contact contact = new Profile.Contact("email@test.com", "123", "456", "aaaaaaa");
        PersonUpdateCommand.BankAccount bankAccount = mock(PersonUpdateCommand.BankAccount.class);
        Profile.BirthPlace birthPlace = new Profile.BirthPlace("City", "CZ");
        Profile.Titles titles = new Profile.Titles("Ing.", "Mgr.");

        PersonUpdateCommand command = new PersonUpdateCommand(
            "NewSurname",
            "Single",
            contact,
            titles,
            birthPlace,
            bankAccount
        );

        ProfileUpdateLookupData lookupData = new ProfileUpdateLookupData("S", "10", "20", 1);

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(codelistClient.getPersonProfileUpdateData(any(), any(), any())).thenReturn(lookupData);

        profileService.updatePersonProfile(personId, command);

        assertThat(person.getBirthSurname()).isEqualTo("NewSurname");
        assertThat(person.getEmail()).isEqualTo("email@test.com");
        assertThat(person.getDataBox()).isEqualTo("aaaaaaa");
        assertThat(person.getBirthPlace()).isEqualTo("City");
        assertThat(person.getMaritalStatus()).isEqualTo("S");
        assertThat(person.getTitlePrefix()).isEqualTo("10");
        assertThat(person.getBirthCountryId()).isEqualTo(1);

        verify(bankingService).updatePersonBankAccount(person, bankAccount);
        verify(codelistClient).getPersonProfileUpdateData("Single", "CZ", titles);
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException on update if person missing")
    void updatePersonProfile_PersonNotFound_ThrowsException() {
        Integer personId = 999;
        PersonUpdateCommand command = mock(PersonUpdateCommand.class);

        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.updatePersonProfile(personId, command))
            .isInstanceOf(PersonNotFoundException.class);
    }

    @Test
    @DisplayName("Should skip contact update if contact is null")
    void updatePersonProfile_NullContact_DoesNotUpdateContact() {
        Integer personId = 123;
        Person person = new Person();
        person.setEmail("old@test.com");

        PersonUpdateCommand command = new PersonUpdateCommand(
            "Surname",
            null,
            null,
            null,
            null,
            null
        );

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));

        profileService.updatePersonProfile(personId, command);

        assertThat(person.getEmail()).isEqualTo("old@test.com");
    }

    @Test
    @DisplayName("Should clear data box if provided data box is null in contact")
    void updatePersonProfile_NullDataBox_ClearsDataBox() {
        Integer personId = 123;
        Person person = new Person();
        person.setDataBox("aaaaaaa");

        Profile.Contact contact = new Profile.Contact("e", "p", "m", null);
        PersonUpdateCommand command = new PersonUpdateCommand(
            "S", "single", contact, null, null, null
        );

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));

        profileService.updatePersonProfile(personId, command);

        assertThat(person.getDataBox()).isNull();
    }

    @Test
    @DisplayName("Should not update data box if provided ID is invalid")
    void updatePersonProfile_InvalidDataBox_DoesNotUpdateDataBox() {
        Integer personId = 123;
        Person person = new Person();
        person.setDataBox("aaaaaaa");

        Profile.Contact contact = new Profile.Contact("e", "p", "m", "invalid");
        PersonUpdateCommand command = new PersonUpdateCommand(
            "S", "single", contact, null, null, null
        );

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));

        profileService.updatePersonProfile(personId, command);

        assertThat(person.getDataBox()).isEqualTo("aaaaaaa");
    }

    @Test
    @DisplayName("Should not update valid fields if codelist lookup returns null")
    void updatePersonProfile_NullLookupData_DoesNotUpdateValidatedFields() {
        Integer personId = 123;
        Person person = new Person();
        person.setMaritalStatus("Old");

        PersonUpdateCommand command = new PersonUpdateCommand(
            "S", "NewStatus", null, null, null, null
        );

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(codelistClient.getPersonProfileUpdateData(any(), any(), any())).thenReturn(null);

        profileService.updatePersonProfile(personId, command);

        assertThat(person.getMaritalStatus()).isEqualTo("Old");
    }

}
