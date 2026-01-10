package com.stag.identity.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class TestOracleContainerConfig {

    /// @see <a href="https://github.com/testcontainers/testcontainers-java/issues/4615">Testcontainers Oracle DB issue</a>
    /// @see <a href="https://medium.com/turkcell/why-oracle-free-edition-will-sabotage-your-multi-schema-application-in-testcontainers-and-what-e00f72f3f11c">Possible workaround</a>
    @Container
    private static final OracleContainer oracleContainer =
        new OracleContainer(DockerImageName.parse("gvenzl/oracle-free:23-slim-faststart"))
            .withUsername("INSTALL2")
            .withPassword("install2")
            .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"), "container-entrypoint-initdb.d/init.sql");

    @Bean
    @ServiceConnection
    public OracleContainer oracleContainer() {
        return oracleContainer;
    }

}
