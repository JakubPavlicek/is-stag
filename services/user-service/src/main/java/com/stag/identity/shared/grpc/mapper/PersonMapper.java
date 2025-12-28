package com.stag.identity.shared.grpc.mapper;

import com.stag.identity.person.model.SimpleProfile;
import com.stag.identity.person.v1.GetPersonSimpleProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/// **Person gRPC Mapper**
///
/// MapStruct mapper for transforming person domain models to gRPC response messages.
/// Converts an internal simple profile model to protobuf message format for gRPC responses.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PersonMapper {

    /// PersonMapper Instance
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    /// Maps a simple profile model to gRPC response message.
    ///
    /// @param simpleProfile the simple profile domain model
    /// @return gRPC response message with profile data
    @Mapping(target = "titlePrefix", source = "simpleProfile.titles.prefix")
    @Mapping(target = "titleSuffix", source = "simpleProfile.titles.suffix")
    GetPersonSimpleProfileResponse buildPersonSimpleProfileResponse(SimpleProfile simpleProfile);

}
