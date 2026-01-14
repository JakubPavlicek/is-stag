package com.stag.academics.student.repository.projection;

/// **Profile View**
///
/// Projection containing essential student profile data including
/// identifiers, study status, and enrollment information.
///
/// @param studentId Student ID
/// @param personId Person ID
/// @param studyStatus Study status
/// @param studyProgramId Study program ID
/// @param studyPlanId Study plan ID
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record ProfileView(
    String studentId,
    Integer personId,
    String studyStatus,
    Long studyProgramId,
    Long studyPlanId
) {

}
