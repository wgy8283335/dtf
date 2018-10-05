package com.coconason.dtf.client.core.dbconnection;

import com.coconason.dtf.client.core.thread.ThreadLockCacheProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * @Author: Jason
 * @date: 2018/8/21-20:16
 */

public final class DtfDataSourceDecorator implements DataSource{
    private DataSource dataSource;
    @Autowired
    private ThreadLockCacheProxy threadLockCacheProxy;
    @Autowired
    @Qualifier("transactionMessageQueueProxy")
    private Queue queue;
    @Autowired
    private ThreadLockCacheProxy secondThreadLockCacheProxy;
    @Autowired
    @Qualifier("threadPoolForClientProxy")
    private ExecutorService threadPoolForClientProxy;
    @Autowired
    private ThreadLockCacheProxy syncFinalCommitThreadLockCacheProxy;

    public DtfDataSourceDecorator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = new DtfConnectionDecorator(dataSource.getConnection(), threadLockCacheProxy,queue, secondThreadLockCacheProxy, threadPoolForClientProxy, syncFinalCommitThreadLockCacheProxy);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = new DtfConnectionDecorator(dataSource.getConnection(username,password), threadLockCacheProxy,queue, secondThreadLockCacheProxy, threadPoolForClientProxy, syncFinalCommitThreadLockCacheProxy);
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
