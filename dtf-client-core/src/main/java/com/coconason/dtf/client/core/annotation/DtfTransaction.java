package com.coconason.dtf.client.core.annotation;

import java.lang.annotation.*;

/**
 * @Author Jason
 * @date 2018/7/23-12:39
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DtfTransaction{
    //There are three types of Dtf Transaction,as below: SYNC_FINAL SYNC_STRONG ASYNC_FINAL
    String type() default "SYNC_FINAL";

}
