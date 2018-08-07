package com.coconason.dtf.client.core.spring.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Author: Jason
 * @date: 2018/7/24-9:07
 */
@Aspect
@Component
public class DtfClientInterceptor {

    @Around("@annotation(com.coconason.dtf.client.core.annotation.DtfTransaction)")
    public Object dtfTransactionExecute(ProceedingJoinPoint joinPoint)throws Throwable{
        System.out.println("Start to proceed Aspect");
        new Client("localhost", 8848).start();
        Object result = joinPoint.proceed();
        System.out.println("End to proceed Aspect");
        return result;
    }
}
