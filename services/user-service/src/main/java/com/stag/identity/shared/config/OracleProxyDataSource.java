package com.stag.identity.shared.config;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleConnection;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/// **Oracle Proxy DataSource**
///
/// A DataSource wrapper that automatically opens and closes Oracle proxy sessions.
/// It extends Spring's DelegatingDataSource for robustness and compatibility.
/// Each connection obtained from this DataSource will have an active proxy session
/// for the configured target user.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
public class OracleProxyDataSource extends DelegatingDataSource {

    /// Target User that will be proxied
    private final String targetUser;

    /// Constructs a new OracleProxyDataSource.
    ///
    /// @param targetDataSource the underlying DataSource to delegate to
    /// @param targetUser the Oracle user to proxy as
    public OracleProxyDataSource(DataSource targetDataSource, String targetUser) {
        super(targetDataSource);
        this.targetUser = targetUser;
    }

    /// Gets a connection with an active Oracle proxy session.
    ///
    /// @return a Connection wrapped in ProxyOracleConnection
    /// @throws SQLException if the proxy session cannot be opened
    @Override
    @NonNull
    public Connection getConnection() throws SQLException {
        // Get a physical connection from the underlying pool (which connects as the proxy user)
        Connection physicalConnection = super.getConnection();

        try {
            OracleConnection oracleConnection = physicalConnection.unwrap(OracleConnection.class);

            // The ProxyOracleConnection wrapper will close the session, so we should always open a new one.
            // This check is a safeguard against unexpected states.
            if (oracleConnection.isProxySession()) {
                log.warn("Connection from pool already has an open proxy session. Closing it before opening a new one.");
                oracleConnection.close(OracleConnection.PROXY_SESSION);
            }

            Properties props = new Properties();
            props.put(OracleConnection.PROXY_USER_NAME, targetUser);

            log.debug("Opening Oracle proxy session for target user: {}", targetUser);
            oracleConnection.openProxySession(OracleConnection.PROXYTYPE_USER_NAME, props);

            log.debug("Creating connection with auto-commit: {}", physicalConnection.getAutoCommit());

            // Return our custom wrapper that intercepts the close() call.
            return new ProxyOracleConnection(physicalConnection);
        } catch (SQLException e) {
            // If we fail to open the proxy, close the physical connection to prevent leaks.
            try {
                physicalConnection.close();
            } catch (SQLException closeEx) {
                log.warn("Failed to close physical connection after proxy session error", closeEx);
            }
            log.error("Failed to open Oracle proxy session for user: {}", targetUser, e);
            throw new SQLException("Failed to open Oracle proxy session for user: " + targetUser, e);
        }
    }

    /// This method is not supported in the proxy setup.
    ///
    /// @param username ignored
    /// @param password ignored
    /// @throws UnsupportedOperationException always
    @Override
    @NonNull
    public Connection getConnection(@NonNull String username, @NonNull String password) {
        // This method should not be used in this proxy setup, as the user is determined by the configuration.
        throw new UnsupportedOperationException("Use the no-argument getConnection() method.");
    }

}
