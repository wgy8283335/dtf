package com.coconason.dtf.demo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDemo2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringDemo2Application.class, args);
	}

//	@Autowired
//	private Environment env;
//
//	@Bean
//	public DataSource dataSource() {
//		DruidDataSource dataSource = new DruidDataSource();
//		dataSource.setUrl(env.getProperty("spring.datasource.url"));
//		dataSource.setUsername(env.getProperty("spring.datasource.username"));//用户名
//		dataSource.setPassword(env.getProperty("spring.datasource.password"));//密码
//		dataSource.setInitialSize(2);
//		dataSource.setMaxActive(20);
//		dataSource.setMinIdle(0);
//		dataSource.setMaxWait(60000);
//		dataSource.setValidationQuery("SELECT 1");
//		dataSource.setTestOnBorrow(false);
//		dataSource.setTestWhileIdle(true);
//		dataSource.setPoolPreparedStatements(false);
//		DTFDataSourceProxy dataSourceProxy = new DTFDataSourceProxy();
//		dataSourceProxy.setDataSource(dataSource);
//		return dataSourceProxy;
//	}
}
