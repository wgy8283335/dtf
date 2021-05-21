package com.dtf.client.core.dbconnection;

import com.dtf.client.core.thread.ThreadLockCacheProxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Dtf data source decorator.
 * Implement the DataSource interface.
 * 
 * @author wangguangyuan
 */
@Component
public final class DtfDataSourceDecorator implements DataSource {
    
    private DataSource dataSource;

    @Autowired
    private ThreadLockCacheProxy threadLockCacheProxy;
    
    @Autowired
    @Qualifier("transactionMessageQueueProxy")
    private Queue queue;
//    
//    @Autowired
//    private ThreadLockCacheProxy secondThreadLockCacheProxy;
    
    @Autowired
    @Qualifier("threadPoolForClientProxy")
    private ExecutorService threadPoolForClientProxy;
//    
//    @Autowired
//    private ThreadLockCacheProxy syncFinalCommitThreadLockCacheProxy;
    
    public DtfDataSourceDecorator(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = new DtfConnectionDecorator(dataSource.getConnection(), threadLockCacheProxy, queue, 
                threadPoolForClientProxy);
        return connection;
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        Connection connection = new DtfConnectionDecorator(dataSource.getConnection(username, password), threadLockCacheProxy, 
                queue, threadPoolForClientProxy);
        return connection;
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
        
    }
    
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        
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
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
