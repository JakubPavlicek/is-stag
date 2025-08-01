package com.stag.identity.user.application.person.port.in;

import com.stag.identity.user.domain.person.model.PersonId;
import com.stag.identity.user.application.person.dto.PersonProfileResult;

public interface GetPersonProfileUseCase {

    PersonProfileResult getPersonProfile(PersonId personId, String language);

}
