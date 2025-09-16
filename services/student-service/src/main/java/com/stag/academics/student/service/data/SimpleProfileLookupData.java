package com.stag.academics.student.service.data;

import java.io.Serializable;

public record SimpleProfileLookupData(
    String firstName,
    String lastName,
    String titlePrefix,
    String titleSuffix,
    String gender
) implements Serializable {

}
