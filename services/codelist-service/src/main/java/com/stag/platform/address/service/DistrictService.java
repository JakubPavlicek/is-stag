package com.stag.platform.address.service;

import com.stag.platform.address.entity.District;
import com.stag.platform.address.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;

}
