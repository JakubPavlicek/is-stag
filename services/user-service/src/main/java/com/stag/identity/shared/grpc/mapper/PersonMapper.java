package com.stag.identity.shared.grpc.mapper;

import com.stag.identity.person.model.SimpleProfile;
import com.stag.identity.person.v1.GetPersonSimpleProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mapping(target = "titlePrefix", source = "simpleProfile.titles.prefix")
    @Mapping(target = "titleSuffix", source = "simpleProfile.titles.suffix")
    GetPersonSimpleProfileResponse buildPersonSimpleProfileResponse(SimpleProfile simpleProfile);

}
