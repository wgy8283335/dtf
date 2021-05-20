package com.dtf.manager.service;

import com.dtf.manager.cache.MessageAsyncQueueProxy;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecoverLogToQueueRunnableTest {
    
    @Test
    public void testRun() {
        MessageAsyncQueueProxy messageAsyncQueueProxy = new MessageAsyncQueueProxy();
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        threadPool.execute(new RecoverLogToQueueRunnable(messageAsyncQueueProxy));
    }
    
}
