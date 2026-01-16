package com.stag.platform.gateway;

import com.stag.platform.gateway.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(ApiGatewayApplication.class, new String[]{}))
                  .thenReturn(null);

            ApiGatewayApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(ApiGatewayApplication.class, new String[]{}));
        }
    }

}
