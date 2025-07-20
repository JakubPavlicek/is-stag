package com.stag.identity.user.repository;

import com.stag.identity.user.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {

}
