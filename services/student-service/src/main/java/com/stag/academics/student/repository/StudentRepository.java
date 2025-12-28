package com.stag.academics.student.repository;

import com.stag.academics.student.entity.Student;
import com.stag.academics.student.repository.projection.ProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/// **Student Repository**
///
/// Data access layer for student entities with custom query methods for
/// profile retrieval and student-person relationship queries.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public interface StudentRepository extends JpaRepository<Student, String> {

    /// Finds all student IDs associated with a person.
    ///
    /// @param personId the person identifier
    /// @return list of student IDs
    @Query("SELECT s.id FROM Student s WHERE s.personId = :personId")
    List<String> findAllStudentIds(Integer personId);

    /// Retrieves the person ID for a given student.
    ///
    /// @param studentId the student identifier
    /// @return optional containing person ID if found
    @Query("SELECT s.personId FROM Student s WHERE s.id = :studentId")
    Optional<Integer> findPersonId(String studentId);

    /// Finds the complete student profile including enrollment details.
    ///
    /// Returns student profile with most recent enrollment data based on
    /// the maximum year of validity.
    ///
    /// @param studentId the student identifier
    /// @return optional containing profile view if found
    @Query(
        """
        SELECT new com.stag.academics.student.repository.projection.ProfileView(
            s.id,
            s.personId,
            s.studyStatus,
            s.studyProgramId,
            se.id.studyPlanId
        )
        FROM
            Student s
        INNER JOIN StudentEnrollment se ON se.id.studentId = s.id
        WHERE
            s.id = :studentId
        AND
            se.id.yearOfValidity = (
                SELECT MAX(se_inner.id.yearOfValidity)
                FROM StudentEnrollment se_inner
                WHERE se_inner.id.studentId = se.id.studentId
            )
        """
    )
    Optional<ProfileView> findStudentProfileById(String studentId);

}
