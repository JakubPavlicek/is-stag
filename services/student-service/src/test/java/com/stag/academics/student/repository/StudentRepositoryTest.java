package com.stag.academics.student.repository;

import com.stag.academics.config.TestCacheConfig;
import com.stag.academics.config.TestOracleContainerConfig;
import com.stag.academics.student.entity.Student;
import com.stag.academics.student.entity.StudentEnrollment;
import com.stag.academics.student.entity.StudentEnrollmentId;
import com.stag.academics.student.repository.projection.ProfileView;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class })
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("findAllStudentIds")
    class FindAllStudentIds {

        @Test
        @DisplayName("should return all student IDs for a given person ID")
        void shouldReturnAllStudentIdsForPerson() {
            createAndPersistStudent("S001", 123);
            createAndPersistStudent("S002", 123);
            createAndPersistStudent("S003", 456);

            List<String> studentIds = studentRepository.findAllStudentIds(123);

            assertThat(studentIds).containsExactlyInAnyOrder("S001", "S002");
        }

        @Test
        @DisplayName("should return empty list when person has no students")
        void shouldReturnEmptyListWhenPersonHasNoStudents() {
            createAndPersistStudent("S001", 123);

            List<String> studentIds = studentRepository.findAllStudentIds(999);

            assertThat(studentIds).isEmpty();
        }

    }

    @Nested
    @DisplayName("findPersonId")
    class FindPersonId {

        @Test
        @DisplayName("should return person ID for a given student ID")
        void shouldReturnPersonIdForStudent() {
            createAndPersistStudent("S001", 123);

            Optional<Integer> personId = studentRepository.findPersonId("S001");

            assertThat(personId).isPresent()
                                .contains(123);
        }

        @Test
        @DisplayName("should return empty optional when student does not exist")
        void shouldReturnEmptyWhenStudentDoesNotExist() {
            Optional<Integer> personId = studentRepository.findPersonId("NONEXISTENT");

            assertThat(personId).isEmpty();
        }

    }

    @Nested
    @DisplayName("findStudentProfileById")
    class FindStudentProfileById {

        @Test
        @DisplayName("should return student profile with latest enrollment data")
        void shouldReturnStudentProfileWithLatestEnrollment() {
            createAndPersistStudent("S001", 123);

            // Old enrollment
            createAndPersistEnrollment("S001", "2022", 1001L);
            // Latest enrollment
            createAndPersistEnrollment("S001", "2023", 1002L);

            Optional<ProfileView> profileView = studentRepository.findStudentProfileById("S001");

            assertThat(profileView).isPresent();
            assertThat(profileView.get().studentId()).isEqualTo("S001");
            assertThat(profileView.get().personId()).isEqualTo(123);
            assertThat(profileView.get().studyStatus()).isEqualTo("N");
            assertThat(profileView.get().studyPlanId()).isEqualTo(1002L);
        }

        @Test
        @DisplayName("should return empty when student has no enrollments")
        void shouldReturnEmptyWhenStudentHasNoEnrollments() {
            createAndPersistStudent("S001", 123);

            // No enrollments created

            Optional<ProfileView> profileView = studentRepository.findStudentProfileById("S001");

            assertThat(profileView).isEmpty();
        }

        @Test
        @DisplayName("should return empty when student does not exist")
        void shouldReturnEmptyWhenStudentDoesNotExist() {
            Optional<ProfileView> profileView = studentRepository.findStudentProfileById("NONEXISTENT");

            assertThat(profileView).isEmpty();
        }

    }

    private void createAndPersistStudent(String studentId, Integer personId) {
        Student student = Student.builder()
                                 .id(studentId)
                                 .owner("TEST")
                                 .dateOfInsert(LocalDate.now())
                                 .graduate("N")
                                 .newlyAdmitted("A")
                                 .reported("A")
                                 .studyStatus("N")
                                 .personId(personId)
                                 .studyProgramId(1L)
                                 .previousStudyDuration(BigDecimal.ZERO)
                                 .reportToHealthInsurance("A")
                                 .payForExceedingStudyTime("N")
                                 .payForFurtherStudy("N")
                                 .previousEducationLevel("K")
                                 .fileIsClosed("N")
                                 .currentStatus("N")
                                 .conditionalEnrollment("N")
                                 .isJointDegree("N")
                                 .build();

        studentRepository.save(student);
    }

    private void createAndPersistEnrollment(String studentId, String yearOfValidity, Long studyPlanId) {
        StudentEnrollmentId id = new StudentEnrollmentId();
        id.setStudentId(studentId);
        id.setYearOfValidity(yearOfValidity);
        id.setStudyPlanId(studyPlanId);

        StudentEnrollment enrollment = new StudentEnrollment();
        enrollment.setId(id);
        enrollment.setOwner("TEST");
        enrollment.setDateOfInsert(LocalDate.now());
        enrollment.setCreditsEarned(BigDecimal.ZERO);
        enrollment.setCreditsPlanned(BigDecimal.ZERO);
        enrollment.setGradeAverage(BigDecimal.ZERO);
        enrollment.setYearOfStudy((short) 1);
        enrollment.setUnclassifiedCredits((short) 0);
        enrollment.setCreditsRegistered(BigDecimal.ZERO);
        enrollment.setRecognizedGradeAverage(BigDecimal.ZERO);
        enrollment.setRecognizedSubjectCount((short) 0);
        enrollment.setStageCheck("N");
        enrollment.setWinterSemesterGradeNumerator(BigDecimal.ZERO);
        enrollment.setWinterSemesterGradeDenominator(BigDecimal.ZERO);
        enrollment.setSummerSemesterGradeNumerator(BigDecimal.ZERO);
        enrollment.setSummerSemesterGradeDenominator(BigDecimal.ZERO);
        enrollment.setCreditsEarnedWithoutExam(BigDecimal.ZERO);

        entityManager.persist(enrollment);
        entityManager.flush();
    }

}
