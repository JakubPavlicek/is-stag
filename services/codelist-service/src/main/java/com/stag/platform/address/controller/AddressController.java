package com.stag.platform.address.controller;

import com.stag.platform.address.mapper.AddressApiMapper;
import com.stag.platform.address.repository.projection.AddressSuggestion;
import com.stag.platform.address.service.AddressService;
import com.stag.platform.api.AddressesApi;
import com.stag.platform.api.dto.AddressListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AddressController implements AddressesApi {

    private final AddressService addressService;

    @Override
    public ResponseEntity<AddressListResponse> getAddresses(String query) {
        List<AddressSuggestion> addressSuggestions = addressService.findAddressSuggestions(query);
        AddressListResponse addressesResponse = AddressApiMapper.INSTANCE.toAddressListResponse(addressSuggestions);

        return ResponseEntity.ok(addressesResponse);
    }

}
