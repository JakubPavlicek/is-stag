package com.stag.platform.codelist.repository;

import com.stag.platform.codelist.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    @Query("SELECT c.name FROM Country c WHERE c.id = :id")
    Optional<String> findNameById(Integer id);

}
