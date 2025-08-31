package com.stag.academics.student.repository;

import com.stag.academics.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    @Query("SELECT s.id FROM Student s WHERE s.personId = :personId")
    List<String> findAllStudentIds(Integer personId);

    @Query("SELECT s.personId FROM Student s WHERE s.id = :studentId")
    Optional<Integer> findPersonId(String studentId);

    <T> Optional<T> findById(String studentId, Class<T> clazz);

}
