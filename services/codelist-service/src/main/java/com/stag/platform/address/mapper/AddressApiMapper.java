package com.stag.platform.address.mapper;

import com.stag.platform.address.repository.projection.AddressSuggestion;
import com.stag.platform.api.dto.Address;
import com.stag.platform.api.dto.AddressListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper
public interface AddressApiMapper {

    AddressApiMapper INSTANCE = Mappers.getMapper(AddressApiMapper.class);

    default AddressListResponse toAddressListResponse(List<AddressSuggestion> addressSuggestions) {
        return new AddressListResponse(toAddressList(addressSuggestions));
    }

    Set<Address> toAddressList(List<AddressSuggestion> addressSuggestions);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "fullAddress")
    Address toAddress(AddressSuggestion addressSuggestion);

}
