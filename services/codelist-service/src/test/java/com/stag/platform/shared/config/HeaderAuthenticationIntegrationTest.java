package com.stag.platform.shared.config;

import com.stag.platform.config.TestCacheConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@WebMvcTest(controllers = HeaderAuthenticationIntegrationTest.TestController.class)
@Import({ HeaderAuthenticationIntegrationTest.TestSecurityConfig.class, TestCacheConfig.class })
class HeaderAuthenticationIntegrationTest {

    @Autowired
    private MockMvcTester mvc;

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        public HeaderAuthenticationFilter headerAuthenticationFilter() {
            return new HeaderAuthenticationFilter();
        }

        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) {
            return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/test-endpoint").hasRole("AD")
                    .anyRequest().authenticated()
                )
                .addFilterBefore(headerAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
        }

        @Bean
        public TestController testController() {
            return new TestController();
        }

    }

    @RestController
    static class TestController {

        @GetMapping("/test-endpoint")
        public Map<String, String> testEndpoint(Authentication authentication) {
            if (authentication != null && authentication.getPrincipal() instanceof GatewayAuthenticatedUser user) {
                return Map.of(
                    "status", "authenticated",
                    "studentId", user.studentId() != null ? user.studentId() : "",
                    "teacherId", user.teacherId() != null ? user.teacherId() : "",
                    "email", user.email() != null ? user.email() : "",
                    "isStudent", String.valueOf(user.isStudent())
                );
            }
            return Map.of("status", "not authenticated");
        }

    }

    @Test
    @DisplayName("should authenticate student with valid headers and AD role")
    void headerAuthenticationFilter_WithValidStudentHeaders_ReturnsOk() {
        mvc.get()
           .uri("/test-endpoint")
           .header(HeaderAuthenticationFilter.HEADER_STUDENT_ID, "S12345")
           .header(HeaderAuthenticationFilter.HEADER_EMAIL, "student@example.com")
           .header(HeaderAuthenticationFilter.HEADER_ROLES, "ST,AD")
           .exchange()
           .assertThat()
           .hasStatus(HttpStatus.OK)
           .bodyJson()
           .satisfies(json -> {
               json.assertThat().extractingPath("$.status").isEqualTo("authenticated");
               json.assertThat().extractingPath("$.studentId").isEqualTo("S12345");
               json.assertThat().extractingPath("$.email").isEqualTo("student@example.com");
               json.assertThat().extractingPath("$.isStudent").isEqualTo("true");
           });
    }

    @Test
    @DisplayName("should authenticate teacher with valid headers and AD role")
    void headerAuthenticationFilter_WithValidTeacherHeaders_ReturnsOk() {
        mvc.get()
           .uri("/test-endpoint")
           .header(HeaderAuthenticationFilter.HEADER_TEACHER_ID, "T99999")
           .header(HeaderAuthenticationFilter.HEADER_EMAIL, "teacher@example.com")
           .header(HeaderAuthenticationFilter.HEADER_ROLES, "VY,AD")
           .exchange()
           .assertThat()
           .hasStatus(HttpStatus.OK)
           .bodyJson()
           .satisfies(json -> {
               json.assertThat().extractingPath("$.status").isEqualTo("authenticated");
               json.assertThat().extractingPath("$.teacherId").isEqualTo("T99999");
               json.assertThat().extractingPath("$.email").isEqualTo("teacher@example.com");
               json.assertThat().extractingPath("$.isStudent").isEqualTo("false");
           });
    }

    @Test
    @DisplayName("should deny access when no identity headers present")
    void headerAuthenticationFilter_NoIdentityHeaders_ReturnsForbidden() {
        mvc.get()
           .uri("/test-endpoint")
           .header(HeaderAuthenticationFilter.HEADER_EMAIL, "user@example.com")
           .header(HeaderAuthenticationFilter.HEADER_ROLES, "AD")
           .exchange()
           .assertThat()
           .hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("should deny access when AD role is missing")
    void headerAuthenticationFilter_MissingAdRole_ReturnsForbidden() {
        mvc.get()
           .uri("/test-endpoint")
           .header(HeaderAuthenticationFilter.HEADER_STUDENT_ID, "S12345")
           .header(HeaderAuthenticationFilter.HEADER_EMAIL, "student@example.com")
           .header(HeaderAuthenticationFilter.HEADER_ROLES, "ST")
           .exchange()
           .assertThat()
           .hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("should parse roles with whitespace correctly")
    void headerAuthenticationFilter_WithWhitespaceInRoles_ParsesCorrectly() {
        mvc.get()
           .uri("/test-endpoint")
           .header(HeaderAuthenticationFilter.HEADER_STUDENT_ID, "S12345")
           .header(HeaderAuthenticationFilter.HEADER_EMAIL, "student@example.com")
           .header(HeaderAuthenticationFilter.HEADER_ROLES, " ST , AD , VY ")
           .exchange()
           .assertThat()
           .hasStatus(HttpStatus.OK)
           .bodyJson()
           .satisfies(json -> {
               json.assertThat().extractingPath("$.status").isEqualTo("authenticated");
               json.assertThat().extractingPath("$.studentId").isEqualTo("S12345");
           });
    }

    @Test
    @DisplayName("should deny access when roles header is empty")
    void headerAuthenticationFilter_EmptyRoles_ReturnsForbidden() {
        mvc.get()
           .uri("/test-endpoint")
           .header(HeaderAuthenticationFilter.HEADER_STUDENT_ID, "S12345")
           .header(HeaderAuthenticationFilter.HEADER_EMAIL, "student@example.com")
           .header(HeaderAuthenticationFilter.HEADER_ROLES, "")
           .exchange()
           .assertThat()
           .hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("should deny access when roles header contains only whitespace")
    void headerAuthenticationFilter_WhitespaceOnlyRoles_ReturnsForbidden() {
        mvc.get()
           .uri("/test-endpoint")
           .header(HeaderAuthenticationFilter.HEADER_STUDENT_ID, "S12345")
           .header(HeaderAuthenticationFilter.HEADER_EMAIL, "student@example.com")
           .header(HeaderAuthenticationFilter.HEADER_ROLES, "   ")
           .exchange()
           .assertThat()
           .hasStatus(HttpStatus.FORBIDDEN);
    }

}
