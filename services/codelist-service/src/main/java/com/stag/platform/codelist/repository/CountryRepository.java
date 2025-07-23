package com.stag.platform.codelist.repository;

import com.stag.platform.codelist.entity.Country;
import com.stag.platform.codelist.projection.CountryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    @Query("SELECT c.name FROM Country c WHERE c.id = :id")
    Optional<String> findNameById(Integer id);

    @Query("SELECT c.id, c.name FROM Country c WHERE c.id IN :ids")
    List<CountryName> findNamesByIds(List<Integer> ids);

}
