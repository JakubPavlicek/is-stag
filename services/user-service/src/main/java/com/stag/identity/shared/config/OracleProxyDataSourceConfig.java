package com.stag.identity.shared.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.SwitchableBugFix;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/// **Oracle Proxy DataSource Configuration**
///
/// Configures HikariCP connection pool with Oracle proxy session support.
/// Creates a proxy user configuration allowing the application to connect through a proxy account,
/// then switch to target user context for proper audit trails and permissions.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Configuration
public class OracleProxyDataSourceConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.proxy.username}")
    private String proxyUser;

    @Value("${spring.datasource.proxy.password}")
    private String proxyPassword;

    @Value("${spring.datasource.proxy.target-username}")
    private String targetUser;

    /// Creates and configures the primary datasource with Oracle proxy session support.
    /// Wraps HikariCP pool with OracleProxyDataSource to automatically manage the proxy session lifecycle.
    ///
    /// @return configured datasource with proxy support
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = getHikariConfig();

        // Create a HikariCP pool that connects as a proxy user
        DataSource hikariDataSource = new HikariDataSource(config);

        // Wrap with OracleProxyDataSource to handle openProxySession() calls
        return new OracleProxyDataSource(hikariDataSource, targetUser);
    }

    /// Builds HikariCP configuration with Oracle-specific optimizations.
    /// Configures connection pool size, timeouts, and Oracle driver properties for optimal performance and connection validation.
    ///
    /// @return HikariCP configuration
    @NonNull
    private HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("oracle.jdbc.OracleDriver");

        // Set proxy user credentials - HikariCP will connect as this user
        config.setUsername(proxyUser);
        config.setPassword(proxyPassword);

        // HikariCP pool settings
        config.setAutoCommit(false);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(30);
        config.setIdleTimeout(600000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);

        // Oracle-specific optimizations
        config.addDataSourceProperty(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "500");
        config.addDataSourceProperty(OracleConnection.CONNECTION_PROPERTY_DEFAULT_CONNECTION_VALIDATION, "LOCAL");
        config.addDataSourceProperty(OracleConnection.CONNECTION_PROPERTY_IMPLICIT_STATEMENT_CACHE_SIZE, "200");
        config.addDataSourceProperty(OracleConnection.CONNECTION_PROPERTY_DISABLED_BUG_FIXES, SwitchableBugFix.BugNumber.BUG_11891661);

        return config;
    }

}
