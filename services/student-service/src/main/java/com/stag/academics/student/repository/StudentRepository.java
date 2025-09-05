package com.stag.academics.student.repository;

import com.stag.academics.student.entity.Student;
import com.stag.academics.student.repository.projection.ProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    @Query("SELECT s.id FROM Student s WHERE s.personId = :personId")
    List<String> findAllStudentIds(Integer personId);

    @Query("SELECT s.personId FROM Student s WHERE s.id = :studentId")
    Optional<Integer> findPersonId(String studentId);

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
