package com.stag.platform.entry.repository.projection;

import java.io.Serializable;

/// **Domain Value View**
///
/// Projection for domain value data including key, name, and abbreviation.
///
/// @param key Domain value key
/// @param name Domain value name
/// @param abbreviation Domain value abbreviation
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record DomainValueView(
    String key,
    String name,
    String abbreviation
) implements Serializable {

}
