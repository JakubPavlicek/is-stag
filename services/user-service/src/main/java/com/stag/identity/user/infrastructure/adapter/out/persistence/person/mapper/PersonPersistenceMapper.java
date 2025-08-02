package com.stag.identity.user.infrastructure.adapter.out.persistence.person.mapper;

import com.stag.identity.user.domain.person.model.PersonAddress;
import com.stag.identity.user.domain.person.model.PersonBank;
import com.stag.identity.user.domain.person.model.PersonEducation;
import com.stag.identity.user.domain.person.model.PersonProfile;
import com.stag.identity.user.infrastructure.adapter.out.persistence.person.projection.PersonAddressProjection;
import com.stag.identity.user.infrastructure.adapter.out.persistence.person.projection.PersonBankProjection;
import com.stag.identity.user.infrastructure.adapter.out.persistence.person.projection.PersonEducationProjection;
import com.stag.identity.user.infrastructure.adapter.out.persistence.person.projection.PersonProfileProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonPersistenceMapper {

    PersonProfile toPersonProfileData(PersonProfileProjection personProfileProjection);

    PersonAddress toPersonAddressData(PersonAddressProjection personAddressProjection);

    PersonBank toPersonBankingData(PersonBankProjection personBankProjection);

    PersonEducation toPersonEducationData(PersonEducationProjection personEducationProjection);

}
