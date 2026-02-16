package com.stag.academics.student.service;

import com.stag.academics.shared.grpc.client.StudyPlanClient;
import com.stag.academics.shared.grpc.client.UserClient;
import com.stag.academics.student.exception.StudentNotFoundException;
import com.stag.academics.student.exception.StudentProfileFetchException;
import com.stag.academics.student.model.Profile;
import com.stag.academics.student.repository.StudentRepository;
import com.stag.academics.student.repository.projection.ProfileView;
import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private StudyPlanClient studyPlanClient;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionTemplate.execute(any()))
                 .thenAnswer(invocation -> {
                     TransactionCallback<?> callback = invocation.getArgument(0);
                     return callback.doInTransaction(mock(TransactionStatus.class));
                 });
    }

    @Nested
    @DisplayName("findAllStudentIds")
    class FindAllStudentIds {

        @Test
        @DisplayName("should return list of student IDs when found")
        void findAllStudentIds_Found_ReturnsList() {
            Integer personId = 123;
            List<String> expectedIds = List.of("S123", "S456");
            when(studentRepository.findAllStudentIds(personId)).thenReturn(expectedIds);

            List<String> result = studentService.findAllStudentIds(personId);

            assertThat(result).isEqualTo(expectedIds);
            verify(studentRepository).findAllStudentIds(personId);
        }

        @Test
        @DisplayName("should return empty list when no students found")
        void findAllStudentIds_NotFound_ReturnsEmptyList() {
            Integer personId = 123;
            when(studentRepository.findAllStudentIds(personId)).thenReturn(List.of());

            List<String> result = studentService.findAllStudentIds(personId);

            assertThat(result).isEmpty();
            verify(studentRepository).findAllStudentIds(personId);
        }

    }

    @Nested
    @DisplayName("findPersonId")
    class FindPersonId {

        @Test
        @DisplayName("should return person ID when student exists")
        void findPersonId_Found_ReturnsPersonId() {
            String studentId = "S123";
            Integer expectedPersonId = 456;
            when(studentRepository.findPersonId(studentId)).thenReturn(Optional.of(expectedPersonId));

            Integer result = studentService.findPersonId(studentId);

            assertThat(result).isEqualTo(expectedPersonId);
            verify(studentRepository).findPersonId(studentId);
        }

        @Test
        @DisplayName("should throw StudentNotFoundException when student not found")
        void findPersonId_NotFound_ThrowsException() {
            String studentId = "S123";
            when(studentRepository.findPersonId(studentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.findPersonId(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining(studentId);
            verify(studentRepository).findPersonId(studentId);
        }

    }

    @Nested
    @DisplayName("getStudentProfile")
    class GetStudentProfile {

        @Test
        @DisplayName("should return complete profile when all data found")
        void getStudentProfile_AllFound_ReturnsProfile() {
            String studentId = "S123";
            String language = "en";

            ProfileView profileView = Instancio.create(ProfileView.class);
            SimpleProfileLookupData profileData = Instancio.create(SimpleProfileLookupData.class);
            StudyProgramAndFieldLookupData studyData = Instancio.create(StudyProgramAndFieldLookupData.class);

            when(studentRepository.findStudentProfileById(studentId)).thenReturn(Optional.of(profileView));
            when(userClient.getPersonSimpleProfileData(profileView.personId(), language)).thenReturn(profileData);
            when(studyPlanClient.getStudyProgramAndField(profileView.studyProgramId(), profileView.studyPlanId(), language)).thenReturn(studyData);

            Profile result = studentService.getStudentProfile(studentId, language);

            assertThat(result).isNotNull();
            assertThat(result.studentId()).isEqualTo(profileView.studentId());
            assertThat(result.firstName()).isEqualTo(profileData.firstName());
            assertThat(result.lastName()).isEqualTo(profileData.lastName());
            assertThat(result.studyProgram().name()).isEqualTo(studyData.studyProgram().name());

            verify(studentRepository).findStudentProfileById(studentId);
            verify(userClient).getPersonSimpleProfileData(profileView.personId(), language);
            verify(studyPlanClient).getStudyProgramAndField(profileView.studyProgramId(), profileView.studyPlanId(), language);
        }

        @Test
        @DisplayName("should throw StudentNotFoundException when student profile not found in DB")
        void getStudentProfile_StudentNotFound_ThrowsException() {
            String studentId = "S123";
            String language = "en";
            when(studentRepository.findStudentProfileById(studentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.getStudentProfile(studentId, language))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining(studentId);

            verify(studentRepository).findStudentProfileById(studentId);
        }

        @Test
        @DisplayName("should propagate runtime exception when user client fails")
        void getStudentProfile_UserClientFails_PropagatesException() {
            String studentId = "S123";
            String language = "en";
            ProfileView profileView = Instancio.create(ProfileView.class);

            when(studentRepository.findStudentProfileById(studentId)).thenReturn(Optional.of(profileView));
            when(userClient.getPersonSimpleProfileData(profileView.personId(), language)).thenThrow(new RuntimeException("User service failed"));

            // Mock study plan client to not block or fail, though structured task scope might cancel it
            lenient().when(studyPlanClient.getStudyProgramAndField(any(), any(), any()))
                     .thenReturn(Instancio.create(StudyProgramAndFieldLookupData.class));

            assertThatThrownBy(() -> studentService.getStudentProfile(studentId, language))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User service failed");
        }

        @Test
        @DisplayName("should propagate runtime exception when study plan client fails")
        void getStudentProfile_StudyPlanClientFails_PropagatesException() {
            String studentId = "S123";
            String language = "en";
            ProfileView profileView = Instancio.create(ProfileView.class);
            SimpleProfileLookupData profileData = Instancio.create(SimpleProfileLookupData.class);

            when(studentRepository.findStudentProfileById(studentId)).thenReturn(Optional.of(profileView));
            lenient().when(userClient.getPersonSimpleProfileData(profileView.personId(), language))
                     .thenReturn(profileData);
            when(studyPlanClient.getStudyProgramAndField(profileView.studyProgramId(), profileView.studyPlanId(), language))
                .thenThrow(new RuntimeException("Study plan service failed"));

            assertThatThrownBy(() -> studentService.getStudentProfile(studentId, language))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Study plan service failed");
        }

        @Test
        @DisplayName("should wrap checked exception in StudentProfileFetchException")
        void getStudentProfile_CheckedException_WrapsInFetchException() {
            String studentId = "S123";
            String language = "en";
            ProfileView profileView = Instancio.create(ProfileView.class);

            when(studentRepository.findStudentProfileById(studentId)).thenReturn(Optional.of(profileView));
            when(userClient.getPersonSimpleProfileData(any(), any())).thenAnswer(_ -> {
                throw new Exception("Checked error");
            });

            assertThatThrownBy(() -> studentService.getStudentProfile(studentId, language))
                .isInstanceOf(StudentProfileFetchException.class)
                .hasCauseInstanceOf(Exception.class)
                .hasMessageContaining(studentId);
        }

        @Test
        @DisplayName("should handle InterruptedException and re-interrupt thread")
        void getStudentProfile_Interrupted_ThrowsExceptionAndSetsInterruptFlag() {
            String studentId = "S123";
            String language = "en";
            ProfileView profileView = Instancio.create(ProfileView.class);
            CountDownLatch taskStarted = new CountDownLatch(1);
            AtomicReference<Throwable> thrownException = new AtomicReference<>();

            when(studentRepository.findStudentProfileById(studentId)).thenReturn(Optional.of(profileView));
            when(userClient.getPersonSimpleProfileData(any(), any())).thenAnswer(_ -> {
                taskStarted.countDown();
                Thread.sleep(Long.MAX_VALUE);
                return null;
            });

            Thread caller = new Thread(() -> {
                try {
                    studentService.getStudentProfile(studentId, language);
                } catch (Throwable t) {
                    thrownException.set(t);
                }
            });
            caller.start();

            await().atMost(5, SECONDS).until(() -> taskStarted.getCount() == 0);
            caller.interrupt();

            await().atMost(5, SECONDS).until(() -> !caller.isAlive());
            assertThat(thrownException.get())
                .isInstanceOf(StudentProfileFetchException.class)
                .hasCauseInstanceOf(InterruptedException.class);
        }

    }

}
