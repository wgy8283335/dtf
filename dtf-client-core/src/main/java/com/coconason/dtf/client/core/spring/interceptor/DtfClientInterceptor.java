package com.coconason.dtf.client.core.spring.interceptor;

import com.coconason.dtf.client.core.beans.TransactionRollback;
import com.coconason.dtf.client.core.transaction.AspectHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Jason
 * @date: 2018/7/24-9:07
 */
@Aspect
@Component
public class DtfClientInterceptor {

    @Autowired
    private AspectHandler aspectHandler;

    @Around("@annotation(com.coconason.dtf.client.core.annotation.DtfTransaction)")
    public Object dtfTransactionExecute(ProceedingJoinPoint joinPoint)throws Throwable{
//        System.out.println("Start to proceed Aspect");
//        new Client("localhost", 8848).start();
//        Object result = joinPoint.proceed();
//        System.out.println("End to proceed Aspect");
//        return result;
        TransactionRollback transactionRollback = TransactionRollback.getCurrentThreadLocal();
        String groupId = null;
        int maxTimeOut = 0;
        if(transactionRollback == null){
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = requestAttributes == null ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
            groupId = request == null ? null : request.getHeader("groupId");
            maxTimeOut = request == null?0:Integer.parseInt(request.getHeader("maxTimeOut"));
        }
        return aspectHandler.before(groupId,maxTimeOut,joinPoint);
    }
}
