package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageAsyncQueueProxy;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerFailingAsyncRequestRunnableTest {
    
    @Test
    public void testRun() {
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        MessageAsyncQueueProxy messageAsyncQueueProxy = new MessageAsyncQueueProxy();
        threadPool.execute(new ConsumerFailingAsyncRequestRunnable(messageAsyncQueueProxy));
    }
    
}
