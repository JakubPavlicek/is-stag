package com.stag.platform.entry.mapper;

import com.stag.platform.api.dto.DomainListResponse;
import com.stag.platform.api.dto.DomainValue;
import com.stag.platform.api.dto.DomainValueListResponse;
import com.stag.platform.entry.repository.projection.DomainValueView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/// **Domain API Mapper**
///
/// MapStruct mapper for converting domain projections to API DTOs.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper
public interface DomainApiMapper {

    /// Mapper instance
    DomainApiMapper INSTANCE = Mappers.getMapper(DomainApiMapper.class);

    /// Converts a list of domain names to domain list response.
    ///
    /// @param domains List of domain names
    /// @return Domain list response DTO
    default DomainListResponse toDomainListResponse(List<String> domains) {
        return new DomainListResponse(new LinkedHashSet<>(domains));
    }

    /// Converts a list of domain value views to domain value list response.
    ///
    /// @param domainValues List of domain value views
    /// @return Domain value list response DTO
    default DomainValueListResponse toDomainValueListResponse(List<DomainValueView> domainValues) {
        return new DomainValueListResponse(toDomainValueList(domainValues));
    }

    /// Maps domain value views to domain value DTOs.
    ///
    /// @param domainValues List of domain value views
    /// @return Set of domain value DTOs
    Set<DomainValue> toDomainValueList(List<DomainValueView> domainValues);

}
