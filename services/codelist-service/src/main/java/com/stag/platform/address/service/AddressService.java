package com.stag.platform.address.service;

import com.stag.platform.address.repository.projection.AddressSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<AddressSuggestion> findAddressSuggestions(String query, Integer limit) {
        return Collections.emptyList();
    }

}
