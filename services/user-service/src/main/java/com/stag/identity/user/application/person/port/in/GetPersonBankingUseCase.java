package com.stag.identity.user.application.person.port.in;

import com.stag.identity.user.application.person.dto.PersonBankingResult;
import com.stag.identity.user.domain.person.model.PersonId;

public interface GetPersonBankingUseCase {

    PersonBankingResult getPersonBanking(PersonId personId, String language);

}
