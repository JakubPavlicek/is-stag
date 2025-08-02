package com.stag.identity.user.infrastructure.adapter.out.persistence.person;

import com.stag.identity.user.domain.person.model.PersonAddress;
import com.stag.identity.user.domain.person.model.PersonBank;
import com.stag.identity.user.domain.person.model.PersonEducation;
import com.stag.identity.user.domain.person.model.PersonProfile;
import com.stag.identity.user.application.person.port.out.PersonRepositoryPort;
import com.stag.identity.user.domain.person.model.PersonId;
import com.stag.identity.user.infrastructure.adapter.out.persistence.person.mapper.PersonPersistenceMapper;
import com.stag.identity.user.infrastructure.adapter.out.persistence.person.projection.PersonProfileProjection;
import com.stag.identity.user.infrastructure.adapter.out.persistence.person.repository.PersonJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PersonRepositoryAdapter implements PersonRepositoryPort {

    private final PersonJpaRepository personJpaRepository;
    private final PersonPersistenceMapper personMapper;

    @Override
    public Optional<PersonProfile> findPersonProfileById(PersonId personId) {
        return personJpaRepository.findById(personId.id(), PersonProfileProjection.class)
                                  .map(personMapper::toPersonProfileData);
    }

    @Override
    public Optional<PersonAddress> findAddressesByPersonId(PersonId personId) {
        return personJpaRepository.findAddressesByPersonId(personId.id())
                                  .map(personMapper::toPersonAddressData);
    }

    @Override
    public Optional<PersonBank> findBankingByPersonId(PersonId personId) {
        return personJpaRepository.findBankingByPersonId(personId.id())
                                  .map(personMapper::toPersonBankingData);
    }

    @Override
    public Optional<PersonEducation> findEducationByPersonId(PersonId personId) {
        return personJpaRepository.findEducationByPersonId(personId.id())
                                  .map(personMapper::toPersonEducationData);
    }

}
