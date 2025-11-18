package com.stag.identity.person.service;

import com.stag.identity.person.entity.Person;
import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.mapper.ProfileMapper;
import com.stag.identity.person.model.Profile;
import com.stag.identity.person.model.SimpleProfile;
import com.stag.identity.person.repository.PersonRepository;
import com.stag.identity.person.repository.projection.ProfileView;
import com.stag.identity.person.repository.projection.SimpleProfileView;
import com.stag.identity.person.service.data.AddressIdsLookupData;
import com.stag.identity.person.service.data.CodelistMeaningsLookupData;
import com.stag.identity.person.service.data.ProfileLookupData;
import com.stag.identity.person.service.dto.PersonUpdateCommand;
import com.stag.identity.person.util.DataBoxValidator;
import com.stag.identity.shared.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
    @PreAuthorize("""
        hasAnyRole('AD', 'DE', 'PR', 'SR', 'SP', 'VY', 'VK')
        || @authorizationService.isStudentAndOwner(hasRole('ST'), principal.claims['studentId'], #personId)
    """)
    public void updatePersonProfile(Integer personId, PersonUpdateCommand command, String language) {
        Person person = personRepository.findById(personId)
                                        .orElseThrow(() -> new PersonNotFoundException(personId));

        ObjectUtils.updateIfNotNull(command.birthSurname(), person::setBirthSurname);

        updateContact(person, command.contact());
        updateTitles(person, command.titles());

        bankingService.updatePersonBankAccount(person, command.bankAccount());

        // TODO: get the codelist value of marital status and set it
        ObjectUtils.updateIfNotNull(command.maritalStatus(), person::setMaritalStatus);

        // TODO: update BirthPlace and TemporaryAddress
        updateAddresses(person, command.birthPlace(), command.temporaryAddress(), language);
    }

    private void updateAddresses(Person person, Profile.BirthPlace birthPlace, PersonUpdateCommand.Address address, String language) {
        if (birthPlace == null && address == null) {
            return;
        }

        if (birthPlace != null) {
            ObjectUtils.updateIfNotNull(birthPlace.city(), person::setBirthPlace);
        }

        if (address != null) {
            ObjectUtils.updateIfNotNull(address.street(), person::setTemporaryStreet);
            ObjectUtils.updateIfNotNull(address.streetNumber(), person::setTemporaryStreetNumber);
            ObjectUtils.updateIfNotNull(address.zipCode(), person::setTemporaryZipCode);
            ObjectUtils.updateIfNotNull(address.postOffice(), person::setPostOfficeForeign);
        }

        AddressIdsLookupData addressIdsData = codelistLookupService.getAddressIdsData(birthPlace, address, language);

        if (addressIdsData != null) {
            // Czech address
            if (addressIdsData.countryId() == 203) {
                // Need to use the retrieved address ids that were validated
            }

            // Temporary address is different from domicile address
            if (!person.getDomicileCountryId().equals(addressIdsData.countryId())) {

            }
            else {

            }
        }
    }

    private void updateContact(Person person, Profile.Contact contact) {
        ObjectUtils.updateIfNotNull(contact.email(), person::setEmail);
        ObjectUtils.updateIfNotNull(contact.phone(), person::setPhone);
        ObjectUtils.updateIfNotNull(contact.mobile(), person::setMobile);

        if (DataBoxValidator.isValidDataBoxId(contact.dataBox())) {
            person.setDataBox(contact.dataBox());
        }
    }

    private void updateTitles(Person person, Profile.Titles titles) {
        ObjectUtils.updateIfNotNull(titles.prefix(), person::setTitlePrefix);
        ObjectUtils.updateIfNotNull(titles.suffix(), person::setTitleSuffix);
    }

}
