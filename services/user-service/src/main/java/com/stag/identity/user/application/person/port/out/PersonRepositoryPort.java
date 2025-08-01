package com.stag.identity.user.application.person.port.out;

import com.stag.identity.user.domain.person.model.PersonAddress;
import com.stag.identity.user.domain.person.model.PersonBank;
import com.stag.identity.user.domain.person.model.PersonEducation;
import com.stag.identity.user.domain.person.model.PersonId;
import com.stag.identity.user.domain.person.model.PersonProfile;

import java.util.Optional;

/// An output port that defines the contract for persisting and retrieving Person aggregates.
/// This acts as the bridge between the application layer and the persistence layer (infrastructure).
public interface PersonRepositoryPort {

    Optional<PersonProfile> findPersonProfileById(PersonId personId);
    Optional<PersonAddress> findAddressesByPersonId(PersonId personId);
    Optional<PersonBank> findBankingByPersonId(PersonId personId);
    Optional<PersonEducation> findEducationByPersonId(PersonId personId);

}
