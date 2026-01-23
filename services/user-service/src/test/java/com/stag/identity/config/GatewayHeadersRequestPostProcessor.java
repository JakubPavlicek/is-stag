package com.stag.identity.config;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/// **Gateway Headers Request Post Processor**
///
/// Test helper for MockMvc that simulates gateway authentication headers.
/// This replaces the old `.with(jwt())` pattern with header-based authentication.
///
/// **Usage**
///
/// ```java
/// import static com.stag.identity.config.GatewayHeadersRequestPostProcessor.gatewayHeaders;
///
/// mvc.get()
///    .uri("/api/v1/persons/12345")
///    .with(gatewayHeaders())  // Default student user
///    .accept(MediaType.APPLICATION_JSON);
///
/// mvc.get()
///    .uri("/api/v1/persons/12345")
///    .with(gatewayHeaders().studentId("S999").roles("ST", "AD"))
///    .accept(MediaType.APPLICATION_JSON);
/// ```
///
/// @author Jakub Pavlicek
/// @version 1.0.0
public class GatewayHeadersRequestPostProcessor implements RequestPostProcessor {

    private String studentId;
    private String teacherId;
    private String email;
    private String roles;

    private GatewayHeadersRequestPostProcessor() {
        // Default test user (matches TestSecurityConfig default)
        this.studentId = "12345";
        this.email = "test@example.com";
        this.roles = "AD";
    }

    /// Creates a new instance with default test user.
    ///
    /// @return new instance with default student user
    public static GatewayHeadersRequestPostProcessor gatewayHeaders() {
        return new GatewayHeadersRequestPostProcessor();
    }

    /// Sets the student ID header.
    ///
    /// @param studentId the student ID
    /// @return this instance for method chaining
    public GatewayHeadersRequestPostProcessor studentId(String studentId) {
        this.studentId = studentId;
        this.teacherId = null; // Clear teacher ID if setting student ID
        return this;
    }

    /// Sets the teacher ID header.
    ///
    /// @param teacherId the teacher ID
    /// @return this instance for method chaining
    public GatewayHeadersRequestPostProcessor teacherId(String teacherId) {
        this.teacherId = teacherId;
        this.studentId = null; // Clear student ID if setting teacher ID
        return this;
    }

    /// Sets the email header.
    ///
    /// @param email the email address
    /// @return this instance for method chaining
    public GatewayHeadersRequestPostProcessor email(String email) {
        this.email = email;
        return this;
    }

    /// Sets the roles header (comma-separated).
    ///
    /// @param roles one or more roles
    /// @return this instance for method chaining
    public GatewayHeadersRequestPostProcessor roles(String... roles) {
        this.roles = String.join(",", roles);
        return this;
    }

    @Override
    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        if (studentId != null) {
            request.addHeader("X-Student-Id", studentId);
        }
        if (teacherId != null) {
            request.addHeader("X-Teacher-Id", teacherId);
        }
        if (email != null) {
            request.addHeader("X-Email", email);
        }
        if (roles != null) {
            request.addHeader("X-Roles", roles);
        }
        return request;
    }

}
