package com.stag.academics;

import com.stag.academics.config.TestCacheConfig;
import com.stag.academics.config.TestOracleContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class })
@ActiveProfiles("test")
class StudentServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
