package com.stag.academics.student.service.data;

import java.io.Serializable;

/// **Simple Profile Lookup Data**
///
/// Basic person profile data retrieved from the Person service.
/// Contains name, titles, and gender information.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record SimpleProfileLookupData(
    String firstName,
    String lastName,
    String titlePrefix,
    String titleSuffix,
    String gender
) implements Serializable {

}
