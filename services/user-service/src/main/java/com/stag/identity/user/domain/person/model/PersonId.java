package com.stag.identity.user.domain.person.model;

import java.io.Serializable;

/// A dedicated value object for the Person's identifier.
public record PersonId(Integer id) implements Serializable {

}
