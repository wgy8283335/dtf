package com.coconason.dtf.client.core.transaction;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @Author: Jason
 * @date: 2018/10/2-18:57
 */
public interface AspectInterface {
    Object before(String info,ProceedingJoinPoint point) throws Throwable;
}
