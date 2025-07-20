package com.stag.academics.student.repository;

import com.stag.academics.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {

    @Query("SELECT s.personalNumber FROM Student s WHERE s.personId = :personId")
    List<String> findAllPersonalNumbers(Integer personId);

}
