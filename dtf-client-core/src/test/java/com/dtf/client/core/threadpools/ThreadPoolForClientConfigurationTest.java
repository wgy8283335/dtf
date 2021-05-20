package com.dtf.client.core.threadpools;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class ThreadPoolForClientConfigurationTest {
    
    @Test
    public void assertConfiguration() throws NoSuchFieldException, IllegalAccessException {
        ThreadPoolForClientConfiguration configuration = createConfiguration();
        assertEquals(configuration.getCorePoolSize().intValue(),100);
        assertEquals(configuration.getMaximumPoolSize().intValue(),1000);
        assertEquals(configuration.getKeepAliveTime().intValue(),10);
        assertEquals(configuration.getQueueSize().intValue(),150);
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
    
}
