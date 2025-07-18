package com.stag.identity.user.repository;

import com.stag.identity.user.entity.Osoba;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Osoba, Integer> {

}
