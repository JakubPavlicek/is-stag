package com.stag.platform.address.repository;

import com.stag.platform.address.entity.Street;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreetRepository extends JpaRepository<Street, Long> {
}
