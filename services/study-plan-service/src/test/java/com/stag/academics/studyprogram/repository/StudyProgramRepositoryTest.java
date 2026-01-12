package com.stag.academics.studyprogram.repository;

import com.stag.academics.config.TestCacheConfig;
import com.stag.academics.config.TestOracleContainerConfig;
import com.stag.academics.studyprogram.entity.StudyProgram;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
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
class StudyProgramRepositoryTest {

    @Autowired
    private StudyProgramRepository studyProgramRepository;

    @Test
    @DisplayName("should return study program view with English name when language is 'en'")
    void findStudyProgramViewById_EnglishLanguage_ReturnsEnglishName() {
        StudyProgram studyProgram = Instancio.of(StudyProgram.class)
                                             .set(field(StudyProgram::getId), 1L)
                                             .set(field(StudyProgram::getFaculty), "FAV")
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
                                             .set(field(StudyProgram::getNameCz), "Informatika CZ")
                                             .set(field(StudyProgram::getNameEn), "Computer Science EN")
                                             .set(field(StudyProgram::getCode), "B1234")
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

        Optional<StudyProgramView> result = studyProgramRepository.findStudyProgramViewById(1L, "en");

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().name()).isEqualTo("Computer Science EN");
        assertThat(result.get().faculty()).isEqualTo("FAV");
        assertThat(result.get().code()).isEqualTo("B1234");
        assertThat(result.get().form()).isEqualTo("P");
        assertThat(result.get().type()).isEqualTo("B");
    }

    @Test
    @DisplayName("should return study program view with Czech name when language is 'cs'")
    void findStudyProgramViewById_CzechLanguage_ReturnsCzechName() {
        StudyProgram studyProgram = Instancio.of(StudyProgram.class)
                                             .set(field(StudyProgram::getId), 2L)
                                             .set(field(StudyProgram::getFaculty), "FAV")
                                             .set(field(StudyProgram::getType), "M")
                                             .set(field(StudyProgram::getForm), "K")
                                             .set(field(StudyProgram::getLanguage), "CZ")
                                             .set(field(StudyProgram::getProgramProfile), "A")
                                             .set(field(StudyProgram::getStandardLength), new BigDecimal("2.0"))
                                             .set(field(StudyProgram::getMaxLength), new BigDecimal("4.0"))
                                             .set(field(StudyProgram::getInternalCode), "SP002")
                                             .set(field(StudyProgram::getDateOfInsert), LocalDate.now())
                                             .set(field(StudyProgram::getCreditLimit), (short) 120)
                                             .set(field(StudyProgram::getReportedStatus), "A")
                                             .set(field(StudyProgram::getValidFrom), "2020")
                                             .set(field(StudyProgram::getInvalidFrom), null)
                                             .set(field(StudyProgram::getDiploma), "A")
                                             .set(field(StudyProgram::getDocument), "A")
                                             .set(field(StudyProgram::getDiplomaType), "D1")
                                             .set(field(StudyProgram::getReportType), "R1")
                                             .set(field(StudyProgram::getCertificateType), "C1")
                                             .set(field(StudyProgram::getNameCz), "Informatika CZ")
                                             .set(field(StudyProgram::getNameEn), "Computer Science EN")
                                             .set(field(StudyProgram::getCode), "N1234")
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
                                             .set(field(StudyProgram::getIsFollowUp), "A")
                                             .set(field(StudyProgram::getDegree), "Ing.")
                                             .set(field(StudyProgram::getOwner), "SYSTEM")
                                             .set(field(StudyProgram::getRequiredEducationLevel), "K")
                                             .set(field(StudyProgram::getLimitedAccreditationReason), "N")
                                             .set(field(StudyProgram::getRigorosumDegree), "Ph")
                                             .set(field(StudyProgram::getLifelongLearningProgramPurpose), "N")
                                             .set(field(StudyProgram::getFinalThesisType), "DT")
                                             .create();

        studyProgramRepository.saveAndFlush(studyProgram);

        Optional<StudyProgramView> result = studyProgramRepository.findStudyProgramViewById(2L, "cs");

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(2L);
        assertThat(result.get().name()).isEqualTo("Informatika CZ");
        assertThat(result.get().faculty()).isEqualTo("FAV");
        assertThat(result.get().code()).isEqualTo("N1234");
        assertThat(result.get().form()).isEqualTo("K");
        assertThat(result.get().type()).isEqualTo("M");
    }

    @Test
    @DisplayName("should return empty when study program does not exist")
    void findStudyProgramViewById_NotFound_ReturnsEmpty() {
        Optional<StudyProgramView> result = studyProgramRepository.findStudyProgramViewById(999L, "en");

        assertThat(result).isEmpty();
    }

}
