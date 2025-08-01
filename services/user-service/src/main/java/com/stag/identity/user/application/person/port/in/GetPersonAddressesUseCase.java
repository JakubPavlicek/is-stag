package com.stag.identity.user.application.person.port.in;

import com.stag.identity.user.application.person.dto.PersonAddressesResult;
import com.stag.identity.user.domain.person.model.PersonId;

public interface GetPersonAddressesUseCase {

    PersonAddressesResult getPersonAddresses(PersonId personId, String language);

}
