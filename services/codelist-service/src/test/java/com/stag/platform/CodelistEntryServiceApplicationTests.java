package com.stag.platform;

import com.stag.platform.config.TestCacheConfig;
import com.stag.platform.config.TestOracleContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class})
@ActiveProfiles("test")
class CodelistEntryServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
