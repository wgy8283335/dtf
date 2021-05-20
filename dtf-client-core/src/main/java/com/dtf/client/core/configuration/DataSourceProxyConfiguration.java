package com.dtf.client.core.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.dtf.client.core.dbconnection.DtfDataSourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Data source proxy configuration.
 * 
 * @Author: wangguangyuan
 */
@Configuration
@ComponentScan
public class DataSourceProxyConfiguration {
    
    @Autowired
    private Environment env;
    
    /**
     * Create dtf data source.
     * 
     * @return data source
     */
    @Bean
    public DataSource dataSource() {
        String type = env.getProperty("spring.datasource.type");
        switch (type) {
            case "com.alibaba.druid.pool.DruidDataSource":
                DruidDataSource dataSource = new DruidDataSource();
                dataSource.setUrl(env.getProperty("spring.datasource.url"));
                dataSource.setUsername(env.getProperty("spring.datasource.username"));
                dataSource.setPassword(env.getProperty("spring.datasource.password"));
                dataSource.setInitialSize(2);
                dataSource.setMaxActive(20);
                dataSource.setMinIdle(0);
                dataSource.setMaxWait(60000);
                dataSource.setValidationQuery("SELECT 1");
                dataSource.setTestOnBorrow(false);
                dataSource.setTestWhileIdle(true);
                dataSource.setPoolPreparedStatements(false);
                dataSource.setDefaultAutoCommit(false);
                return new DtfDataSourceDecorator(dataSource);
            default:
                return null;
        }
    }
    
}
