package com.stag.platform.entry.repository.projection;

import java.io.Serializable;

public record DomainValueView(
    String key,
    String name,
    String abbreviation
) implements Serializable {

}
