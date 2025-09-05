package com.stag.academics.student.service.data;

public record SimpleProfileLookupData(
    String firstName,
    String lastName,
    String titlePrefix,
    String titleSuffix,
    String gender
) {

}
