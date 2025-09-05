package com.stag.academics.shared.grpc.client;

import com.stag.academics.shared.grpc.mapper.StudyPlanMapper;
import com.stag.academics.student.service.data.StudyProgramAndFieldLookupData;
import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyPlanClient {

    @GrpcClient("study-plan-service")
    private StudyPlanServiceGrpc.StudyPlanServiceBlockingStub studyPlanServiceStub;

    /// Get Study program and Field of study data specifically for student profile (GET /students/{studentId})
    public StudyProgramAndFieldLookupData getStudyProgramAndField(Long studyProgramId, Long studyPlanId, String language) {
        var request = StudyPlanMapper.INSTANCE.toStudyProgramAndFieldDataRequest(studyProgramId, studyPlanId, language);
        var response = studyPlanServiceStub.getStudyProgramAndField(request);
        return StudyPlanMapper.INSTANCE.toStudyProgramAndFieldData(response);
    }

}
