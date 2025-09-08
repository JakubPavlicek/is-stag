package com.stag.platform.address.repository.projection;

import java.io.Serializable;

public record CountryView(
    Integer id,
    String name,
    String commonName
) implements Serializable {

}
