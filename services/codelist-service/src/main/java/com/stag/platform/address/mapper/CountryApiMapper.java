package com.stag.platform.address.mapper;

import com.stag.platform.address.repository.projection.CountryView;
import com.stag.platform.api.dto.Country;
import com.stag.platform.api.dto.CountryListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

/// **Country API Mapper**
///
/// MapStruct mapper for converting country projections to API DTOs.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper
public interface CountryApiMapper {

    /// Mapper instance
    CountryApiMapper INSTANCE = Mappers.getMapper(CountryApiMapper.class);

    /// Converts country views to country list response.
    ///
    /// @param countries Set of country views
    /// @return Country list response DTO
    default CountryListResponse toCountryListResponse(Set<CountryView> countries) {
        return new CountryListResponse(toCountryList(countries));
    }

    /// Maps country views to country DTOs.
    ///
    /// @param countries Set of country views
    /// @return Set of country DTOs
    Set<Country> toCountryList(Set<CountryView> countries);

}
