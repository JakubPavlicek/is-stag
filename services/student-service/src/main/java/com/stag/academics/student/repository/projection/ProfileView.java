package com.stag.academics.student.repository.projection;

public record ProfileView(
    String studentId,
    Integer personId,
    String studyStatus,
    Long studyProgramId,
    Long studyPlanId
) {

}
