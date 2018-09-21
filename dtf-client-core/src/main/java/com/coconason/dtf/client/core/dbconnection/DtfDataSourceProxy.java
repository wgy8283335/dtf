package com.coconason.dtf.client.core.dbconnection;

import com.coconason.dtf.client.core.nettyclient.messagequeue.TransactionMessageQueue;
import com.coconason.dtf.client.core.threadpools.ThreadPoolForClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @Author: Jason
 * @date: 2018/8/21-20:16
 */
//@Component
//@DependsOn("dataSourceOriginal")
public class DtfDataSourceProxy implements DataSource{
    //@Resource(type = DruidAbstractDataSource.class)
    //@Resource(name="dataSource")
    //@Autowired
    //@Qualifier("dataSourceOriginal")
    private DataSource dataSource;
    @Autowired
    private ThreadsInfo threadsInfo;
    @Autowired
    private TransactionMessageQueue queue;
    @Autowired
    private ThreadsInfo secondThreadsInfo;
    @Autowired
    private ThreadPoolForClient threadPoolForClient;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setThreadsInfo(ThreadsInfo threadsInfo) {
        this.threadsInfo = threadsInfo;
    }

    public void setQueue(TransactionMessageQueue queue) {
        this.queue = queue;
    }

    public void setSecondThreadsInfo(ThreadsInfo secondThreadsInfo) {
        this.secondThreadsInfo = secondThreadsInfo;
    }

    public void setThreadPoolForClient(ThreadPoolForClient threadPoolForClient) {
        this.threadPoolForClient = threadPoolForClient;
    }


    public DtfDataSourceProxy(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = new DtfConnection(dataSource.getConnection(),threadsInfo,queue,secondThreadsInfo,threadPoolForClient);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = new DtfConnection(dataSource.getConnection(username,password),threadsInfo,queue,secondThreadsInfo,threadPoolForClient);
        return connection;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
