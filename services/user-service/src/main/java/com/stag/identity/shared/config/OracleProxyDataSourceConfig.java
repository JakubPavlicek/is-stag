package com.stag.identity.shared.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/// **Oracle Proxy DataSource Configuration**
///
/// Configures HikariCP connection pool with Oracle proxy session support.
/// Creates a proxy user configuration allowing the application to connect through a proxy account,
/// then switch to target user context for proper audit trails and permissions.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("!test")
@Configuration
public class OracleProxyDataSourceConfig {

    /// Target user that will be proxied
    @Value("${spring.datasource.proxy.target-username}")
    private String targetUser;

    /// Creates a [HikariDataSource] configured from `spring.datasource.*` properties.
    ///
    /// @param properties the autoconfigured data source properties
    /// @return a HikariCP connection pool instance
    @Bean("hikariDataSource")
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource hikariDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                         .type(HikariDataSource.class)
                         .build();
    }

    /// Creates the primary [DataSource] by wrapping the HikariCP pool with Oracle proxy session support.
    ///
    /// @param hikariDataSource the underlying HikariCP data source
    /// @return a proxy-aware data source that switches to the target user context per connection
    @Bean
    @Primary
    public DataSource dataSource(
        @Qualifier("hikariDataSource") HikariDataSource hikariDataSource
    ) {
        return new OracleProxyDataSource(hikariDataSource, targetUser);
    }

}
