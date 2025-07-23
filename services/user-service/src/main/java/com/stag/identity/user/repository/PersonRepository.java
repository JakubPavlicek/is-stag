package com.stag.identity.user.repository;

import com.stag.identity.user.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    <T> Optional<T> findById(Integer id, Class<T> clazz);

}
