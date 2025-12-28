package com.stag.identity.shared.config;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleConnection;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/// **Proxy Oracle Connection Wrapper**
///
/// A wrapper for a physical Oracle connection that manages the lifecycle of a proxy session.
/// It delegates all calls to the physical connection but intercepts the close() call
/// to ensure the proxy session is terminated before the physical connection is returned to the pool.
/// This prevents proxy session leaks in connection pooling scenarios.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
public class ProxyOracleConnection implements Connection {

    private final Connection physicalConnection;
    private final OracleConnection oracleConnection;
    private boolean isClosed = false;

    /// Constructs a new ProxyOracleConnection wrapper.
    ///
    /// @param physicalConnection the physical Oracle connection with an active proxy session
    /// @throws SQLException if the connection cannot be unwrapped
    public ProxyOracleConnection(Connection physicalConnection) throws SQLException {
        this.physicalConnection = physicalConnection;
        this.oracleConnection = physicalConnection.unwrap(OracleConnection.class);
    }

    /// Intercepts the close() call to manage the proxy session lifecycle.
    /// Closes the Oracle proxy session first, then returns the physical connection to the pool.
    ///
    /// @throws SQLException if an error occurs during closing
    @Override
    public void close() throws SQLException {
        if (isClosed) {
            return;
        }

        try {
            // 1. Close the Oracle proxy session first.
            if (oracleConnection.isProxySession()) {
                log.debug("Closing Oracle proxy session.");
                oracleConnection.close(OracleConnection.PROXY_SESSION);
            }

            // 2. Now, close the physical connection (which returns it to the pool).
            physicalConnection.close();
            isClosed = true;
        } catch (SQLException e) {
            log.error("Error closing proxy connection", e);
            // Attempt to close the physical connection anyway to prevent leaks
            if (!physicalConnection.isClosed()) {
                physicalConnection.close();
            }
            throw e;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed || physicalConnection.isClosed();
    }

    // --- All other methods are delegated directly to the physical connection ---

    @Override
    public Statement createStatement() throws SQLException {
        return physicalConnection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return physicalConnection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return physicalConnection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return physicalConnection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        physicalConnection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return physicalConnection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        log.debug(">>>> COMMITTING transaction in proxy session <<<<");
        if (!physicalConnection.getAutoCommit()) {
            physicalConnection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        log.debug(">>>> ROLLING BACK transaction in proxy session <<<<");
        physicalConnection.rollback();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return physicalConnection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        physicalConnection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return physicalConnection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        physicalConnection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return physicalConnection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        physicalConnection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return physicalConnection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return physicalConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        physicalConnection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return physicalConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return physicalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return physicalConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return physicalConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        physicalConnection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        physicalConnection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return physicalConnection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return physicalConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return physicalConnection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        physicalConnection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        physicalConnection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return physicalConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return physicalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return physicalConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return physicalConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return physicalConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return physicalConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return physicalConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return physicalConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return physicalConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return physicalConnection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return physicalConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        physicalConnection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        physicalConnection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return physicalConnection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return physicalConnection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return physicalConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return physicalConnection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        physicalConnection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return physicalConnection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        physicalConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        physicalConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return physicalConnection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return physicalConnection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return true;
        }
        return physicalConnection.isWrapperFor(iface);
    }

}
