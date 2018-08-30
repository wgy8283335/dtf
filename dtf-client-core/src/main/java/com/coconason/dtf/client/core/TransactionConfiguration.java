package com.coconason.dtf.client.core;

import com.coconason.dtf.client.core.spring.http.DtfHttpResponseInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * Created by lorne on 2017/6/26.
 */

@Configuration
@ComponentScan
public class TransactionConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DtfHttpResponseInterceptor());
    }
}
