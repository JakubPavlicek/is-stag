package com.stag.academics.studyplan.repository;

import com.stag.academics.config.TestCacheConfig;
import com.stag.academics.config.TestOracleContainerConfig;
import com.stag.academics.fieldofstudy.entity.FieldOfStudy;
import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import com.stag.academics.studyplan.entity.StudyPlan;
import com.stag.academics.studyprogram.entity.StudyProgram;
import com.stag.academics.studyprogram.repository.StudyProgramRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class })
@ActiveProfiles("test")
class StudyPlanRepositoryTest {

    @Autowired
    private StudyPlanRepository studyPlanRepository;

    @Autowired
    private StudyProgramRepository studyProgramRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("should return field of study view when study plan exists")
    void findFieldOfStudy_StudyPlanExists_ReturnsFieldOfStudyView() {
        StudyProgram studyProgram = Instancio.of(StudyProgram.class)
                                             .set(field(StudyProgram::getId), 1L)
                                             .set(field(StudyProgram::getFaculty), "F1")
                                             .set(field(StudyProgram::getType), "B")
                                             .set(field(StudyProgram::getForm), "P")
                                             .set(field(StudyProgram::getLanguage), "CZ")
                                             .set(field(StudyProgram::getProgramProfile), "A")
                                             .set(field(StudyProgram::getStandardLength), new BigDecimal("3.0"))
                                             .set(field(StudyProgram::getMaxLength), new BigDecimal("5.0"))
                                             .set(field(StudyProgram::getInternalCode), "SP001")
                                             .set(field(StudyProgram::getDateOfInsert), LocalDate.now())
                                             .set(field(StudyProgram::getCreditLimit), (short) 180)
                                             .set(field(StudyProgram::getReportedStatus), "A")
                                             .set(field(StudyProgram::getValidFrom), "2020")
                                             .set(field(StudyProgram::getInvalidFrom), null)
                                             .set(field(StudyProgram::getDiploma), "A")
                                             .set(field(StudyProgram::getDocument), "A")
                                             .set(field(StudyProgram::getDiplomaType), "D1")
                                             .set(field(StudyProgram::getReportType), "R1")
                                             .set(field(StudyProgram::getCertificateType), "C1")
                                             .set(field(StudyProgram::getNameCz), "Test Program CZ")
                                             .set(field(StudyProgram::getNameEn), "Test Program EN")
                                             .set(field(StudyProgram::getGoalsCz), "Goals")
                                             .set(field(StudyProgram::getUniversityPaper), "A")
                                             .set(field(StudyProgram::getReportToHealthInsurance), "A")
                                             .set(field(StudyProgram::getStudentIdMask), "A##1")
                                             .set(field(StudyProgram::getProfessionalStatus), "nespec")
                                             .set(field(StudyProgram::getAccreditationWithFieldNumber), "A")
                                             .set(field(StudyProgram::getInstitutionalAccreditation), "N")
                                             .set(field(StudyProgram::getAccreditationPostAmendment), "A")
                                             .set(field(StudyProgram::getEconomicComplexityCoefficient), new BigDecimal("1"))
                                             .set(field(StudyProgram::getMaxInterruptionLength), new BigDecimal("1.0"))
                                             .set(field(StudyProgram::getJointDegrees), "1")
                                             .set(field(StudyProgram::getJointFacultyInstruction), "Test")
                                             .set(field(StudyProgram::getTotalEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getCertificatesEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getOpponentEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getThesisEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getFinalThesisEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getSupervisorEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getSubjectTotalEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getPrintSpecializationOnDiploma), "N")
                                             .set(field(StudyProgram::getRigorosumPossibility), "N")
                                             .set(field(StudyProgram::getIsFollowUp), "N")
                                             .set(field(StudyProgram::getDegree), "Bc.")
                                             .set(field(StudyProgram::getOwner), "SYSTEM")
                                             .set(field(StudyProgram::getRequiredEducationLevel), "K")
                                             .set(field(StudyProgram::getLimitedAccreditationReason), "N")
                                             .set(field(StudyProgram::getRigorosumDegree), "Ph")
                                             .set(field(StudyProgram::getLifelongLearningProgramPurpose), "N")
                                             .set(field(StudyProgram::getFinalThesisType), "DT")
                                             .create();

        studyProgramRepository.saveAndFlush(studyProgram);

        FieldOfStudy fieldOfStudy = Instancio.of(FieldOfStudy.class)
                                             .set(field(FieldOfStudy::getId), 1L)
                                             .set(field(FieldOfStudy::getStudyProgram), studyProgram)
                                             .set(field(FieldOfStudy::getFieldNumber), "FOS001")
                                             .set(field(FieldOfStudy::getSpecializationNumber), "00")
                                             .set(field(FieldOfStudy::getFaculty), "F1")
                                             .set(field(FieldOfStudy::getDepartment), "KIV")
                                             .set(field(FieldOfStudy::getForm), "P")
                                             .set(field(FieldOfStudy::getType), "B")
                                             .set(field(FieldOfStudy::getValidFrom), "2020")
                                             .set(field(FieldOfStudy::getDateOfInsert), LocalDate.now())
                                             .set(field(FieldOfStudy::getNameCz), "Field CZ")
                                             .set(field(FieldOfStudy::getNameEn), "Field EN")
                                             .set(field(FieldOfStudy::getOwner), "SYSTEM")
                                             .set(field(FieldOfStudy::getCreditLimit), (short) 180)
                                             .set(field(FieldOfStudy::getReportedStatus), "A")
                                             .set(field(FieldOfStudy::getIsTeachingQualification), "N")
                                             .set(field(FieldOfStudy::getNumberOfStages), false)
                                             .set(field(FieldOfStudy::getPrintStatus), "A")
                                             .set(field(FieldOfStudy::getHasDissertation), "N")
                                             .set(field(FieldOfStudy::getPrintSpecialization), "A")
                                             .set(field(FieldOfStudy::getProfessionalStatus), "nespec")
                                             .set(field(FieldOfStudy::getReportToHealthRegistry), "N")
                                             .set(field(FieldOfStudy::getIssueDiplomaAndSupplement), "A")
                                             .set(field(FieldOfStudy::getJointDegrees), "1")
                                             .set(field(FieldOfStudy::getParticipationForm), "1")
                                             .set(field(FieldOfStudy::getMicroCertificateEqf), "1")
                                             .set(field(FieldOfStudy::getInvalidFrom), "2025")
                                             .set(field(FieldOfStudy::getEducationArea), "123")
                                             .set(field(FieldOfStudy::getEvaluationType), "Z")
                                             .set(field(FieldOfStudy::getAdmissionRequirements), "1")
                                             .set(field(FieldOfStudy::getCourseDurationUnit), "D")
                                             .set(field(FieldOfStudy::getProgramProfile), "P")
                                             .set(field(FieldOfStudy::getSupervisionAndVerificationType), "D")
                                             .set(field(FieldOfStudy::getQualityAssuranceType), "I")
                                             .set(field(FieldOfStudy::getCatalogStatus), "N")
                                             .set(field(FieldOfStudy::getNumberA), "01")
                                             .set(field(FieldOfStudy::getPrintSpecializationOnDiploma), "N")
                                             .set(field(FieldOfStudy::getStandardLength), new BigDecimal("3.0"))
                                             .set(field(FieldOfStudy::getMaxLength), new BigDecimal("5.0"))
                                             .create();

        testEntityManager.persistAndFlush(fieldOfStudy);

        StudyPlan studyPlan = Instancio.of(StudyPlan.class)
                                       .set(field(StudyPlan::getId), 1L)
                                       .set(field(StudyPlan::getFieldOfStudy), fieldOfStudy)
                                       .set(field(StudyPlan::getAcademicYear), "2020")
                                       .set(field(StudyPlan::getStage), "1")
                                       .set(field(StudyPlan::getVersion), "1")
                                       .set(field(StudyPlan::getLanguage), "CZ")
                                       .set(field(StudyPlan::getDateOfInsert), LocalDate.now())
                                       .set(field(StudyPlan::getIsCreditBased), "A")
                                       .set(field(StudyPlan::getCreditLimit), (short) 180)
                                       .set(field(StudyPlan::getOwner), "SYSTEM")
                                       .set(field(StudyPlan::getNumberOfSemesters), (short) 6)
                                       .set(field(StudyPlan::getIsSpecialization), "N")
                                       .set(field(StudyPlan::getDisplayEcts), "A")
                                       .set(field(StudyPlan::getEnrollmentLimitation), "L")
                                       .set(field(StudyPlan::getIsForFirstYearStudents), "N")
                                       .create();

        studyPlanRepository.saveAndFlush(studyPlan);

        Optional<FieldOfStudyView> result = studyPlanRepository.findFieldOfStudy(1L, "en");

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("Field EN");
        assertThat(result.get().faculty()).isEqualTo("F1");
        assertThat(result.get().department()).isEqualTo("KIV");
        assertThat(result.get().code()).isEqualTo("FOS001");
    }

    @Test
    @DisplayName("should return field of study view with czech name when language is not english")
    void findFieldOfStudy_LanguageNotEnglish_ReturnsCzechName() {
        StudyProgram studyProgram = Instancio.of(StudyProgram.class)
                                             .set(field(StudyProgram::getId), 2L)
                                             .set(field(StudyProgram::getFaculty), "F1")
                                             .set(field(StudyProgram::getType), "B")
                                             .set(field(StudyProgram::getForm), "P")
                                             .set(field(StudyProgram::getLanguage), "CZ")
                                             .set(field(StudyProgram::getProgramProfile), "A")
                                             .set(field(StudyProgram::getStandardLength), new BigDecimal("3.0"))
                                             .set(field(StudyProgram::getMaxLength), new BigDecimal("5.0"))
                                             .set(field(StudyProgram::getInternalCode), "SP002")
                                             .set(field(StudyProgram::getDateOfInsert), LocalDate.now())
                                             .set(field(StudyProgram::getCreditLimit), (short) 180)
                                             .set(field(StudyProgram::getReportedStatus), "A")
                                             .set(field(StudyProgram::getValidFrom), "2020")
                                             .set(field(StudyProgram::getInvalidFrom), null)
                                             .set(field(StudyProgram::getDiploma), "A")
                                             .set(field(StudyProgram::getDocument), "A")
                                             .set(field(StudyProgram::getDiplomaType), "D1")
                                             .set(field(StudyProgram::getReportType), "R1")
                                             .set(field(StudyProgram::getCertificateType), "C1")
                                             .set(field(StudyProgram::getNameCz), "Test Program CZ")
                                             .set(field(StudyProgram::getNameEn), "Test Program EN")
                                             .set(field(StudyProgram::getGoalsCz), "Goals")
                                             .set(field(StudyProgram::getUniversityPaper), "A")
                                             .set(field(StudyProgram::getReportToHealthInsurance), "A")
                                             .set(field(StudyProgram::getStudentIdMask), "A##1")
                                             .set(field(StudyProgram::getProfessionalStatus), "nespec")
                                             .set(field(StudyProgram::getAccreditationWithFieldNumber), "A")
                                             .set(field(StudyProgram::getInstitutionalAccreditation), "N")
                                             .set(field(StudyProgram::getAccreditationPostAmendment), "A")
                                             .set(field(StudyProgram::getEconomicComplexityCoefficient), new BigDecimal("1"))
                                             .set(field(StudyProgram::getMaxInterruptionLength), new BigDecimal("1.0"))
                                             .set(field(StudyProgram::getJointDegrees), "1")
                                             .set(field(StudyProgram::getJointFacultyInstruction), "Test")
                                             .set(field(StudyProgram::getTotalEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getCertificatesEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getOpponentEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getThesisEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getFinalThesisEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getSupervisorEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getSubjectTotalEvaluationTypeId), 1L)
                                             .set(field(StudyProgram::getPrintSpecializationOnDiploma), "N")
                                             .set(field(StudyProgram::getRigorosumPossibility), "N")
                                             .set(field(StudyProgram::getIsFollowUp), "N")
                                             .set(field(StudyProgram::getDegree), "Bc.")
                                             .set(field(StudyProgram::getOwner), "SYSTEM")
                                             .set(field(StudyProgram::getRequiredEducationLevel), "K")
                                             .set(field(StudyProgram::getLimitedAccreditationReason), "N")
                                             .set(field(StudyProgram::getRigorosumDegree), "Ph")
                                             .set(field(StudyProgram::getLifelongLearningProgramPurpose), "N")
                                             .set(field(StudyProgram::getFinalThesisType), "DT")
                                             .create();

        studyProgramRepository.saveAndFlush(studyProgram);

        FieldOfStudy fieldOfStudy = Instancio.of(FieldOfStudy.class)
                                             .set(field(FieldOfStudy::getId), 2L)
                                             .set(field(FieldOfStudy::getStudyProgram), studyProgram)
                                             .set(field(FieldOfStudy::getFieldNumber), "FOS002")
                                             .set(field(FieldOfStudy::getSpecializationNumber), "00")
                                             .set(field(FieldOfStudy::getFaculty), "F1")
                                             .set(field(FieldOfStudy::getDepartment), "KIV")
                                             .set(field(FieldOfStudy::getForm), "P")
                                             .set(field(FieldOfStudy::getType), "B")
                                             .set(field(FieldOfStudy::getValidFrom), "2020")
                                             .set(field(FieldOfStudy::getDateOfInsert), LocalDate.now())
                                             .set(field(FieldOfStudy::getNameCz), "Field CZ")
                                             .set(field(FieldOfStudy::getNameEn), "Field EN")
                                             .set(field(FieldOfStudy::getOwner), "SYSTEM")
                                             .set(field(FieldOfStudy::getCreditLimit), (short) 180)
                                             .set(field(FieldOfStudy::getReportedStatus), "A")
                                             .set(field(FieldOfStudy::getIsTeachingQualification), "N")
                                             .set(field(FieldOfStudy::getNumberOfStages), false)
                                             .set(field(FieldOfStudy::getPrintStatus), "A")
                                             .set(field(FieldOfStudy::getHasDissertation), "N")
                                             .set(field(FieldOfStudy::getPrintSpecialization), "A")
                                             .set(field(FieldOfStudy::getProfessionalStatus), "nespec")
                                             .set(field(FieldOfStudy::getReportToHealthRegistry), "N")
                                             .set(field(FieldOfStudy::getIssueDiplomaAndSupplement), "A")
                                             .set(field(FieldOfStudy::getJointDegrees), "1")
                                             .set(field(FieldOfStudy::getParticipationForm), "1")
                                             .set(field(FieldOfStudy::getMicroCertificateEqf), "1")
                                             .set(field(FieldOfStudy::getInvalidFrom), "2025")
                                             .set(field(FieldOfStudy::getEducationArea), "123")
                                             .set(field(FieldOfStudy::getEvaluationType), "Z")
                                             .set(field(FieldOfStudy::getAdmissionRequirements), "1")
                                             .set(field(FieldOfStudy::getCourseDurationUnit), "D")
                                             .set(field(FieldOfStudy::getProgramProfile), "P")
                                             .set(field(FieldOfStudy::getSupervisionAndVerificationType), "D")
                                             .set(field(FieldOfStudy::getQualityAssuranceType), "I")
                                             .set(field(FieldOfStudy::getCatalogStatus), "N")
                                             .set(field(FieldOfStudy::getNumberA), "01")
                                             .set(field(FieldOfStudy::getPrintSpecializationOnDiploma), "N")
                                             .set(field(FieldOfStudy::getStandardLength), new BigDecimal("3.0"))
                                             .set(field(FieldOfStudy::getMaxLength), new BigDecimal("5.0"))
                                             .create();

        testEntityManager.persistAndFlush(fieldOfStudy);

        StudyPlan studyPlan = Instancio.of(StudyPlan.class)
                                       .set(field(StudyPlan::getId), 2L)
                                       .set(field(StudyPlan::getFieldOfStudy), fieldOfStudy)
                                       .set(field(StudyPlan::getAcademicYear), "2020")
                                       .set(field(StudyPlan::getStage), "1")
                                       .set(field(StudyPlan::getVersion), "1")
                                       .set(field(StudyPlan::getLanguage), "CZ")
                                       .set(field(StudyPlan::getDateOfInsert), LocalDate.now())
                                       .set(field(StudyPlan::getIsCreditBased), "A")
                                       .set(field(StudyPlan::getCreditLimit), (short) 180)
                                       .set(field(StudyPlan::getOwner), "SYSTEM")
                                       .set(field(StudyPlan::getNumberOfSemesters), (short) 6)
                                       .set(field(StudyPlan::getIsSpecialization), "N")
                                       .set(field(StudyPlan::getDisplayEcts), "A")
                                       .set(field(StudyPlan::getEnrollmentLimitation), "L")
                                       .set(field(StudyPlan::getIsForFirstYearStudents), "N")
                                       .create();

        studyPlanRepository.saveAndFlush(studyPlan);

        Optional<FieldOfStudyView> result = studyPlanRepository.findFieldOfStudy(2L, "cs");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Field CZ");
    }

    @Test
    @DisplayName("should return empty when study plan does not exist")
    void findFieldOfStudy_StudyPlanDoesNotExist_ReturnsEmpty() {
        Optional<FieldOfStudyView> result = studyPlanRepository.findFieldOfStudy(999L, "en");

        assertThat(result).isEmpty();
    }

}