package com.coconason.dtf.client.core.dbconnection;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.group.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.coconason.dtf.client.core.beans.service.TransactionServiceInfo;
import com.coconason.dtf.client.core.beans.type.TransactionType;
import com.coconason.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.coconason.dtf.client.core.thread.ThreadLockCacheProxy;
import com.coconason.dtf.client.core.threadpools.ThreadPoolForClientConfiguration;
import com.coconason.dtf.client.core.threadpools.ThreadPoolForClientProxy;
import com.coconason.dtf.common.protobuf.MessageProto;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

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
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DtfConnectionDecoratorTest {
    
    @Test
    public void assertConstructorWithOneParameter() {
        Connection dtfConnection = new DtfConnectionDecorator(mock(Connection.class));
        assertNotNull(dtfConnection);
    }
    
    @Test
    public void assertConstructorWithMultiParameters() throws NoSuchFieldException, IllegalAccessException {
        Connection dtfConnection = createDtfConnectionDecorator();
        assertNotNull(dtfConnection);
    }
    
    @Test
    public void assertCommit() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        dtfConnection.commit();
        OperationType state = getState(dtfConnection);
        assertEquals(OperationType.COMMIT, state);
    }
    
    @Test
    public void assertCommitWithReadOnly() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        setReadOnlyWithTrue(dtfConnection);
        dtfConnection.commit();
        OperationType state = getState(dtfConnection);
        boolean hasRead = getHasRead(dtfConnection);
        assertEquals(OperationType.DEFAULT, state);
        assertEquals(true, hasRead);
    }

    @Test
    public void assertRollback() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        dtfConnection.rollback();
        OperationType state = getState(dtfConnection);
        assertEquals(OperationType.ROLLBACK, state);
    }
    
    @Test
    public void assertRollbackWithReadOnly() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        setReadOnlyWithTrue(dtfConnection);
        dtfConnection.rollback();
        OperationType state = getState(dtfConnection);
        boolean hasRead = getHasRead(dtfConnection);
        assertEquals(OperationType.DEFAULT, state);
        assertEquals(true, hasRead);
    }
    
    @Test
    public void assertCloseWithReadOnly() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        setReadOnlyWithTrue(dtfConnection);
        dtfConnection.close();
        OperationType state = getState(dtfConnection);
        boolean hasRead = getHasRead(dtfConnection);
        assertEquals(OperationType.DEFAULT, state);
        assertEquals(false, hasRead);
    }
    
    @Test
    public void assertCloseWithRollback() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        dtfConnection.rollback();
        dtfConnection.close();
        OperationType state = getState(dtfConnection);
        boolean hasRead = getHasRead(dtfConnection);
        assertEquals(OperationType.ROLLBACK, state);
        assertEquals(false, hasRead);
    }
    
    @Test
    public void assertCloseWithHasRead() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        setReadOnlyWithTrue(dtfConnection);
        dtfConnection.commit();
        dtfConnection.close();
        OperationType state = getState(dtfConnection);
        boolean hasRead = getHasRead(dtfConnection);
        assertEquals(OperationType.DEFAULT, state);
        assertEquals(true, hasRead);
    }
    
    @Test
    public void assertCloseWithoutTransactionServiceInfo() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        dtfConnection.close();
        OperationType state = getState(dtfConnection);
        boolean hasRead = getHasRead(dtfConnection);
        assertEquals(OperationType.DEFAULT, state);
        assertEquals(false, hasRead);
    }
    
    @Test
    public void assertCloseWithAsyncFinal() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        BaseTransactionServiceInfo.setCurrent(new TransactionServiceInfo("1", new JSONObject(), MessageProto.Message.ActionType.ADD));
        TransactionType.setCurrent(TransactionType.ASYNC_FINAL);
        dtfConnection.close();
        OperationType state = getState(dtfConnection);
        boolean hasRead = getHasRead(dtfConnection);
        assertEquals(OperationType.DEFAULT, state);
        assertEquals(false, hasRead);
    }
    
    @Test
    public void assertCloseWithSyncWhenTimeout() throws NoSuchFieldException, IllegalAccessException, SQLException {
        Connection dtfConnection = createDtfConnectionDecorator();
        BaseTransactionServiceInfo.setCurrent(new TransactionServiceInfo("1", new JSONObject(), MessageProto.Message.ActionType.ADD));
        dtfConnection.close();
        OperationType state = getState(dtfConnection);
        boolean hasRead = getHasRead(dtfConnection);
        assertEquals(OperationType.DEFAULT, state);
        assertEquals(false, hasRead);
    }
    
    @Test
    public void assert1CloseWithSyncFinalWhenCommit() throws NoSuchFieldException, IllegalAccessException, SQLException, InterruptedException {
        setEnv(MessageProto.Message.ActionType.ADD);
        Connection dtfConnection = createDtfConnectionDecorator();
        setState(dtfConnection, OperationType.COMMIT);
        dtfConnection.close();
        sendSignalLc(dtfConnection, "177211");
        TransactionServiceInfo transactionServiceInfo1 = getTransactionServiceInfoFromQueue(dtfConnection);
        TransactionServiceInfo transactionServiceInfo2 = getTransactionServiceInfoFromQueue(dtfConnection);
        unSetEnv();
        assertEquals(MessageProto.Message.ActionType.SUB_SUCCESS, transactionServiceInfo2.getAction());
        assertEquals(MessageProto.Message.ActionType.ADD, transactionServiceInfo1.getAction());
        assertEquals("1", transactionServiceInfo1.getId());
    }
    
    @Test
    public void assert2CloseWithSyncStrongWhenCommit() throws NoSuchFieldException, IllegalAccessException, SQLException, InterruptedException {
        setEnv(MessageProto.Message.ActionType.ADD_STRONG);
        Connection dtfConnection = createDtfConnectionDecorator();
        setState(dtfConnection, OperationType.COMMIT);
        dtfConnection.close();
        sendSignalLc(dtfConnection, "177211");
        TransactionServiceInfo transactionServiceInfo = getTransactionServiceInfoFromQueue(dtfConnection);
        TransactionServiceInfo transactionServiceInfo2 = getTransactionServiceInfoFromQueue(dtfConnection);
        unSetEnv();
        assertEquals(MessageProto.Message.ActionType.SUB_SUCCESS_STRONG, transactionServiceInfo2.getAction());
        assertEquals(MessageProto.Message.ActionType.ADD_STRONG, transactionServiceInfo.getAction());
        assertEquals("1", transactionServiceInfo.getId());
    }
    
    @Test
    public void assert3CloseWithSyncFinalWhenCommitFail() throws NoSuchFieldException, IllegalAccessException, SQLException, InterruptedException {
        setEnv(MessageProto.Message.ActionType.ADD);
        Connection dtfConnection = createBadDtfConnectionDecorator();
        setState(dtfConnection, OperationType.COMMIT);
        dtfConnection.close();
        sendSignalLc(dtfConnection, "177211");
        TransactionServiceInfo transactionServiceInfo1 = getTransactionServiceInfoFromQueue(dtfConnection);
        TransactionServiceInfo transactionServiceInfo2 = getTransactionServiceInfoFromQueue(dtfConnection);
        unSetEnv();        
        assertEquals(MessageProto.Message.ActionType.SUB_FAIL, transactionServiceInfo2.getAction());
        assertEquals(MessageProto.Message.ActionType.ADD, transactionServiceInfo1.getAction());
        assertEquals("1", transactionServiceInfo1.getId());
    }
    
    @Test
    public void assert4CloseWithSyncStrongWhenCommitFail() throws NoSuchFieldException, IllegalAccessException, SQLException, InterruptedException {
        setEnv(MessageProto.Message.ActionType.ADD);
        Connection dtfConnection = createBadDtfConnectionDecorator();
        setState(dtfConnection, OperationType.COMMIT);
        dtfConnection.close();
        sendSignalLc(dtfConnection, "177211");
        TransactionServiceInfo transactionServiceInfo1 = getTransactionServiceInfoFromQueue(dtfConnection);
        TransactionServiceInfo transactionServiceInfo2 = getTransactionServiceInfoFromQueue(dtfConnection);
        unSetEnv();
        assertEquals(MessageProto.Message.ActionType.SUB_FAIL, transactionServiceInfo2.getAction());
        assertEquals(MessageProto.Message.ActionType.ADD, transactionServiceInfo1.getAction());
        assertEquals("1", transactionServiceInfo1.getId());
    }
    
    private void setEnv(MessageProto.Message.ActionType type) {
        JSONObject map = new JSONObject();
        map.put("groupId", "17721");
        BaseTransactionServiceInfo.setCurrent(new TransactionServiceInfo("1", map, type));
        TransactionGroupInfo.setCurrent(new TransactionGroupInfo("17721",1L, new HashSet<Long>()));
    }
    
    private void unSetEnv() {
        BaseTransactionServiceInfo.setCurrent(null);
        TransactionGroupInfo.setCurrent(null);
    }
    
    private void sendSignalLc(Connection connection, String id) throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Thread.sleep(200);
        Field field = connection.getClass().getDeclaredField("threadLockCacheProxy");
        field.setAccessible(true);
        ThreadLockCacheProxy proxy = (ThreadLockCacheProxy) field.get(connection);
        ClientLockAndConditionInterface lc = proxy.getIfPresent(id);
        lc.signal();
    }
    
    private TransactionServiceInfo getTransactionServiceInfoFromQueue(Connection connection) throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Thread.sleep(400);
        Field field = connection.getClass().getDeclaredField("queue");
        field.setAccessible(true);
        Queue queue = (Queue) field.get(connection);
        TransactionServiceInfo result = (TransactionServiceInfo) queue.poll();
        return result;
    }
    
    private boolean getHasRead(Connection connection) throws NoSuchFieldException, IllegalAccessException {
        Field field = connection.getClass().getDeclaredField("hasRead");
        field.setAccessible(true);
        boolean value = (boolean) field.get(connection);
        return value;
    }
    
    private void setReadOnlyWithTrue(Connection connection) throws NoSuchFieldException, IllegalAccessException {
        Field field = connection.getClass().getDeclaredField("readOnly");
        field.setAccessible(true);
        field.set(connection, true);
    }
    
    private void setState(Connection connection, OperationType type) throws NoSuchFieldException, IllegalAccessException {
        Field field = connection.getClass().getDeclaredField("state");
        field.setAccessible(true);
        field.set(connection, type);
    }
    
    private OperationType getState(Connection connection) throws NoSuchFieldException, IllegalAccessException {
        Field field = connection.getClass().getDeclaredField("state");
        field.setAccessible(true);
        OperationType value = (OperationType) field.get(connection);
        return value;
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
    
    private Connection createDtfConnectionDecorator() throws NoSuchFieldException, IllegalAccessException {
        Connection connection = createConnection();
        ThreadLockCacheProxy threadLockCacheProxy = new ThreadLockCacheProxy();
        Queue queue = new LinkedBlockingDeque();
        ThreadPoolForClientConfiguration configuration = createConfiguration();
        ExecutorService executorService = new ThreadPoolForClientProxy(configuration);
        Connection dtfConnection = new DtfConnectionDecorator(connection, threadLockCacheProxy, queue, executorService);
        return dtfConnection;
    }
    
    private Connection createBadDtfConnectionDecorator() throws NoSuchFieldException, IllegalAccessException {
        Connection connection = createBadConnection();
        ThreadLockCacheProxy threadLockCacheProxy = new ThreadLockCacheProxy();
        Queue queue = new LinkedBlockingDeque();
        ThreadPoolForClientConfiguration configuration = createConfiguration();
        ExecutorService executorService = new ThreadPoolForClientProxy(configuration);
        Connection dtfConnection = new DtfConnectionDecorator(connection, threadLockCacheProxy, queue, executorService);
        return dtfConnection;
    }
    
    private Connection createConnection() {
        Connection result = new DefaultConnection();
        return result;
    }
    
    private Connection createBadConnection() {
        Connection result = new BadConnection();
        return result;
    }
    
    private class BadConnection extends DefaultConnection {
        
        @Override
        public void commit() throws SQLException {
            throw new SQLException();
        }
    }
    
    private class DefaultConnection implements Connection {
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
    }
}
