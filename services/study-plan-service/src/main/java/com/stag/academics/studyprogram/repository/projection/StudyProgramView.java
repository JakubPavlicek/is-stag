package com.stag.academics.studyprogram.repository.projection;

public record StudyProgramView(
    Long id,
    String name,
    String faculty,
    String code,
    String form,
    String type
) {

}
