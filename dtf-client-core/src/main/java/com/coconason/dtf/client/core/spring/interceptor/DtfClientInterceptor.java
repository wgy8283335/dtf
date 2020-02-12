package com.coconason.dtf.client.core.spring.interceptor;

import com.coconason.dtf.client.core.transaction.AspectInterface;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Dtf client interceptor.
 * 
 * @Author: Jason
 */
@Aspect
@Component
@Order(1)
final class DtfClientInterceptor {

    /**
     * Logger for DtfClientInterceptor class.
     */
    private Logger logger = LoggerFactory.getLogger(DtfClientInterceptor.class);
    
    @Autowired
    @Qualifier("aspectHandler")
    private AspectInterface aspectHandler;
    
    /**
     * Interceptor for restful client, before send request.
     * 
     * @param joinPoint join point
     * @return return the result of the intercepted method
     */
    @Around("@annotation(com.coconason.dtf.client.core.annotation.DtfTransaction)")
    public Object dtfTransactionExecute(final ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes == null ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
        String info = request == null ? null : request.getHeader("groupInfo");
        Object result = null;
        try {
            result = aspectHandler.before(info, joinPoint);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        } catch (Throwable error) {
            throw error;
        }
        return result;
    }
}
