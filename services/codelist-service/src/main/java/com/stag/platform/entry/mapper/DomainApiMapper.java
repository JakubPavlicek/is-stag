package com.stag.platform.entry.mapper;

import com.stag.platform.api.dto.DomainListResponse;
import com.stag.platform.api.dto.DomainValue;
import com.stag.platform.api.dto.DomainValueListResponse;
import com.stag.platform.entry.repository.projection.DomainValueView;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mapper
public interface DomainApiMapper {

    DomainApiMapper INSTANCE = Mappers.getMapper(DomainApiMapper.class);

    default DomainListResponse toDomainListResponse(List<String> domains) {
        return new DomainListResponse(new LinkedHashSet<>(domains));
    }

    default DomainValueListResponse toDomainValueListResponse(List<DomainValueView> domainValues) {
        return new DomainValueListResponse(toDomainValueList(domainValues));
    }

    Set<DomainValue> toDomainValueList(List<DomainValueView> domainValues);

}
