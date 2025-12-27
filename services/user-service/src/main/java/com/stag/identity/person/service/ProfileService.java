package com.stag.identity.person.service;

import com.stag.identity.person.entity.Person;
import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.mapper.ProfileMapper;
import com.stag.identity.person.model.Profile;
import com.stag.identity.person.model.SimpleProfile;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.ProfileView;
import com.stag.identity.person.repository.projection.SimpleProfileView;
import com.stag.identity.person.service.data.CodelistMeaningsLookupData;
import com.stag.identity.person.service.data.ProfileLookupData;
import com.stag.identity.person.service.data.ProfileUpdateLookupData;
import com.stag.identity.person.service.dto.PersonUpdateCommand;
import com.stag.identity.person.util.DataBoxValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService {

    private final PersonRepository personRepository;

    private final BankingService bankingService;

    private final CodelistLookupService codelistLookupService;
    private final StudentLookupService studentLookupService;

    private final TransactionTemplate transactionTemplate;

    @Cacheable(value = "person-profile", key = "{#personId, #language}")
    @PreAuthorize("""
        hasAnyRole('AD', 'DE', 'PR', 'SR', 'SP', 'VY', 'VK')
        || @authorizationService.isStudentAndOwner(hasRole('ST'), principal.claims['studentId'], #personId)
    """)
    public Profile getPersonProfile(Integer personId, String language) {
        log.info("Fetching person profile for personId: {} with language: {}", personId, language);

        ProfileView profileView = transactionTemplate.execute(status ->
            personRepository.findById(personId, ProfileView.class)
                            .orElseThrow(() -> new PersonNotFoundException(personId))
        );

        log.debug("Person profile found, fetching additional data for personId: {}", personId);

        CompletableFuture<List<String>> studentIdsFuture =
            studentLookupService.getStudentIds(personId);

        CompletableFuture<ProfileLookupData> profileDataFuture =
            codelistLookupService.getPersonProfileData(profileView, language);

        CompletableFuture.allOf(studentIdsFuture, profileDataFuture).join();

        log.debug("Additional data fetched, mapping to PersonProfile for personId: {}", personId);
        Profile profile = ProfileMapper.INSTANCE.toPersonProfile(
            profileView, studentIdsFuture.join(), profileDataFuture.join()
        );

        log.info("Successfully fetched person profile for personId: {}", personId);
        return profile;
    }

    @Cacheable(value = "person-simple-profile", key = "{#personId, #language}")
    public SimpleProfile getPersonSimpleProfile(Integer personId, String language) {
        log.info("Fetching person simple profile for personId: {} with language: {}", personId, language);

        SimpleProfileView simpleProfileView = transactionTemplate.execute(status ->
            personRepository.findById(personId, SimpleProfileView.class)
                            .orElseThrow(() -> new PersonNotFoundException(personId))
        );

        log.debug("Person simple profile found, fetching additional data for personId: {}", personId);

        CodelistMeaningsLookupData codelistMeaningsLookupData =
            codelistLookupService.getSimpleProfileData(simpleProfileView, language);

        SimpleProfile simpleProfile = ProfileMapper.INSTANCE.toSimplePersonProfile(
            simpleProfileView, codelistMeaningsLookupData
        );

        log.info("Successfully fetched person simple profile for personId: {}", personId);
        return simpleProfile;
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "person-profile", key = "{#personId, 'cs'}"),
        @CacheEvict(value = "person-profile", key = "{#personId, 'en'}"),
        @CacheEvict(value = "person-simple-profile", key = "{#personId, 'cs'}"),
        @CacheEvict(value = "person-simple-profile", key = "{#personId, 'en'}"),
        @CacheEvict(value = "person-banking", key = "{#personId, 'cs'}"),
        @CacheEvict(value = "person-banking", key = "{#personId, 'en'}"),
        @CacheEvict(value = "student-profile", allEntries = true),
    })
    @PreAuthorize("""
        hasRole('AD')
        || @authorizationService.isStudentAndOwner(hasRole('ST'), principal.claims['studentId'], #personId)
    """)
    public void updatePersonProfile(Integer personId, PersonUpdateCommand command) {
        log.info("Updating person profile for personId: {}", personId);

        Person person = personRepository.findById(personId)
                                        .orElseThrow(() -> new PersonNotFoundException(personId));

        person.setBirthSurname(command.birthSurname());
        updateContact(person, command.contact());
        bankingService.updatePersonBankAccount(person, command.bankAccount());

        Profile.BirthPlace birthPlace = command.birthPlace();
        if (birthPlace != null) {
            person.setBirthPlace(birthPlace.city());
        }

        log.debug("Checking person profile update data for personId: {}", personId);

        // Check if provided data are valid by calling the codelist-service and get the birthCountryId
        ProfileUpdateLookupData profileUpdateLookupData = codelistLookupService.getPersonProfileUpdateData(
            command.maritalStatus(),
            birthPlace != null ? birthPlace.country() : null,
            command.titles()
        );

        log.debug("Successfully checked person profile update data for personId: {}", personId);

        // Update the values that were validated by codelist-service
        if (profileUpdateLookupData != null) {
            person.setMaritalStatus(profileUpdateLookupData.maritalStatusLowValue());
            person.setTitlePrefix(profileUpdateLookupData.titlePrefixLowValue());
            person.setTitleSuffix(profileUpdateLookupData.titleSuffixLowValue());
            person.setBirthCountryId(profileUpdateLookupData.birthCountryId());
        }

        log.info("Successfully updated person profile for personId: {}", personId);
    }

    private void updateContact(Person person, Profile.Contact contact) {
        if (contact == null) {
            log.debug("Contact is null, no updates to perform for personId: {}", person.getId());
            return;
        }

        person.setEmail(contact.email());
        person.setPhone(contact.phone());
        person.setMobile(contact.mobile());

        if (contact.dataBox() == null) {
            person.setDataBox(null);
            return;
        }

        if (DataBoxValidator.isValidDataBoxId(contact.dataBox())) {
            person.setDataBox(contact.dataBox());
        }
    }

}
