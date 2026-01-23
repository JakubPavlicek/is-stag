package com.stag.academics.student.controller;

import com.stag.academics.config.TestCacheConfig;
import com.stag.academics.shared.config.SecurityConfig;
import com.stag.academics.student.exception.StudentNotFoundException;
import com.stag.academics.student.exception.StudentProfileFetchException;
import com.stag.academics.student.model.Profile;
import com.stag.academics.student.service.StudentService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.concurrent.CompletionException;

import static com.stag.academics.config.GatewayHeadersRequestPostProcessor.gatewayHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(StudentController.class)
@Import({ TestCacheConfig.class, SecurityConfig.class })
@ActiveProfiles("test")
class StudentControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    @DisplayName("should return 200 OK with student profile when valid studentId and English language provided")
    void getStudentProfile_ValidStudentIdAndEnglishLanguage_ReturnsOkWithStudentProfile() {
        String studentId = "S123";
        String language = "en";

        Profile profile = Profile.builder()
                                 .studentId(studentId)
                                 .personId(123)
                                 .firstName("John")
                                 .lastName("Doe")
                                 .gender("M")
                                 .studyProgram(new Profile.StudyProgram(1, "Program", "FAC", "C1", "F1", "T1"))
                                 .fieldOfStudy(new Profile.FieldOfStudy(1, "Field", "FAC", "DEP", "C2"))
                                 .build();

        when(studentService.getStudentProfile(studentId, language)).thenReturn(profile);

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatusOk()
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.studentId").isEqualTo(studentId);
                json.assertThat().extractingPath("$.personId").isEqualTo(123);
                json.assertThat().extractingPath("$.firstName").isEqualTo("John");
                json.assertThat().extractingPath("$.lastName").isEqualTo("Doe");
                json.assertThat().extractingPath("$.gender").isEqualTo("M");
                json.assertThat().extractingPath("$.studyProgram.name").isEqualTo("Program");
                json.assertThat().extractingPath("$.fieldOfStudy.name").isEqualTo("Field");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 404 Not Found when student does not exist")
    void getStudentProfile_StudentNotFound_Returns404() {
        String studentId = "UNKNOWN";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(new StudentNotFoundException(studentId));

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(404)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(404);
                json.assertThat().extractingPath("$.title").isEqualTo("Student Not Found");
                json.assertThat().extractingPath("$.detail").asString().contains("Student with ID: UNKNOWN not found");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 500 Internal Server Error when fetching student profile fails")
    void getStudentProfile_ProfileFetchFailed_Returns500() {
        String studentId = "S123";
        String language = "en";

        doThrow(new StudentProfileFetchException(studentId, new RuntimeException("fetch failed")))
            .when(studentService)
            .getStudentProfile(studentId, language);

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .with(gatewayHeaders())
                      .accept(MediaType.APPLICATION_JSON))
            .hasStatus(500)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(500);
                json.assertThat().extractingPath("$.title").isEqualTo("Student Profile Fetch Error");
                json.assertThat().extractingPath("$.detail").asString().contains("Failed to fetch student profile for studentId=S123");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 403 Unauthorized when no auth headers are provided")
    void getStudentProfile_NoJwtToken_Returns401() {
        String studentId = "S123";
        String language = "en";

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON))
            .hasStatus(403);
    }

    @Test
    @DisplayName("should return 403 Forbidden when AccessDeniedException is thrown")
    void getStudentProfile_AccessDenied_Returns403() {
        String studentId = "S123";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(new AccessDeniedException("Access is denied"));

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(403)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(403);
                json.assertThat().extractingPath("$.title").isEqualTo("Access Denied");
                json.assertThat().extractingPath("$.detail").asString().contains("Access is denied");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 503 Service Unavailable when CallNotPermittedException is thrown")
    void getStudentProfile_CircuitBreakerOpen_Returns503() {
        String studentId = "S123";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(CallNotPermittedException.class);

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(503)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(503);
                json.assertThat().extractingPath("$.title").isEqualTo("Service Unavailable");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 404 Not Found when StatusRuntimeException with NOT_FOUND is thrown (e.g. CodelistNotFound)")
    void getStudentProfile_GrpcNotFound_Returns404() {
        String studentId = "S123";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(404)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(404);
                json.assertThat().extractingPath("$.title").isEqualTo("Resource Not Found");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 400 Bad Request when StatusRuntimeException with INVALID_ARGUMENT is thrown")
    void getStudentProfile_GrpcInvalidArgument_Returns400() {
        String studentId = "S123";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Argument");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 503 Service Unavailable when StatusRuntimeException with UNAVAILABLE is thrown")
    void getStudentProfile_GrpcUnavailable_Returns503() {
        String studentId = "S123";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(503)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(503);
                json.assertThat().extractingPath("$.title").isEqualTo("Service Unavailable");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 504 Gateway Timeout when StatusRuntimeException with DEADLINE_EXCEEDED is thrown")
    void getStudentProfile_GrpcDeadlineExceeded_Returns504() {
        String studentId = "S123";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(new StatusRuntimeException(Status.DEADLINE_EXCEEDED));

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(504)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(504);
                json.assertThat().extractingPath("$.title").isEqualTo("Gateway Timeout");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @ParameterizedTest(name = "should return mapped status for gRPC status {0}")
    @EnumSource(value = Status.Code.class, names = {
        "PERMISSION_DENIED", "UNAUTHENTICATED", "ALREADY_EXISTS", 
        "FAILED_PRECONDITION", "UNIMPLEMENTED"
    })
    @DisplayName("should return mapped status for other gRPC statuses")
    void getStudentProfile_OtherGrpcStatuses_ReturnsMappedStatus(Status.Code code) {
        String studentId = "S123";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(new StatusRuntimeException(Status.fromCode(code)));

        int expectedStatus = switch (code) {
            case PERMISSION_DENIED -> 403;
            case UNAUTHENTICATED -> 401;
            case ALREADY_EXISTS -> 409;
            case FAILED_PRECONDITION -> 412;
            case UNIMPLEMENTED -> 501;
            default -> 500;
        };

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(expectedStatus)
            .bodyJson()
            .satisfies(json -> json.assertThat().extractingPath("$.status").isEqualTo(expectedStatus));

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should unwrap and handle CompletionException with StatusRuntimeException")
    void getStudentProfile_CompletionExceptionWithGrpc_Returns404() {
        String studentId = "S123";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(new CompletionException(new StatusRuntimeException(Status.NOT_FOUND)));

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(404)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(404);
                json.assertThat().extractingPath("$.title").isEqualTo("Resource Not Found");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 500 Internal Server Error for unexpected exceptions")
    void getStudentProfile_UnexpectedException_Returns500() {
        String studentId = "S123";
        String language = "en";

        when(studentService.getStudentProfile(studentId, language))
            .thenThrow(new RuntimeException("Unexpected error"));

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", studentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(500)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(500);
                json.assertThat().extractingPath("$.title").isEqualTo("Internal Server Error");
                json.assertThat().extractingPath("$.detail").asString().contains("Unexpected error");
            });

        verify(studentService).getStudentProfile(studentId, language);
    }

    @Test
    @DisplayName("should return 400 Bad Request when studentId is too long (ConstraintViolationException)")
    void getStudentProfile_InvalidStudentId_Returns400() {
        String longStudentId = "12345678901";
        String language = "en";

        assertThat(mvc.get()
                      .uri("/api/v1/students/{studentId}", longStudentId)
                      .header(HttpHeaders.ACCEPT_LANGUAGE, language)
                      .accept(MediaType.APPLICATION_JSON)
                      .with(gatewayHeaders()))
            .hasStatus(400)
            .bodyJson()
            .satisfies(json -> {
                json.assertThat().extractingPath("$.status").isEqualTo(400);
                json.assertThat().extractingPath("$.title").isEqualTo("Invalid Value");
                json.assertThat().extractingPath("$.violations").isNotNull();
            });
    }

}
