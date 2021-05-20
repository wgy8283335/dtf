package com.dtf.client.core.dbconnection;

import org.junit.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

public class DtfDataSourceDecoratorTest {
    
    @Test
    public void assertGetConnectionWithoutParameters() throws SQLException {
        DtfDataSourceDecorator dtfDataSourceDecorator = createDtfDataSource();
        Connection dtfConnectionDecorator = dtfDataSourceDecorator.getConnection();
        assertNotNull(dtfConnectionDecorator);
    }
    
    @Test
    public void assertGetConnectionWithParameters() throws SQLException {
        DtfDataSourceDecorator dtfDataSourceDecorator = createDtfDataSource();
        Connection dtfConnectionDecorator = dtfDataSourceDecorator.getConnection("name", "password");
        assertNotNull(dtfConnectionDecorator);
    }
    
    private DtfDataSourceDecorator createDtfDataSource() {
        
        DtfDataSourceDecorator result = new DtfDataSourceDecorator(new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return null;
            }

            @Override
            public Connection getConnection(String s, String s1) throws SQLException {
                return null;
            }

            @Override
            public <T> T unwrap(Class<T> aClass) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> aClass) throws SQLException {
                return false;
            }

            @Override
            public PrintWriter getLogWriter() throws SQLException {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter printWriter) throws SQLException {

            }

            @Override
            public void setLoginTimeout(int i) throws SQLException {

            }

            @Override
            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                return null;
            }
        });
        return result;
    }
}
