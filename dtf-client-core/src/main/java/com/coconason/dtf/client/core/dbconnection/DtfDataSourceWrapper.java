package com.coconason.dtf.client.core.dbconnection;

import com.coconason.dtf.client.core.threadpools.ThreadPoolForClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * @Author: Jason
 * @date: 2018/8/21-20:16
 */

public class DtfDataSourceWrapper implements DataSource{
    private DataSource dataSource;
    @Autowired
    private ThreadLockCacheProxy threadLockCacheProxy;
    @Autowired
    @Qualifier("transactionMessageQueueProxy")
    private Queue queue;
    @Autowired
    private ThreadLockCacheProxy secondThreadLockCacheProxy;
    @Autowired
    private ThreadPoolForClient threadPoolForClient;
    @Autowired
    private ThreadLockCacheProxy syncFinalCommitThreadLockCacheProxy;

    public DtfDataSourceWrapper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = new DtfConnectionWrapper(dataSource.getConnection(), threadLockCacheProxy,queue, secondThreadLockCacheProxy,threadPoolForClient, syncFinalCommitThreadLockCacheProxy);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = new DtfConnectionWrapper(dataSource.getConnection(username,password), threadLockCacheProxy,queue, secondThreadLockCacheProxy,threadPoolForClient, syncFinalCommitThreadLockCacheProxy);
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
