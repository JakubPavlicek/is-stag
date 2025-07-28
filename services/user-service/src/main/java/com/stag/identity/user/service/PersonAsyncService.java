package com.stag.identity.user.service;

import com.stag.identity.user.grpc.CodelistServiceClient;
import com.stag.identity.user.grpc.StudentServiceClient;
import com.stag.identity.user.repository.PersonRepository;
import com.stag.identity.user.repository.projection.Address;
import com.stag.identity.user.repository.projection.AddressProjection;
import com.stag.identity.user.model.AddressType;
import com.stag.identity.user.repository.projection.ForeignAddressProjection;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonForeignAddressData;
import com.stag.identity.user.service.data.PersonProfileData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonAsyncService {

    private final PersonRepository personRepository;

    private final StudentServiceClient studentServiceClient;
    private final CodelistServiceClient codelistServiceClient;

    @Async
    public CompletableFuture<List<String>> getStudentPersonalNumbers(Integer personId) {
        log.info("getStudentPersonalNumbers thread: {}", Thread.currentThread());
        List<String> personalNumbers = studentServiceClient.getStudentPersonalNumbers(personId);

        return CompletableFuture.completedFuture(personalNumbers);
    }

    @Async
    public CompletableFuture<PersonProfileData> getPersonProfileData(PersonProfileProjection personProfile) {
        log.info("getPersonProfileData thread: {}", Thread.currentThread());
        PersonProfileData personProfileData = codelistServiceClient.getPersonProfileData(personProfile);

        return CompletableFuture.completedFuture(personProfileData);
    }

    @Async
    public CompletableFuture<PersonAddressData> getPersonAddressData(Integer personId) {
        log.info("getPersonAddressData thread: {}", Thread.currentThread());

        List<AddressProjection> addresses = personRepository.findAddressesByPersonId(personId);

        AddressProjection permanentAddress = getAddress(addresses, AddressType.PERMANENT);
        AddressProjection temporaryAddress = getAddress(addresses, AddressType.TEMPORARY);

        PersonAddressData addressData = codelistServiceClient.getPersonAddressData(permanentAddress, temporaryAddress);

        return CompletableFuture.completedFuture(addressData);
    }

    @Async
    public CompletableFuture<PersonForeignAddressData> getForeignAddressesByPersonId(Integer personId) {
        log.info("getForeignAddressesByPersonId thread: {}", Thread.currentThread());

        List<ForeignAddressProjection> foreignAddresses = personRepository.findForeignAddressesByPersonId(personId);

        ForeignAddressProjection permanentAddress = getAddress(foreignAddresses, AddressType.FOREIGN_PERMANENT);
        ForeignAddressProjection temporaryAddress = getAddress(foreignAddresses, AddressType.FOREIGN_TEMPORARY);

        PersonForeignAddressData foreignAddressData = new PersonForeignAddressData(permanentAddress, temporaryAddress);

        return CompletableFuture.completedFuture(foreignAddressData);
    }

    private <T extends Address> T getAddress(List<T> addresses, AddressType type) {
        return addresses.stream()
                        .filter(address -> address.getAddressType().equals(type.name()))
                        .findFirst()
                        .orElse(null);
    }

}
