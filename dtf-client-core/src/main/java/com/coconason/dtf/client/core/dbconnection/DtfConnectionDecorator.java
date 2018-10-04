package com.coconason.dtf.client.core.dbconnection;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.BaseTransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.beans.TransactionType;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.utils.UuidGenerator;
import com.google.common.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.coconason.dtf.client.core.constants.Member.ORIGINAL_ID;

/**
 * @Author: Jason
 * @date: 2018/8/22-9:04
 */
public class DtfConnectionDecorator implements Connection {

    private Logger logger = LoggerFactory.getLogger(DtfConnectionDecorator.class);

    private Connection connection;

    private boolean readOnly = false;

    private boolean hasRead = false;

    private volatile DbOperationType state = DbOperationType.DEFAULT;

    private boolean hasClose = false;

    private ThreadLockCacheProxy threadLockCacheProxy;

    private Queue queue;

    private TransactionServiceInfo transactionServiceInfo;

    private Cache<String,ClientLockAndConditionInterface>  secondThreadLockCacheProxy;

    private ExecutorService threadPoolForClientProxy;

    private ThreadLockCacheProxy syncFinalCommitThreadLockCacheProxy;

    public DtfConnectionDecorator(Connection connection) {
        this.connection = connection;
    }

    public DtfConnectionDecorator(Connection connection, ThreadLockCacheProxy threadLockCacheProxy, Queue queue, ThreadLockCacheProxy secondThreadLockCacheProxy, ExecutorService threadPoolForClientProxy, ThreadLockCacheProxy syncFinalCommitThreadLockCacheProxy) {
        this.connection = connection;
        this.threadLockCacheProxy = threadLockCacheProxy;
        this.queue = queue;
        this.secondThreadLockCacheProxy = secondThreadLockCacheProxy;
        this.threadPoolForClientProxy = threadPoolForClientProxy;
        this.syncFinalCommitThreadLockCacheProxy = syncFinalCommitThreadLockCacheProxy;
    }

    @Override
    public void commit() throws SQLException {
        if(readOnly){
            connection.commit();
            hasRead = true;
            return;
        }
        logger.info("commit");
        state = DbOperationType.COMMIT;
        close();
        hasClose = true;
    }

    @Override
    public void rollback() throws SQLException {
        if(readOnly){
            connection.rollback();
            hasRead = true;
            return;
        }
        logger.info("rollback");
        state = DbOperationType.ROLLBACK;
        close();
        hasClose = true;
    }

    @Override
    public void close() throws SQLException {
        if(readOnly||hasRead){
            connection.close();
            return;
        }
        if(hasClose){
            hasClose = false;
            return;
        }
        transactionServiceInfo = TransactionServiceInfo.getCurrent();
        if(TransactionType.SYNC_FINAL==TransactionType.getCurrent()||TransactionType.SYNC_STRONG==TransactionType.getCurrent()) {
            threadPoolForClientProxy.execute(new SubmitRunnable(TransactionGroupInfo.getCurrent()));
            try {
                BaseTransactionGroupInfo transactionGroupInfo = TransactionGroupInfo.getCurrent();
                String groupId = transactionGroupInfo.getGroupId();
                Long memberId = transactionGroupInfo.getMemberId();
                if (ORIGINAL_ID.equals(memberId) && TransactionType.SYNC_STRONG==TransactionType.getCurrent()){
                    queue.add(TransactionServiceInfo.newInstanceWithGroupidSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.APPLYFORSUBMIT_STRONG,TransactionGroupInfo.getCurrent().getGroupId(),TransactionGroupInfo.getCurrent().getGroupMembers()));
                    ClientLockAndConditionInterface secondlc = new ClientLockAndCondition(new ReentrantLock(), DbOperationType.DEFAULT);
                    secondThreadLockCacheProxy.put(groupId, secondlc);
                    boolean isWholeSuccess = secondlc.await(10000,TimeUnit.MILLISECONDS);
                    if(isWholeSuccess==false){
                        ClientLockAndConditionInterface syncFinalCommitLc = syncFinalCommitThreadLockCacheProxy.getIfPresent(groupId);
                        syncFinalCommitLc.setState(DbOperationType.WHOLE_FAIL);
                        throw new Exception("Distributed transaction fail to receive WHOLE_SUCCESS_STRONG , groupId is :"+groupId);
                    }
                    ClientLockAndConditionInterface secondlc2 = secondThreadLockCacheProxy.getIfPresent(groupId);
                    if (secondlc2.getState() == DbOperationType.WHOLE_FAIL) {
                        queue.add(TransactionServiceInfo.newInstanceForShortMessage(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.WHOLE_FAIL_STRONG_ACK, groupId));
                        connection.close();
                        ClientLockAndConditionInterface syncFinalCommitLc = syncFinalCommitThreadLockCacheProxy.getIfPresent(groupId);
                        syncFinalCommitLc.setState(DbOperationType.WHOLE_FAIL);
                        throw new Exception("Distributed transaction failed and groupId:"+groupId);
                    }else{
                        queue.add(TransactionServiceInfo.newInstanceForShortMessage(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.WHOLE_SUCCESS_STRONG_ACK, groupId));
                        //4. close the connection.
                        System.out.println("dtf connection.close();");
                        connection.close();
                        ClientLockAndConditionInterface syncFinalCommitLc = syncFinalCommitThreadLockCacheProxy.getIfPresent(groupId);
                        syncFinalCommitLc.setState(DbOperationType.WHOLE_SUCCESS);
                    }
                }
                else if(ORIGINAL_ID.equals(memberId) && TransactionType.SYNC_FINAL==TransactionType.getCurrent()){
                    queue.add(TransactionServiceInfo.newInstanceWithGroupidSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.APPLYFORSUBMIT,TransactionGroupInfo.getCurrent().getGroupId(),TransactionGroupInfo.getCurrent().getGroupMembers()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                connection.close();
            }
        }else{
            connection.commit();
            connection.close();
        }
    }

    private class SubmitRunnable implements Runnable{
        private BaseTransactionGroupInfo transactionGroupInfo;

        public SubmitRunnable(BaseTransactionGroupInfo transactionGroupInfo) {
            this.transactionGroupInfo = transactionGroupInfo;
        }

        @Override
        public void run() {
            String groupId = transactionGroupInfo.getGroupId();
            Set groupMembers = transactionGroupInfo.getGroupMembers();
            Long memberId = transactionGroupInfo.getMemberId();
            try {
                //2. Use lock condition to wait for signaling.
                ClientLockAndConditionInterface lc = new ClientLockAndCondition(new ReentrantLock(),state);
                JSONObject map = transactionServiceInfo.getInfo();
                threadLockCacheProxy.put(map.get("groupId").toString()+memberId,lc);
                queue.add(transactionServiceInfo);
                System.out.println("before dtf connection add wait ---------------------------"+System.currentTimeMillis());
                boolean result = lc.await(10000, TimeUnit.MILLISECONDS);
                System.out.println("after dtf connection add wait finish ---------------------------"+System.currentTimeMillis());
                if(result == false){
                    connection.rollback();
                    connection.close();
                    throw new Exception("haven't received approve submit message");
                }
                //3. After signaling, if success commit or rollback, otherwise skip the committing.
                state = threadLockCacheProxy.getIfPresent(map.get("groupId").toString()+memberId).getState();
                if(state == DbOperationType.COMMIT){
                    //Thread.sleep(30000);
                    if(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD_STRONG) {
                        //int i = 6/0;
                        queue.add(TransactionServiceInfo.newInstanceForSub(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_SUCCESS_STRONG, groupId, groupMembers, memberId));
                    }else if(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD){
                        queue.add(TransactionServiceInfo.newInstanceForSub(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_SUCCESS, groupId, groupMembers, memberId));
                    }
                    System.out.println("提交");
                    connection.commit();
                }else if(state == DbOperationType.ROLLBACK){
                    if(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD_STRONG){
                        queue.add(TransactionServiceInfo.newInstanceWithGroupidSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL_STRONG, groupId,groupMembers));
                    }else if(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD){
                        queue.add(TransactionServiceInfo.newInstanceWithGroupidSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL, groupId,groupMembers));
                    }
                    System.out.println("回滚");
                    connection.rollback();
                }
            } catch (Exception e) {
                try {
                    connection.rollback();
                    if(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD_STRONG){
                        queue.add(TransactionServiceInfo.newInstanceWithGroupidSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL_STRONG, groupId,groupMembers));
                    }else if(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD){
                        System.out.println("sub fail ---------------------------"+System.currentTimeMillis());
                        queue.add(TransactionServiceInfo.newInstanceWithGroupidSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL, groupId,groupMembers));
                    }
                    e.printStackTrace();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } finally {
                try {
                    if(memberId!=1&&(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD_STRONG)|| transactionServiceInfo.getAction()== MessageProto.Message.ActionType.CANCEL) {
                        connection.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
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
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
        connection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
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
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType,resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql,resultSetType,resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql,resultSetType,resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
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
    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareStatement(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql,autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return connection.prepareStatement(sql,columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return connection.prepareStatement(sql,columnNames);
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
    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        connection.setClientInfo(name,value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName,elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct(typeName,attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        connection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        connection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor,milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }
}
