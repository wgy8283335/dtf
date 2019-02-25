package com.coconason.dtf.manager.threadpools;

import com.coconason.dtf.manager.utils.PropertiesReader;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: Jason
 * @date: 2018/9/17-11:36
 */
public final class ThreadPoolForServerProxy implements ExecutorService {
    private ExecutorService pool;

    private ThreadPoolForServerProxy(Integer corePoolSize, Integer maximumPoolSize, Integer keepAliveTime, Integer capacity) {
        pool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<Runnable>(capacity),new DefaultThreadFactory("defaultThreadFactory"),new ThreadPoolExecutor.AbortPolicy());
    }

    public static ThreadPoolForServerProxy initialize() throws Exception{
        String classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        PropertiesReader propertiesReader = new PropertiesReader(classpath+"config.properties");
        ThreadPoolForServerProxy threadPoolForServerProxy = new ThreadPoolForServerProxy(
                Integer.valueOf(propertiesReader.getProperty("corePoolSize")),
                Integer.valueOf(propertiesReader.getProperty("maximumPoolSize")),
                Integer.valueOf(propertiesReader.getProperty("keepAliveTime")),
                Integer.valueOf(propertiesReader.getProperty("capacity")));
        return threadPoolForServerProxy;
    }

    @Override
    public void execute(Runnable runnable){
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
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return pool.awaitTermination(timeout,unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return pool.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return pool.submit(task,result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return pool.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return pool.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return pool.invokeAll(tasks,timeout,unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return pool.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return pool.invokeAny(tasks,timeout,unit);
    }
}
