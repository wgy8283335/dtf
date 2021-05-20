package com.dtf.client.core.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Dtf Transaction Annotation.
 * 
 * @author wangguangyuan
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DtfTransaction {
    
    /**
     * There are three types of Dtf Transaction,as below: SYNC_FINAL SYNC_STRONG ASYNC_FINAL.
     * 
     * @return transaction type name
     */
    String type() default "SYNC_FINAL";
    
}
