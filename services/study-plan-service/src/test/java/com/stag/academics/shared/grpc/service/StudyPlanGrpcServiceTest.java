package com.stag.academics.shared.grpc.service;

import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import com.stag.academics.studyplan.service.StudyPlanService;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldRequest;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import com.stag.academics.studyprogram.exception.StudyProgramNotFoundException;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import com.stag.academics.studyprogram.service.StudyProgramService;
import io.grpc.stub.StreamObserver;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyPlanGrpcServiceTest {

    @Mock
    private StudyProgramService studyProgramService;

    @Mock
    private StudyPlanService studyPlanService;

    @Mock
    private StreamObserver<GetStudyProgramAndFieldResponse> responseObserver;

    @InjectMocks
    private StudyPlanGrpcService grpcService;

    @Test
    @DisplayName("should return response when both services succeed")
    void getStudyProgramAndField_Success() {
        long programId = 1L;
        long planId = 10L;
        String language = "en";

        GetStudyProgramAndFieldRequest request = GetStudyProgramAndFieldRequest.newBuilder()
            .setStudyProgramId(programId)
            .setStudyPlanId(planId)
            .setLanguage(language)
            .build();

        StudyProgramView programView = Instancio.create(StudyProgramView.class);
        FieldOfStudyView fieldView = Instancio.create(FieldOfStudyView.class);

        when(studyProgramService.findStudyProgram(programId, language)).thenReturn(programView);
        when(studyPlanService.findFieldOfStudy(planId, language)).thenReturn(fieldView);

        grpcService.getStudyProgramAndField(request, responseObserver);

        verify(responseObserver).onNext(any(GetStudyProgramAndFieldResponse.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    @DisplayName("should error when study program not found")
    void getStudyProgramAndField_ProgramNotFound() {
        Long programId = 1L;
        long planId = 10L;
        String language = "en";

        GetStudyProgramAndFieldRequest request = GetStudyProgramAndFieldRequest.newBuilder()
            .setStudyProgramId(programId)
            .setStudyPlanId(planId)
            .setLanguage(language)
            .build();

        StudyProgramNotFoundException exception = new StudyProgramNotFoundException(programId);

        when(studyProgramService.findStudyProgram(programId, language)).thenThrow(exception);

        grpcService.getStudyProgramAndField(request, responseObserver);

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(captor.capture());
        
        assertThat(captor.getValue()).isNull();
    }

    @Test
    @DisplayName("should handle interruption during execution")
    void getStudyProgramAndField_Interrupted() {
        long programId = 1L;
        long planId = 10L;
        String language = "en";

        GetStudyProgramAndFieldRequest request = GetStudyProgramAndFieldRequest.newBuilder()
            .setStudyProgramId(programId)
            .setStudyPlanId(planId)
            .setLanguage(language)
            .build();

        CountDownLatch taskStarted = new CountDownLatch(1);

        when(studyProgramService.findStudyProgram(any(), any())).thenAnswer(_ -> {
            taskStarted.countDown();
            Thread.sleep(Long.MAX_VALUE);
            return null;
        });
        when(studyPlanService.findFieldOfStudy(any(), any())).thenReturn(Instancio.create(FieldOfStudyView.class));

        AtomicReference<Thread> callerThread = new AtomicReference<>();

        Thread testThread = new Thread(() -> {
            callerThread.set(Thread.currentThread());
            grpcService.getStudyProgramAndField(request, responseObserver);
        });
        testThread.start();

        await().atMost(Duration.ofSeconds(5)).until(() -> taskStarted.getCount() == 0);

        testThread.interrupt();

        await().atMost(Duration.ofSeconds(5)).until(() -> !testThread.isAlive());

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(captor.capture());
        
        assertThat(captor.getValue()).isNull();
    }
}
