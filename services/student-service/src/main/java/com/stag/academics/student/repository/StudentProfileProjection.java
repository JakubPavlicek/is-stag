package com.stag.academics.student.repository;

public record StudentProfileProjection(
    String studentId,
    Integer personId,
    String studyStatus,
    Integer studyProgramId
) {

}
