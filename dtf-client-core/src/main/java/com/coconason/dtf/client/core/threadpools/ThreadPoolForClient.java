package com.coconason.dtf.client.core.threadpools;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jason
 * @date: 2018/9/17-11:36
 */
@Component
public class ThreadPoolForClient {

    @Autowired
    private ThreadPoolForClientConfiguration threadPoolForClientConfiguration;

    private ExecutorService pool;

    public ThreadPoolForClient() {
        pool = new ThreadPoolExecutor(threadPoolForClientConfiguration.getCorePoolSize(),threadPoolForClientConfiguration.getMaximumPoolSize(),threadPoolForClientConfiguration.getKeepAliveTime(), TimeUnit.MILLISECONDS,new LinkedBlockingDeque<Runnable>(threadPoolForClientConfiguration.getQueueSize()),new DefaultThreadFactory("defaultThreadFactory"),new ThreadPoolExecutor.AbortPolicy());
    }

    public void addTask(Runnable runnable){
        pool.execute(runnable);
    }
}
