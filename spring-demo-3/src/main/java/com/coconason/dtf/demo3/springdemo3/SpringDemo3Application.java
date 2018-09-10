package com.coconason.dtf.demo3.springdemo3;

import com.alibaba.druid.pool.DruidDataSource;
import com.coconason.dtf.client.core.dbconnection.DTFDataSourceProxy;
import com.coconason.dtf.client.core.dbconnection.SecondThreadsInfo;
import com.coconason.dtf.client.core.dbconnection.ThreadsInfo;
import com.coconason.dtf.client.core.nettyclient.messagequeue.TransactionMessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@SpringBootApplication
@EnableAutoConfiguration
public class SpringDemo3Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringDemo3Application.class, args);
	}

	@Autowired
	private Environment env;

	@Autowired
	private ThreadsInfo threadsInfo;

	@Autowired
	private TransactionMessageQueue queue;

	@Autowired
	private SecondThreadsInfo secondThreadsInfo;

	@Bean
	public DataSource dataSource() {
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
		DTFDataSourceProxy dataSourceProxy = new DTFDataSourceProxy();
		dataSourceProxy.setDataSource(dataSource);
		dataSourceProxy.setThreadsInfo(threadsInfo);
		dataSourceProxy.setQueue(queue);
		dataSourceProxy.setSecondThreadsInfo(secondThreadsInfo);
		return dataSourceProxy;
	}
}
