package com.stag.platform.address.mapper;

import com.stag.platform.address.repository.projection.CountryView;
import com.stag.platform.api.dto.Country;
import com.stag.platform.api.dto.CountryListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface CountryApiMapper {

    CountryApiMapper INSTANCE = Mappers.getMapper(CountryApiMapper.class);

    default CountryListResponse toCountryListResponse(Set<CountryView> countries) {
        return new CountryListResponse(toCountryList(countries));
    }

    Set<Country> toCountryList(Set<CountryView> countries);

}
