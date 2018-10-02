package com.coconason.dtf.client.core.threadpools;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: Jason
 * @date: 2018/9/17-11:36
 */
@Component
public class ThreadPoolForClientProxy implements ExecutorService{

    private ExecutorService pool;

    @Autowired
    public ThreadPoolForClientProxy(ThreadPoolForClientConfiguration threadPoolForClientConfiguration) {
        pool = new ThreadPoolExecutor(threadPoolForClientConfiguration.getCorePoolSize(),threadPoolForClientConfiguration.getMaximumPoolSize(),threadPoolForClientConfiguration.getKeepAliveTime(), TimeUnit.MILLISECONDS,new LinkedBlockingDeque<Runnable>(threadPoolForClientConfiguration.getQueueSize()),new DefaultThreadFactory("defaultThreadFactory"),new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void execute(Runnable command) {
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
