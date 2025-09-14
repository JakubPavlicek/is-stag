package com.stag.platform.address.repository.projection;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FieldProjection;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IdProjection;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ProjectionConstructor;

@ProjectionConstructor
public record AddressSuggestion(
    @IdProjection
    Long id,
    @FieldProjection(path = "fullAddress")
    String fullAddress
) {

}
