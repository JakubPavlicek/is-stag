package com.stag.identity.user.application.person.port.out;

import com.stag.identity.user.domain.person.model.PersonId;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * An output port defining the contract for fetching data from the external student service.
 */
public interface StudentServicePort {

    List<String> getStudentPersonalNumbers(PersonId personId);

}
