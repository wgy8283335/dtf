package com.coconason.dtf.client.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.coconason.dtf.client.core.dbconnection.DtfDataSourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * @Author: Jason
 * @date: 2018/8/21-7:26
 */
@Configuration
@ComponentScan
public class DataSourceProxyConfiguration{

    @Autowired
    private Environment env;

	@Bean
	public DataSource dataSource() {
		String type = env.getProperty("spring.datasource.type");
		switch (type){
			case "com.alibaba.druid.pool.DruidDataSource":
				DruidDataSource dataSource = new DruidDataSource();
				dataSource.setUrl(env.getProperty("spring.datasource.url"));
				dataSource.setUsername(env.getProperty("spring.datasource.username"));//用户名
				dataSource.setPassword(env.getProperty("spring.datasource.password"));//密码
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
