package com.stag.platform.address.service;

import com.stag.platform.address.repository.AddressPointRepository;
import com.stag.platform.address.repository.projection.AddressSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressPointRepository addressPointRepository;

    @Transactional(readOnly = true)
    public List<AddressSuggestion> findAddressSuggestions(String query) {
        // Limit the number of results to 20 for performance
        return addressPointRepository.findAddressSuggestions(query, PageRequest.of(0, 20));
    }
}
