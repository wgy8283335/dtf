package com.coconason.dtf.client.core.transaction;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Aspect before the join point.
 * 
 * @Author: Jason
 */
public interface AspectInterface {

    /**
     * Aspect before the join point.
     * 
     * @param info group information
     * @param point join point
     * @return result of method of the join point
     */
    Object before(String info, ProceedingJoinPoint point);
    
}
