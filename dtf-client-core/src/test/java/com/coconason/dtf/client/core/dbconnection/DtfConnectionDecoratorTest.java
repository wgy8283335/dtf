package com.coconason.dtf.client.core.dbconnection;

import com.coconason.dtf.client.core.thread.ThreadLockCacheProxy;
import com.coconason.dtf.client.core.threadpools.ThreadPoolForClientConfiguration;
import com.coconason.dtf.client.core.threadpools.ThreadPoolForClientProxy;
import org.junit.Test;

import java.lang.reflect.Field;
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
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.assertNotNull;

public class DtfConnectionDecoratorTest {
    
    @Test
    public void assertConstructorWithOneParameter() {
        Connection connection = createConnection();
        Connection dtfConnection = new DtfConnectionDecorator(connection);
        assertNotNull(dtfConnection);
    }
    
    @Test
    public void assertConstructorWithMultiParameters() throws NoSuchFieldException, IllegalAccessException {
        Connection connection = createConnection();
        ThreadLockCacheProxy threadLockCacheProxy = new ThreadLockCacheProxy();
        Queue queue = new LinkedBlockingDeque();
        ThreadPoolForClientConfiguration configuration = createConfiguration();
        ExecutorService executorService = new ThreadPoolForClientProxy(configuration);
        Connection dtfConnection = new DtfConnectionDecorator(connection, threadLockCacheProxy, queue, executorService);
        assertNotNull(dtfConnection);
    }
    
    private ThreadPoolForClientConfiguration createConfiguration() throws NoSuchFieldException, IllegalAccessException {
        ThreadPoolForClientConfiguration result = new ThreadPoolForClientConfiguration();
        Field corePoolSize = result.getClass().getDeclaredField("corePoolSize");
        corePoolSize.setAccessible(true);
        corePoolSize.set(result, 100);
        Field maximumPoolSize = result.getClass().getDeclaredField("maximumPoolSize");
        maximumPoolSize.setAccessible(true);
        maximumPoolSize.set(result, 1000);
        Field keepAliveTime = result.getClass().getDeclaredField("keepAliveTime");
        keepAliveTime.setAccessible(true);
        keepAliveTime.set(result, 100);
        Field queueSize = result.getClass().getDeclaredField("queueSize");
        queueSize.setAccessible(true);
        queueSize.set(result, 100);
        return result;
    }
    
    private Connection createConnection() {
        Connection result = new Connection() {
            @Override
            public Statement createStatement() throws SQLException {
                return null;
            }
            
            @Override
            public PreparedStatement prepareStatement(String s) throws SQLException {
                return null;
            }

            @Override
            public CallableStatement prepareCall(String s) throws SQLException {
                return null;
            }

            @Override
            public String nativeSQL(String s) throws SQLException {
                return null;
            }

            @Override
            public void setAutoCommit(boolean b) throws SQLException {

            }

            @Override
            public boolean getAutoCommit() throws SQLException {
                return false;
            }

            @Override
            public void commit() throws SQLException {

            }

            @Override
            public void rollback() throws SQLException {

            }

            @Override
            public void close() throws SQLException {

            }

            @Override
            public boolean isClosed() throws SQLException {
                return false;
            }

            @Override
            public DatabaseMetaData getMetaData() throws SQLException {
                return null;
            }

            @Override
            public void setReadOnly(boolean b) throws SQLException {

            }

            @Override
            public boolean isReadOnly() throws SQLException {
                return false;
            }

            @Override
            public void setCatalog(String s) throws SQLException {

            }

            @Override
            public String getCatalog() throws SQLException {
                return null;
            }

            @Override
            public void setTransactionIsolation(int i) throws SQLException {

            }

            @Override
            public int getTransactionIsolation() throws SQLException {
                return 0;
            }

            @Override
            public SQLWarning getWarnings() throws SQLException {
                return null;
            }

            @Override
            public void clearWarnings() throws SQLException {

            }

            @Override
            public Statement createStatement(int i, int i1) throws SQLException {
                return null;
            }

            @Override
            public PreparedStatement prepareStatement(String s, int i, int i1) throws SQLException {
                return null;
            }

            @Override
            public CallableStatement prepareCall(String s, int i, int i1) throws SQLException {
                return null;
            }

            @Override
            public Map<String, Class<?>> getTypeMap() throws SQLException {
                return null;
            }

            @Override
            public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

            }

            @Override
            public void setHoldability(int i) throws SQLException {

            }

            @Override
            public int getHoldability() throws SQLException {
                return 0;
            }

            @Override
            public Savepoint setSavepoint() throws SQLException {
                return null;
            }

            @Override
            public Savepoint setSavepoint(String s) throws SQLException {
                return null;
            }

            @Override
            public void rollback(Savepoint savepoint) throws SQLException {

            }

            @Override
            public void releaseSavepoint(Savepoint savepoint) throws SQLException {

            }

            @Override
            public Statement createStatement(int i, int i1, int i2) throws SQLException {
                return null;
            }

            @Override
            public PreparedStatement prepareStatement(String s, int i, int i1, int i2) throws SQLException {
                return null;
            }

            @Override
            public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException {
                return null;
            }

            @Override
            public PreparedStatement prepareStatement(String s, int i) throws SQLException {
                return null;
            }

            @Override
            public PreparedStatement prepareStatement(String s, int[] ints) throws SQLException {
                return null;
            }

            @Override
            public PreparedStatement prepareStatement(String s, String[] strings) throws SQLException {
                return null;
            }

            @Override
            public Clob createClob() throws SQLException {
                return null;
            }

            @Override
            public Blob createBlob() throws SQLException {
                return null;
            }

            @Override
            public NClob createNClob() throws SQLException {
                return null;
            }

            @Override
            public SQLXML createSQLXML() throws SQLException {
                return null;
            }

            @Override
            public boolean isValid(int i) throws SQLException {
                return false;
            }

            @Override
            public void setClientInfo(String s, String s1) throws SQLClientInfoException {

            }

            @Override
            public void setClientInfo(Properties properties) throws SQLClientInfoException {

            }

            @Override
            public String getClientInfo(String s) throws SQLException {
                return null;
            }

            @Override
            public Properties getClientInfo() throws SQLException {
                return null;
            }

            @Override
            public Array createArrayOf(String s, Object[] objects) throws SQLException {
                return null;
            }

            @Override
            public Struct createStruct(String s, Object[] objects) throws SQLException {
                return null;
            }

            @Override
            public void setSchema(String s) throws SQLException {

            }

            @Override
            public String getSchema() throws SQLException {
                return null;
            }

            @Override
            public void abort(Executor executor) throws SQLException {

            }

            @Override
            public void setNetworkTimeout(Executor executor, int i) throws SQLException {

            }

            @Override
            public int getNetworkTimeout() throws SQLException {
                return 0;
            }

            @Override
            public <T> T unwrap(Class<T> aClass) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> aClass) throws SQLException {
                return false;
            }
        };
        return result;
    }
}
