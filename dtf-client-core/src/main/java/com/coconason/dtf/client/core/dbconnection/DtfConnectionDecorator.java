package com.coconason.dtf.client.core.dbconnection;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.BaseTransactionGroupInfo;
import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionServiceInfoFactory;
import com.coconason.dtf.client.core.beans.TransactionType;
import com.coconason.dtf.client.core.thread.ClientLockAndCondition;
import com.coconason.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.coconason.dtf.client.core.thread.ThreadLockCacheProxy;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.utils.UuidGenerator;
import com.google.common.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.coconason.dtf.client.core.constants.Member;

/**
 * Dtf connection decorator.
 * Implement the java.sql.connection interface.
 * 
 * @Author: Jason
 */
public final class DtfConnectionDecorator implements Connection {
    
    /**
     * Logger for DtfConnectionDecorator.
     */
    private Logger logger = LoggerFactory.getLogger(DtfConnectionDecorator.class);
    
    /**
     * Database connection.
     */
    private Connection connection;
    
    /**
     * Transaction operation type. Use volatile to keep state visible between different threads.
     */
    private volatile OperationType state = OperationType.DEFAULT;
    
    /**
     * Cache for thread lock.
     */
    private ThreadLockCacheProxy threadLockCacheProxy;
    
    /**
     * Queue which store TransactionServiceInfo object.
     */
    private Queue queue;
    
    /**
     * Transaction service info.
     */
    private BaseTransactionServiceInfo transactionServiceInfo;
    
    /**
     * Cache for thread lock.
     */
    private Cache<String, ClientLockAndConditionInterface> secondThreadLockCacheProxy;
    
    /**
     * Thread pool to execute transaction operations.
     */
    private ExecutorService threadPoolForClientProxy;
    
    /**
     * Cache for thread lock. Used as synchronous final commit.
     */
    private ThreadLockCacheProxy syncFinalCommitThreadLockCacheProxy;
    
    private boolean readOnly;
    
    private boolean hasRead;
    
    private boolean hasClose;
    
    public DtfConnectionDecorator(final Connection connection) {
        this.connection = connection;
        this.readOnly = false;
        this.hasRead = false;
        this.hasClose = false;
    }
    
    public DtfConnectionDecorator(final Connection connection, final ThreadLockCacheProxy threadLockCacheProxy, final Queue queue, final ThreadLockCacheProxy secondThreadLockCacheProxy,
                                  final ExecutorService threadPoolForClientProxy, final ThreadLockCacheProxy syncFinalCommitThreadLockCacheProxy) {
        this.connection = connection;
        this.threadLockCacheProxy = threadLockCacheProxy;
        this.queue = queue;
        this.secondThreadLockCacheProxy = secondThreadLockCacheProxy;
        this.threadPoolForClientProxy = threadPoolForClientProxy;
        this.syncFinalCommitThreadLockCacheProxy = syncFinalCommitThreadLockCacheProxy;
        this.readOnly = false;
        this.hasRead = false;
        this.hasClose = false;
    }
    
    /**
     * Commit transaction.
     * Only read only transaction could be committed by database connection directly.
     * Otherwise, commit will be handled by close(). Close() method is also a over written method.
     */
    @Override
    public void commit() throws SQLException {
        if (readOnly) {
            connection.commit();
            hasRead = true;
            return;
        }
        logger.info("commit");
        state = OperationType.COMMIT;
        close();
        hasClose = true;
    }
    
    /**
     * Roll back transaction.
     * Only read only transaction could be rolled back by database connection directly.
     * Otherwise, commit will be rolled back by close(). Close() method is also a over written method.
     */
    @Override
    public void rollback() throws SQLException {
        if (readOnly) {
            connection.rollback();
            hasRead = true;
            return;
        }
        logger.info("rollback");
        state = OperationType.ROLLBACK;
        close();
        hasClose = true;
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }
    
    /**
     * Close the connection.
     * The close() method is a over written method. Main process of the dtf transaction is described at here.
     */
    @Override
    public void close() throws SQLException {
        if (readOnly || hasRead) {
            connection.close();
            return;
        }
        if (hasClose) {
            hasClose = false;
            return;
        }
        transactionServiceInfo = BaseTransactionServiceInfo.getCurrent();
        if(null == transactionServiceInfo){
            connection.commit();
            connection.close();
            return;
        }
        if (TransactionType.ASYNC_FINAL == TransactionType.getCurrent()) {
            if (OperationType.COMMIT == state) {
                connection.commit();
            }
            connection.close();
            return;
        }
        threadPoolForClientProxy.execute(new SubmitRunnable(TransactionGroupInfo.getCurrent(),transactionServiceInfo));
        BaseTransactionGroupInfo transactionGroupInfo = TransactionGroupInfo.getCurrent();
        String groupId = transactionGroupInfo.getGroupId();
        Long memberId = transactionGroupInfo.getMemberId();
        process(memberId, groupId);
    }
    
    private void process(final Long memberId, final String groupId) throws SQLException {
        if (Member.ORIGINAL_ID.equals(memberId) && TransactionType.SYNC_FINAL == TransactionType.getCurrent()) {
//            queue.add(TransactionServiceInfoFactory.newInstanceWithGroupIdSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.APPLYFORSUBMIT,
//                    TransactionGroupInfo.getCurrent().getGroupId(), TransactionGroupInfo.getCurrent().getGroupMembers()));
//            return;
        } else if (Member.ORIGINAL_ID.equals(memberId) && TransactionType.SYNC_STRONG == TransactionType.getCurrent()) {
//            queue.add(TransactionServiceInfoFactory.newInstanceWithGroupIdSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.APPLYFORSUBMIT_STRONG,
//                    TransactionGroupInfo.getCurrent().getGroupId(), TransactionGroupInfo.getCurrent().getGroupMembers()));
            ClientLockAndConditionInterface secondlc = new ClientLockAndCondition(new ReentrantLock(), OperationType.DEFAULT);
            secondThreadLockCacheProxy.put(groupId, secondlc);
            boolean isWholeSuccess = secondlc.await(10000, TimeUnit.MILLISECONDS);
            if (!isWholeSuccess) {
                connection.close();
                ClientLockAndConditionInterface syncFinalCommitLc = syncFinalCommitThreadLockCacheProxy.getIfPresent(groupId);
                syncFinalCommitLc.setState(OperationType.WHOLE_FAIL);
                throw new SQLException("Distributed transaction fail to receive WHOLE_SUCCESS_STRONG , groupId is :" + groupId);
            }
            ClientLockAndConditionInterface secondlc2 = secondThreadLockCacheProxy.getIfPresent(groupId);
            if (secondlc2.getState() == OperationType.WHOLE_FAIL) {
                queue.add(TransactionServiceInfoFactory.newInstanceForShortMessage(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.WHOLE_FAIL_STRONG_ACK, groupId));
                connection.close();
                ClientLockAndConditionInterface syncFinalCommitLc = syncFinalCommitThreadLockCacheProxy.getIfPresent(groupId);
                syncFinalCommitLc.setState(OperationType.WHOLE_FAIL);
                throw new SQLException("Distributed transaction failed and groupId:" + groupId);
            } else {
                queue.add(TransactionServiceInfoFactory.newInstanceForShortMessage(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.WHOLE_SUCCESS_STRONG_ACK, groupId));
                connection.close();
                ClientLockAndConditionInterface syncFinalCommitLc = syncFinalCommitThreadLockCacheProxy.getIfPresent(groupId);
                syncFinalCommitLc.setState(OperationType.WHOLE_SUCCESS);
                return;
            }
        }
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
        connection.setReadOnly(readOnly);
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }
    
    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        return connection.setSavepoint(name);
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(final String s) throws SQLException {
        return connection.prepareStatement(s);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return connection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return connection.prepareStatement(sql, columnNames);
    }
    
    @Override
    public CallableStatement prepareCall(final String s) throws SQLException {
        return null;
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return connection.isValid(timeout);
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return connection.getClientInfo(name);
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
        connection.setSchema(schema);
    }
    
    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        connection.abort(executor);
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }
    
    /**
     * Main process of the dtf transaction is described at here.
     */
    private class SubmitRunnable implements Runnable {
        
        private BaseTransactionGroupInfo transactionGroupInfo;
        
        private BaseTransactionServiceInfo transactionServiceInfo;
        
        SubmitRunnable(final BaseTransactionGroupInfo transactionGroupInfo,final BaseTransactionServiceInfo transactionServiceInfo) {
            this.transactionGroupInfo = transactionGroupInfo;
            this.transactionServiceInfo = transactionServiceInfo;
        }
        
        /**
         * 1.Get groupId,groupMembers,memberId from transactionGroupInfo.TransactionGroupInfo is created by AspectHandler.
         * 2.Put lc in the threadLockCacheProxy, and put transactionServiceInfo in the queue to send.
         * 3.Netty channel receive signal, and set lc.state by SignalStrategyContext.
         * 4.If lc time out, then transactionWhenTimeout().
         * 5.Else if state get from the threadLockCacheProxy is OperationType.COMMIT or OperationType.ROLLBACK,then transactionWhenCommitOrRollback().
         * 6.Else if action type is ADD_STRONG or CANCEL, then transactionWhenAddStringOrCancel().
         */
        @Override
        public void run() {
            String groupId = transactionGroupInfo.getGroupId();
            Set groupMembers = transactionGroupInfo.getGroupMembers();
            Long memberId = transactionGroupInfo.getMemberId();
            ClientLockAndConditionInterface lc = new ClientLockAndCondition(new ReentrantLock(), state);
            JSONObject map = transactionServiceInfo.getInfo();
            threadLockCacheProxy.put(map.get("groupId").toString() + memberId, lc);
            queue.add(transactionServiceInfo);
            boolean result = lc.await(10000, TimeUnit.MILLISECONDS);
            if (transactionWhenTimeout(result, groupId, groupMembers)) {
                return;
            }
            if (transactionWhenCommitOrRollback(map, memberId, groupId, groupMembers)) {
                return;
            }
            if (transactionWhenAddStringOrCancel(memberId)) {
                return;
            }
        }
        
        private boolean transactionWhenTimeout(final boolean result, final String groupId, final Set groupMembers) {
            if (!result) {
                try {
                    connection.rollback();
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
                addNewTransactionServicetoQueueWhenFalse(groupId, groupMembers);
                return true;
            }
            return false;
        }
        
        private boolean transactionWhenCommitOrRollback(final JSONObject map, final Long memberId, final String groupId, final Set groupMembers) {
            state = threadLockCacheProxy.getIfPresent(map.get("groupId").toString() + memberId).getState();
            if (state == OperationType.COMMIT) {
                logger.debug("commit");
                try {
                    connection.commit();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
                addNewTransactionServicetoQueueWhenCommit(groupId, groupMembers, memberId);
                return true;
            } else if (state == OperationType.ROLLBACK) {
                logger.debug("rollback");
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
                addNewTransactionServicetoQueueWhenRollback(groupId, groupMembers);
                return true;
            }
            return false;
        }
        
        private boolean transactionWhenAddStringOrCancel(final Long memberId) {
            if ((memberId != 1) && (transactionServiceInfo.getAction() == MessageProto.Message.ActionType.ADD_STRONG) || transactionServiceInfo.getAction() == MessageProto.Message.ActionType.CANCEL) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
                return true;
            }
            return false;
        }
        
        private void addNewTransactionServicetoQueueWhenFalse(final String groupId, final Set groupMembers) {
            if (transactionServiceInfo.getAction() == MessageProto.Message.ActionType.ADD_STRONG) {
                queue.add(TransactionServiceInfoFactory.newInstanceWithGroupIdSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL_STRONG, groupId, groupMembers));
            } else if (transactionServiceInfo.getAction() == MessageProto.Message.ActionType.ADD) {
                queue.add(TransactionServiceInfoFactory.newInstanceWithGroupIdSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL, groupId, groupMembers));
            }
        }
        
        private void addNewTransactionServicetoQueueWhenCommit(final String groupId, final Set groupMembers, final Long memberId) {
            if (transactionServiceInfo.getAction() == MessageProto.Message.ActionType.ADD_STRONG) {
                queue.add(TransactionServiceInfoFactory.newInstanceForSub(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_SUCCESS_STRONG, groupId, groupMembers, memberId));
            } else if (transactionServiceInfo.getAction() == MessageProto.Message.ActionType.ADD) {
                queue.add(TransactionServiceInfoFactory.newInstanceForSub(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_SUCCESS, groupId, groupMembers, memberId));
            }
        }
        
        private void addNewTransactionServicetoQueueWhenRollback(final String groupId, final Set groupMembers) {
            if (transactionServiceInfo.getAction() == MessageProto.Message.ActionType.ADD_STRONG) {
                queue.add(TransactionServiceInfoFactory.newInstanceWithGroupIdSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL_STRONG, groupId, groupMembers));
            } else if (transactionServiceInfo.getAction() == MessageProto.Message.ActionType.ADD) {
                queue.add(TransactionServiceInfoFactory.newInstanceWithGroupIdSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL, groupId, groupMembers));
            }
        }
    }
    
}
