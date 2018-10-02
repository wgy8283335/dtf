package com.coconason.dtf.manager.threadpools;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jason
 * @date: 2018/9/17-11:36
 */
public class ThreadPoolForServer {
    private ExecutorService pool;

    public ThreadPoolForServer(Integer corePoolSize,Integer maximumPoolSize,Integer keepAliveTime,Integer capacity) {
        pool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<Runnable>(capacity),new DefaultThreadFactory("defaultThreadFactory"),new ThreadPoolExecutor.AbortPolicy());
    }

    public void execute(Runnable runnable){
        pool.execute(runnable);
    }
}
