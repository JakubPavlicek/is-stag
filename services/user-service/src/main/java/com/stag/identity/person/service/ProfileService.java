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

/// **Profile Service**
///
/// Business logic for person profile operations. Handles full and simple profile
/// retrieval with localized codelist data, student ID lookups, and profile updates.
/// Uses caching and async data fetching for optimal performance.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService {

    /// Person Repository
    private final PersonRepository personRepository;

    /// Banking Service
    private final BankingService bankingService;

    /// Codelist Lookup Service
    private final CodelistLookupService codelistLookupService;
    /// Student Lookup Service
    private final StudentLookupService studentLookupService;

    /// Transaction Template for transaction management
    private final TransactionTemplate transactionTemplate;

    /// Retrieves a full person profile with enriched codelist data and student IDs.
    /// Fetches profile projection, then asynchronously loads student IDs and
    /// localized codelist meanings. Result is cached per person ID and language.
    ///
    /// @param personId the person identifier
    /// @param language the language code for codelist localization
    /// @return complete profile with localized data
    /// @throws PersonNotFoundException if person not found
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

    /// Retrieves simplified person profile with basic localized codelist data.
    /// Lighter alternative to full profile for scenarios requiring only essential
    /// personal information. Result is cached per person ID and language.
    ///
    /// @param personId the person identifier
    /// @param language the language code for codelist localization
    /// @return simple profile with localized data
    /// @throws PersonNotFoundException if person not found
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

    /// Updates person profile information including contact details, banking, and personal data.
    /// Validates codelist values (marital status, birth country, titles) via codelist service.
    /// Evicts all related caches (profiles, banking, student) after a successful update.
    ///
    /// @param personId the person identifier
    /// @param command the update command containing new profile data
    /// @throws PersonNotFoundException if person not found
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

    /// Updates person contact information including email, phone, mobile, and data box.
    /// Validates a data box ID format if provided. Skips update if contact is null.
    ///
    /// @param person the person entity to update
    /// @param contact the new contact information
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
