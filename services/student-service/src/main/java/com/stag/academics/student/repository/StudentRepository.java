package com.stag.academics.student.repository;

import com.stag.academics.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    @Query("SELECT s.personalNumber FROM Student s WHERE s.personId = :personId")
    List<String> findAllPersonalNumbers(Integer personId);

    @Query("SELECT s.personId FROM Student s WHERE s.personalNumber = :personalNumber")
    Optional<Integer> findPersonIdByPersonalNumber(String personalNumber);

}
