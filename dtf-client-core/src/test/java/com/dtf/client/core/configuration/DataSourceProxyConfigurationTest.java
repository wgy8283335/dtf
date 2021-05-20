package com.dtf.client.core.configuration;

import org.junit.Test;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataSourceProxyConfigurationTest {
    
    @Test
    public void testDataSource() throws NoSuchFieldException, IllegalAccessException {
        DataSourceProxyConfiguration configuration = createDataSourceProxyConfiguration();
        DataSource dataSource = configuration.dataSource();
        assertNotNull(dataSource);
    }
    
    private DataSourceProxyConfiguration createDataSourceProxyConfiguration() throws NoSuchFieldException, IllegalAccessException {
        DataSourceProxyConfiguration result = new DataSourceProxyConfiguration();
        Field envField = result.getClass().getDeclaredField("env");
        envField.setAccessible(true);
        Environment env = mock(Environment.class);
        when(env.getProperty("spring.datasource.type")).thenReturn("com.alibaba.druid.pool.DruidDataSource");
        when(env.getProperty("spring.datasource.url")).thenReturn("http://localhost");
        when(env.getProperty("spring.datasource.username")).thenReturn("admin");
        when(env.getProperty("spring.datasource.password")).thenReturn("123456");
        envField.set(result, env);
        return result;
    }
    
}
