package com.stag.identity;

import com.stag.identity.config.TestCacheConfig;
import com.stag.identity.config.TestOracleContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({ TestOracleContainerConfig.class, TestCacheConfig.class })
@ActiveProfiles("test")
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
