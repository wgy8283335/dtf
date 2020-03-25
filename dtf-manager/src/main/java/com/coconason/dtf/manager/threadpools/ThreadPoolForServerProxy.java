package com.coconason.dtf.manager.threadpools;

import com.coconason.dtf.manager.utils.PropertiesReader;
import io.netty.util.concurrent.DefaultThreadFactory;

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
 * Thread pool. Proxy of ExecutorService.
 * 
 * @Author: Jason
 */
public final class ThreadPoolForServerProxy implements ExecutorService {
    
    /**
     * Thread pool.
     */
    private ExecutorService pool;
    
    private ThreadPoolForServerProxy(final Integer corePoolSize, final Integer maximumPoolSize, final Integer keepAliveTime, final Integer capacity) {
        pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(capacity), 
                new DefaultThreadFactory("defaultThreadFactory"), new ThreadPoolExecutor.AbortPolicy());
    }
    
    /**
     * Initialize thread pool for server proxy.
     * 
     * @param propertiesReader PropertiesReader
     * @return thread pool for server proxy
     */
    public static ThreadPoolForServerProxy initialize(final PropertiesReader propertiesReader) {
        ThreadPoolForServerProxy threadPoolForServerProxy = new ThreadPoolForServerProxy(
                Integer.valueOf(propertiesReader.getProperty("corePoolSize")),
                Integer.valueOf(propertiesReader.getProperty("maximumPoolSize")),
                Integer.valueOf(propertiesReader.getProperty("keepAliveTime")),
                Integer.valueOf(propertiesReader.getProperty("capacity")));
        return threadPoolForServerProxy;
    }
    
    @Override
    public void execute(final Runnable runnable) {
        pool.execute(runnable);
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
