package com.stag.academics.fieldofstudy.repository.projection;

public record FieldOfStudyView(
    Long id,
    String name,
    String faculty,
    String department,
    String code
) {

}
