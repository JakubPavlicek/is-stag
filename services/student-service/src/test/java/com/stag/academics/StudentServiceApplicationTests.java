package com.stag.academics;

import com.stag.academics.config.TestCacheConfig;
import com.stag.academics.config.TestOracleContainerConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class })
@ActiveProfiles("test")
class StudentServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(StudentServiceApplication.class, new String[]{}))
                  .thenReturn(null);

            StudentServiceApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(StudentServiceApplication.class, new String[]{}));
        }
    }

}
