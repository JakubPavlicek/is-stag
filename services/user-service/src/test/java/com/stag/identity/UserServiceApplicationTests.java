package com.stag.identity;

import com.stag.identity.config.TestCacheConfig;
import com.stag.identity.config.TestOracleContainerConfig;
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
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main() {
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
			mocked.when(() -> SpringApplication.run(UserServiceApplication.class, new String[]{}))
				  .thenReturn(null);

			UserServiceApplication.main(new String[]{});

			mocked.verify(() -> SpringApplication.run(UserServiceApplication.class, new String[]{}));
		}
	}

}
