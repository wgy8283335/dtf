package com.coconason.dtf.client.core.threadpools;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Thread pool running on the client.
 * 
 * @Author: Jason
 */
@Component
public final class ThreadPoolForClientProxy implements ExecutorService {

    /**
     * Thread pool.
     */
    private ExecutorService pool;
    
    @Autowired
    public ThreadPoolForClientProxy(final ThreadPoolForClientConfiguration threadPoolForClientConfiguration) {
        pool = new ThreadPoolExecutor(threadPoolForClientConfiguration.getCorePoolSize(), threadPoolForClientConfiguration.getMaximumPoolSize(), 
                threadPoolForClientConfiguration.getKeepAliveTime(), TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(threadPoolForClientConfiguration.getQueueSize()), 
                new DefaultThreadFactory("defaultThreadFactory"), new ThreadPoolExecutor.AbortPolicy());
    }
    
    /**
     * Execute the runnable in thread pool.
     * 
     * @param command runnable object
     */
    @Override
    public void execute(final Runnable command) {
        pool.execute(command);
    }
    
    @Override
    public void shutdown() {
        pool.shutdown();
    }
    
    @Override
    public List<Runnable> shutdownNow() {
        return pool.shutdownNow();
    }
    
    @Override
    public boolean isShutdown() {
        return pool.isShutdown();
    }
    
    @Override
    public boolean isTerminated() {
        return pool.isTerminated();
    }
    
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return pool.awaitTermination(timeout, unit);
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return pool.submit(task);
    }
    
    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return pool.submit(task, result);
    }
    
    @Override
    public Future<?> submit(final Runnable task) {
        return pool.submit(task);
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return pool.invokeAll(tasks);
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException {
        return pool.invokeAll(tasks, timeout, unit);
    }
    
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return pool.invokeAny(tasks);
    }
    
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return pool.invokeAny(tasks, timeout, unit);
    }
    
}
