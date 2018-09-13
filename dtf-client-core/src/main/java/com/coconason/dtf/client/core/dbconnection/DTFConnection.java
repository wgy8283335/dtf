package com.coconason.dtf.client.core.dbconnection;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.beans.TransactionType;
import com.coconason.dtf.client.core.nettyclient.messagequeue.TransactionMessageQueue;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.utils.UuidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Jason
 * @date: 2018/8/22-9:04
 */
public class DTFConnection implements Connection {

    private Logger logger = LoggerFactory.getLogger(DTFConnection.class);

    private Connection connection;

    private boolean readOnly = false;

    private volatile DBOperationType state = DBOperationType.DEFAULT;

    private boolean hasClose = false;

    private ThreadsInfo threadsInfo;

    private TransactionMessageQueue queue;

    private TransactionServiceInfo transactionServiceInfo;

    private SecondThreadsInfo secondThreadsInfo;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public DTFConnection(Connection connection) {
        this.connection = connection;
    }

    public DTFConnection(Connection connection,ThreadsInfo threadsInfo,TransactionMessageQueue queue,SecondThreadsInfo secondThreadsInfo) {
        this.connection = connection;
        this.threadsInfo = threadsInfo;
        this.queue = queue;
        this.secondThreadsInfo = secondThreadsInfo;
    }

    @Override
    public void commit() throws SQLException {
        if(readOnly){
            connection.commit();
            return;
        }
        logger.info("commit");
        state = DBOperationType.COMMIT;
        close();
        hasClose = true;
    }

    @Override
    public void rollback() throws SQLException {
        if(readOnly){
            connection.rollback();
            return;
        }
        logger.info("rollback");
        state = DBOperationType.ROLLBACK;
        close();
        hasClose = true;
    }

    @Override
    public void close() throws SQLException {
        if(readOnly){
            connection.close();
            return;
        }
        if(hasClose){
            hasClose = false;
            return;
        }
        transactionServiceInfo = TransactionServiceInfo.getCurrent();
        if("SYNC_FINAL".equals(TransactionType.getCurrent().getTransactionType())||"SYNC_STRONG".equals(TransactionType.getCurrent().getTransactionType())) {
            Thread thread = new Thread(new SubmitRunnable(TransactionGroupInfo.getCurrent()));
            thread.start();
            try {
                TransactionGroupInfo transactionGroupInfo = TransactionGroupInfo.getCurrent();
                String groupId = transactionGroupInfo.getGroupId();
                Long memberId = transactionGroupInfo.getMemberId();
                if (memberId == 1 && "SYNC_STRONG".equals(TransactionType.getCurrent().getTransactionType())) {
                    LockAndCondition secondlc = new LockAndCondition(new ReentrantLock(), DBOperationType.DEFAULT);
                    secondThreadsInfo.put(groupId, secondlc);
                    secondlc.await();
                    LockAndCondition secondlc2 = secondThreadsInfo.get(groupId);
                    if (secondlc2.getState() == DBOperationType.WHOLEFAIL) {
                        throw new Exception("Distributed transaction failed");
                    }
                    System.out.println("DTFConenction finished---------------------------------");
                    //4. close the connection.
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            connection.commit();
            connection.close();
        }
    }

    private class SubmitRunnable implements Runnable{
        private TransactionGroupInfo transactionGroupInfo;

        public SubmitRunnable(TransactionGroupInfo transactionGroupInfo) {
            this.transactionGroupInfo = transactionGroupInfo;
        }

        @Override
        public void run() {
            String groupId = transactionGroupInfo.getGroupId();
            Set groupMembers = transactionGroupInfo.getGroupMembers();
            Long memberId = transactionGroupInfo.getMemberId();
            try {
                //2. Use lock condition to wait for signaling.
                LockAndCondition lc = new LockAndCondition(new ReentrantLock(),state);
                JSONObject map = transactionServiceInfo.getInfo();
                threadsInfo.put(map.get("groupId").toString()+memberId,lc);
                System.out.println("Thread.currentThread().getName()--------------"+Thread.currentThread().getName());
                queue.put(transactionServiceInfo);
                System.out.println("transactionServiceInfo action is -------------"+transactionServiceInfo.getAction());
                lc.await();
                //3. After signaling, if success commit or rollback, otherwise skip the committing.
                System.out.println("Thread.currentThread().getName()--------------"+Thread.currentThread().getName());
                System.out.println("继续执行--------------");
                state = threadsInfo.get(map.get("groupId").toString()+memberId).getState();
                if(state == DBOperationType.COMMIT){
                    System.out.println("提交");
                    connection.commit();
                    if(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD_STRONG){
                        queue.put(TransactionServiceInfo.newInstanceForSubSccuessStrong(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_SUCCESS_STRONG, groupId,groupMembers,memberId));
                    }
                }else if(state == DBOperationType.ROLLBACK){
                    System.out.println("回滚");
                    connection.rollback();
                    if(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD_STRONG){
                        queue.put(TransactionServiceInfo.newInstanceWithGroupidSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL_STRONG, groupId,groupMembers));
                    }
                }
            } catch (Exception e) {
                try {
                    connection.rollback();
                    if(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD_STRONG){
                        queue.put(TransactionServiceInfo.newInstanceWithGroupidSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.SUB_FAIL_STRONG, groupId,groupMembers));
                    }
                    e.printStackTrace();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } finally {
                try {
                    if(memberId!=1&&(transactionServiceInfo.getAction()== MessageProto.Message.ActionType.ADD_STRONG)||transactionServiceInfo.getAction()== MessageProto.Message.ActionType.CANCEL) {
                        System.out.println("DTFConenction finished---------------------------------");
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
