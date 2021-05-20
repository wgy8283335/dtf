package com.dtf.client.core.threadpools;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ThreadPoolForClientProxyTest {
    
    @Test
    public void assertConstructor() throws NoSuchFieldException, IllegalAccessException {
        ThreadPoolForClientProxy proxy = createProxy();
        assertNotNull(proxy);
    }
    
    @Test
    public void assertExecute() throws NoSuchFieldException, IllegalAccessException , InterruptedException{
        ThreadPoolForClientProxy proxy = createProxy();
        AtomicInteger i = new AtomicInteger(1);
        proxy.execute(new TestRunnable(i));
        Thread.sleep(1000);
        assertEquals(2, i.get());
    }
    
    private ThreadPoolForClientProxy createProxy() throws NoSuchFieldException, IllegalAccessException {
        ThreadPoolForClientConfiguration configuration = createConfiguration();
        ThreadPoolForClientProxy result = new ThreadPoolForClientProxy(configuration);
        return result;
    }
    
    private ThreadPoolForClientConfiguration createConfiguration() throws NoSuchFieldException, IllegalAccessException {
        ThreadPoolForClientConfiguration result = new ThreadPoolForClientConfiguration();
        Field corePoolSize = result.getClass().getDeclaredField("corePoolSize");
        corePoolSize.setAccessible(true);
        corePoolSize.set(result, 100);
        Field maximumPoolSize = result.getClass().getDeclaredField("maximumPoolSize");
        maximumPoolSize.setAccessible(true);
        maximumPoolSize.set(result, 1000);
        Field keepAliveTime = result.getClass().getDeclaredField("keepAliveTime");
        keepAliveTime.setAccessible(true);
        keepAliveTime.set(result, 10);
        Field queueSize = result.getClass().getDeclaredField("queueSize");
        queueSize.setAccessible(true);
        queueSize.set(result, 150);
        return result;
    }
    
    private class TestRunnable implements Runnable {
        
        private AtomicInteger i;

        public TestRunnable(AtomicInteger i) {
            this.i = i;
        }

        @Override
        public void run() {
            i.incrementAndGet();
        }
    }
    
}
