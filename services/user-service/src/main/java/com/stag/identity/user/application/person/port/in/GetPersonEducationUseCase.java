package com.stag.identity.user.application.person.port.in;

import com.stag.identity.user.application.person.dto.PersonEducationResult;
import com.stag.identity.user.domain.person.model.PersonId;

public interface GetPersonEducationUseCase {

    PersonEducationResult getPersonEducation(PersonId personId, String language);

}
