package com.stag.academics.shared.grpc.mapper;

import com.stag.academics.student.service.data.SimpleProfileLookupData;
import com.stag.identity.person.v1.GetPersonSimpleProfileRequest;
import com.stag.identity.person.v1.GetPersonSimpleProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/// **Person Mapper**
///
/// MapStruct mapper for converting between gRPC person service messages
/// and internal data models.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PersonMapper {

    /// PersonMapper Instance
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    /// Maps person ID and language to gRPC request.
    ///
    /// @param personId the person identifier
    /// @param language the language code
    /// @return gRPC request for simple profile data
    GetPersonSimpleProfileRequest toSimpleProfileDataRequest(Integer personId, String language);

    /// Maps gRPC response to internal profile data model.
    ///
    /// @param response the gRPC response
    /// @return simple profile lookup data
    SimpleProfileLookupData toSimpleProfileData(GetPersonSimpleProfileResponse response);

}
